package com.madarasz.knowthemeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DOs.User;
import com.madarasz.knowthemeta.database.DOs.admin.AdminStamp;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.AdminStampRepository;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.DeckRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.StandingRepository;
import com.madarasz.knowthemeta.database.DRs.TournamentRepository;
import com.madarasz.knowthemeta.database.DRs.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
public class Operations {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired ABRBroker abrBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired AdminStampRepository adminStampRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired MetaRepository metaRepository;
    @Autowired TournamentRepository tournamentRepository;
    @Autowired StandingRepository standingRepository;
    @Autowired UserRepository userRepository;
    @Autowired DeckRepository deckRepository;

    private static final Logger log = LoggerFactory.getLogger(Operations.class);
    private static final StopWatch stopwatch = new StopWatch();
    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final String MESSAGE_NOT_HAPPENED_YET = "not happened yet";

    // for injecting mock during unit/integration tests
    public void setNetrunnerDBBroker(NetrunnerDBBroker mock) {
        this.netrunnerDBBroker = mock;
    }

    @Transactional
    public String getTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            return MESSAGE_NOT_HAPPENED_YET;
        }
        return dateTimeFormatter.format(adminEntry.getTimestamp());
    }

    @Transactional
    public void setTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            adminEntry = new AdminStamp(entry, new Date());
        } else {
            adminEntry.setTimestamp(new Date());
        }
        adminStampRepository.save(adminEntry);
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
        stopwatch.start();
        int newCount = 0;
        int editCount = 0;
        List<CardInPack> existingCards = cardRepository.listCardInPack();
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
        stopwatch.start();
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        int updateCount = 0;
        int createCount = 0;

        for (CardCycle cardCycle : cycles) {
            CardCycle found = cardCycleRepository.findByCode(cardCycle.getCode()); // TODO without DB
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
        return cycles;
    }

    @Transactional
    private void updateMWLs(List<CardInPack> cards) {
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

    @Transactional
    public String getMetaData(Meta meta) {
        stopwatch.start();
        int tournamentCreatedCount = 0;
        int standingCreatedCount = 0;
        int userCreatedCount = 0;
        int deckCreatedCount = 0;
        Long metaId = meta.getId();

        List<CardInPack> identities = cardRepository.listIdentities();
        List<Tournament> existingTournaments = tournamentRepository.listForMeta(metaId);
        log.info("Existing tournaments for meta: "+existingTournaments.size());
        List<User> existingUsers = userRepository.listAll();
        List<Deck> existingDecks = deckRepository.listAll();
        List<Tournament> tournaments = abrBroker.getTournamentData(meta);

        for (Tournament tournament : tournaments) {
            // tournament
            int tournamentId = tournament.getId();
            if (!existingTournaments.stream().filter(x -> x.getId() == tournamentId).findFirst().isPresent()) {
                // new tournament
                log.debug("New tournament saved: " + tournament.toString());
                tournamentRepository.save(tournament);
                existingTournaments.add(tournament);
                tournamentCreatedCount++;
            }
            // standings
            List<Standing> standings = abrBroker.getStadingData(tournament, identities, existingDecks);
            for (Standing standing : standings) {
                // decks
                if (standing.getDeck() != null) {
                    Deck deck = standing.getDeck();     
                    // player
                    int userId = deck.getPlayer().getUser_id();
                    Optional<User> existingPlayer = existingUsers.stream().filter(x -> x.getUser_id() == userId).findFirst();
                    if (!existingPlayer.isPresent()) {
                        User player = deck.getPlayer();
                        userRepository.save(player);
                        existingUsers.add(player);
                        userCreatedCount++;
                    } else {
                        deck.setPlayer(existingPlayer.get());
                    }
                    // deck
                    int deckId = deck.getId();
                    Optional<Deck> existingDeck = existingDecks.stream().filter(x -> x.getId() == deckId).findFirst();
                    if (!existingDeck.isPresent()) {
                        deckRepository.save(deck);
                        existingDecks.add(deck);
                        deckCreatedCount++;
                    } else {
                        standing.setDeck(existingDeck.get());
                    }
                }
                // TODO: without DB
                Standing existingStanding = standingRepository.findByTournamentSideRank(tournament.getId(), standing.getIsRunner(), standing.getRank());
                if (existingStanding == null) {
                    // new standing
                    standingRepository.save(standing);
                    standingCreatedCount++;
                }

            }
        }
        // update counts
        meta.setTournamentCount(metaRepository.countTournaments(metaId));
        meta.setStandingsCount(metaRepository.countStandings(metaId));
        meta.setDecksPlayedCount(metaRepository.countDecks(metaId));
        meta.setLastUpdate(new Date());
        metaRepository.save(meta);

        // logging
        stopwatch.stop();
        String message = String.format("Meta update \"%s\" finished (%.3f sec) - New tournament: %d, new stading: %d, new deck: %d, new player: %d", meta.getTitle(), 
            stopwatch.getTotalTimeSeconds(), tournamentCreatedCount, standingCreatedCount, deckCreatedCount, userCreatedCount);
        log.info(message);
        return message;
    }

    @Transactional
    public void dropDB() {
        // TODO
    }
}