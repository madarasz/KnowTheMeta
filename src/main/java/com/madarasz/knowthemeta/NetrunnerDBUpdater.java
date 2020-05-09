package com.madarasz.knowthemeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Service
public class NetrunnerDBUpdater {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired CardInPackRepository cardInPackRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired MWLRepository mwlRepository;
    private static final Logger log = LoggerFactory.getLogger(NetrunnerDBUpdater.class);

    // for injecting mock during unit/integration tests
    public void setNetrunnerDBBroker(NetrunnerDBBroker mock) {
        this.netrunnerDBBroker = mock;
    }

    public double updateFromNetrunnerDB() {
        log.info("Starting NetrunnerDB update");
        StopWatch netrunnerTimer = new StopWatch();
        netrunnerTimer.start();

        Set<CardCycle> cycles = this.updateCycles();
        Set<CardPack> packs = this.updatePacks(cycles);
        List<CardInPack> cards = this.updateCards(packs);  
        this.updateMWLs(cards);

        netrunnerTimer.stop();
        log.info(String.format("Finished NetrunnerDB update (%.3f sec)", netrunnerTimer.getTotalTimeSeconds()));
        return netrunnerTimer.getTotalTimeSeconds();
    }

    @Transactional
    private List<CardInPack> updateCards(Set<CardPack> packs) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        int newCount = 0;
        int editCount = 0;
        List<CardInPack> existingCards = new ArrayList<CardInPack>(cardInPackRepository.listAll());
        List<CardInPack> cards = netrunnerDBBroker.loadCards(packs);

        for (CardInPack cardInPack : cards) {
            String title = cardInPack.getCard().getTitle();
            CardPack pack = cardInPack.getCardPack();
            String code = cardInPack.getCode();    

            Optional<CardInPack> cardExists = existingCards.stream().filter(x -> x.getCard().getTitle().equals(title)).findFirst();
            if (!cardExists.isPresent()) {
                // new card
                Card card = cardInPack.getCard();
                pack.addCards(cardInPack);
                cardPackRepository.save(pack);
                existingCards.add(cardInPack);
                newCount++;
                log.debug(String.format("New card: %s - %s", card.getTitle(), pack.getName()));
            } else {
                // card exists
                Optional<CardInPack> printExists = existingCards.stream().filter(x -> x.getCode().equals(code)).findFirst();
                if (!printExists.isPresent()) {
                    // new reprint
                    Card card = cardExists.get().getCard();
                    CardInPack reprint = new CardInPack(card, pack, code, cardInPack.getImage_url());
                    pack.addCards(reprint);
                    cardPackRepository.save(pack);
                    existingCards.add(reprint);
                    editCount++;
                    log.debug(String.format("New reprint: %s - %s", card.getTitle(), pack.getName()));
                }
            }
        }

        // logging
        stopwatch.stop();
        if (newCount + editCount == 0) {
            log.info(String.format("Cards: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cards: %d new cards, %d reprints (%.3f sec)", newCount, editCount,
                    stopwatch.getTotalTimeSeconds()));
        }
        return existingCards;
    }

    @Transactional
    private Set<CardPack> updatePacks(Set<CardCycle> cycles) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        Set<CardPack> packs = netrunnerDBBroker.loadPacks(cycles);
        int createCount = 0;

        for (CardPack cardPack : packs) {
            CardPack found = cardPackRepository.findByCode(cardPack.getCode()); // TODO: without DB
            if (found == null) {
                cardPackRepository.save(cardPack);
                CardCycle cardCycle = cardPack.getCycle();
                cardCycle.addPack(cardPack);
                cardCycleRepository.save(cardCycle);
                log.debug(String.format("New pack: %s (cycle: %s)", cardPack.getName(), cardPack.getCycle().getName()));
                createCount++;
            } 
        }

        // logging
        stopwatch.stop();
        if (createCount == 0) {
            log.info(String.format("Cardpacks: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cardpacks: %d added (%.3f sec)", createCount, stopwatch.getTotalTimeSeconds()));
        }
        return packs;
    }

    @Transactional
    private Set<CardCycle> updateCycles() {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        Set<CardCycle> existingCycles = cardCycleRepository.findall();
        int updateCount = 0;
        int createCount = 0;

        for (CardCycle cardCycle : cycles) {
            Optional<CardCycle> found = existingCycles.stream().filter(x -> x.getCode().equals(cardCycle.getCode())).findFirst();
            if (!found.isPresent()) {
                cardCycleRepository.save(cardCycle);
                existingCycles.add(cardCycle);
                log.debug("New cycle: " + cardCycle.getName());
                createCount++;
            } else {
                CardCycle foundCycle = found.get();
                if (!foundCycle.equals(cardCycle)) {
                    foundCycle.copyFrom(cardCycle);
                    cardCycleRepository.save(foundCycle);
                    log.debug("Updated cycle: " + foundCycle.getName());
                    updateCount++;
                }
            }
        }

        // logging
        stopwatch.stop();
        if (updateCount + createCount == 0) {
            log.info(String.format("Cardcycles: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cardcycles: %d added, %d updated (%.3f sec)", createCount, updateCount, stopwatch.getTotalTimeSeconds()));
        }
        return cycles;
    }

    @Transactional
    private void updateMWLs(List<CardInPack> cards) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        Set<MWL> mwls = netrunnerDBBroker.loadMWL(cards);
        int createCount = 0;
        int updateCount = 0;

        for (MWL mwl : mwls) {
            MWL existing = mwlRepository.findByCode(mwl.getCode());
            if (existing == null) {
                // add MWL
                mwlRepository.save(mwl);
                createCount++;
            } else {
                // update if active property is different
                if (existing.getActive() != mwl.getActive()) {
                    existing.setActive(mwl.getActive());
                    mwlRepository.save(existing);
                    updateCount++;
                }
            }
        }

        // logging
        stopwatch.stop();
        if (updateCount + createCount == 0) {
            log.info(String.format("MWL: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("MWL: %d added, %d updated (%.3f sec)", createCount, updateCount, stopwatch.getTotalTimeSeconds()));
        }
    }
}