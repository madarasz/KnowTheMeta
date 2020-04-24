package com.madarasz.knowthemeta;

import java.util.Set;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
public class Operations {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;

    private static final Logger log = LoggerFactory.getLogger(Operations.class);
    private static final StopWatch stopwatch = new StopWatch();

    public void updateFromNetrunnerDB() {
        this.updateCycles();
        this.updatePacks();
        netrunnerDBBroker.loadCards();
    }

    @Transactional
    private void updatePacks() {
        Set<CardPack> packs = netrunnerDBBroker.loadPacks();
        int createCount = 0;
        stopwatch.start();

        for (CardPack cardPack : packs) {
            CardPack found = cardPackRepository.findByCode(cardPack.getCode());
            if (found == null) {
                cardPackRepository.save(cardPack);
                log.debug("New pack: " + cardPack.getName());
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
    }

    @Transactional
    private void updateCycles() {
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        int updateCount = 0;
        int createCount = 0;
        stopwatch.start();

        for (CardCycle cardCycle : cycles) {
            CardCycle found = cardCycleRepository.findByCode(cardCycle.getCode());
            if (found == null) {
                cardCycleRepository.save(cardCycle);
                log.debug("New cycle: " + cardCycle.getName());
                createCount++;
            } else {
                if (!found.equals(cardCycle)) {
                    // TODO: update
                    updateCount++;
                    log.error("UPDATE NOT IMPLEMENTED");
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
    }

}