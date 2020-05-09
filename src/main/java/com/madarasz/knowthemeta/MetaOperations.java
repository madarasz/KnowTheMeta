package com.madarasz.knowthemeta;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DOs.User;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.AdminStampRepository;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Service
public class MetaOperations {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired ABRBroker abrBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardInPackRepository cardInPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired AdminStampRepository adminStampRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired MetaRepository metaRepository;
    @Autowired TournamentRepository tournamentRepository;
    @Autowired StandingRepository standingRepository;
    @Autowired UserRepository userRepository;
    @Autowired DeckRepository deckRepository;

    private static final Logger log = LoggerFactory.getLogger(MetaOperations.class);
    private static final StopWatch stopwatch = new StopWatch();

    @Transactional
    public void addMeta(String mwlCode, String packCode, Boolean newCards, String title) {
        // TODO: stop if meta exists with same name
        MWL mwl = mwlRepository.findByCode(mwlCode);
        CardPack cardPack = cardPackRepository.findByCode(packCode);
        if (mwl == null) log.error("No MWL found by code: " + mwlCode);
        if (cardPack == null) log.error("No CardPack found by code: " + packCode);
        Meta meta = new Meta(cardPack, mwl, newCards, title);
        metaRepository.save(meta);
        log.info(String.format("New meta added: %s (%s - %s)", title, packCode, mwlCode));
    }

    @Transactional
    public void deleteMeta(String title) {
        Meta meta = metaRepository.findByTitle(title);
        if (meta == null) {
            log.error("Meta not found: " + title);
        } else {
            log.info("Deleting meta: " + meta.getTitle());
            metaRepository.deleteMeta(title);
        }
    }

    @Transactional
    public String getMetaData(Meta meta) {
        stopwatch.start();
        int tournamentCreatedCount = 0;
        int standingCreatedCount = 0;
        int matchUpdatedCount = 0;
        int userCreatedCount = 0;
        int deckCreatedCount = 0;
        Long metaId = meta.getId();

        Set<CardInPack> identities = cardInPackRepository.listIdentities();
        Set<CardInPack> cards = cardInPackRepository.listAll();
        List<Tournament> existingTournaments = tournamentRepository.listForMeta(metaId);
        log.info("Existing tournaments for meta: "+existingTournaments.size());
        List<User> existingUsers = userRepository.listAll();
        Set<Deck> existingDecks = deckRepository.listAll();
        List<Tournament> tournaments = abrBroker.getTournamentData(meta);

        for (Tournament tournament : tournaments) {
            // tournament
            log.trace(String.format("Looking at tournament %s (#%d)", tournament.getTitle(), tournament.getId()));
            int tournamentId = tournament.getId();
            tournamentCreatedCount += updateTournaments(existingTournaments, tournament);
            // standings
            List<Standing> standings = abrBroker.getStadingData(tournament, identities, cards, existingDecks);
            Set<Standing> existingStandings = standingRepository.findByTournament(tournamentId);
            for (Standing standing : standings) {
                // decks
                if (standing.getDeck() != null) {
                    Deck deck = standing.getDeck();     
                    // player
                    userCreatedCount += updateWithPlayer(existingUsers, deck);
                    // deck
                    deckCreatedCount += updateWithDeck(existingDecks, standing, deck);
                }
                Optional<Standing> existingStanding = existingStandings.stream().filter(x -> x.getRank() == standing.getRank() && x.getIsRunner() == standing.getIsRunner()).findFirst();
                if (!existingStanding.isPresent()) {
                    // new standing
                    standingRepository.save(standing);
                    existingStandings.add(standing);
                    standingCreatedCount++;
                }
            }
            // matches
            if (tournament.isMatchDataAvailable()) {
                matchUpdatedCount += updateStandingsWithMatchData(tournamentId, existingStandings);
            }
        }
        // update counters
        updateMetaCounts(meta);

        // logging
        stopwatch.stop();
        String message = String.format("Meta update \"%s\" finished (%.3f sec) - New tournament: %d, new stading: %d, new deck: %d, new player: %d, match updates: %d", 
            meta.getTitle(), stopwatch.getTotalTimeSeconds(), tournamentCreatedCount, standingCreatedCount, deckCreatedCount, userCreatedCount, matchUpdatedCount);
        log.info(message);
        return message;
    }

    private int updateTournaments(List<Tournament> existingTournaments, Tournament tournament) {
        int tournamentId = tournament.getId();
        if (!existingTournaments.stream().filter(x -> x.getId() == tournamentId).findFirst().isPresent()) {
            // new tournament
            log.debug("New tournament saved: " + tournament.toString());
            tournamentRepository.save(tournament);
            existingTournaments.add(tournament);
            return 1;
        }
        return 0;
    }

    private int updateWithPlayer(List<User> existingUsers, Deck deck) {
        int userId = deck.getPlayer().getUser_id();
        Optional<User> existingPlayer = existingUsers.stream().filter(x -> x.getUser_id() == userId).findFirst();
        if (!existingPlayer.isPresent() && userId > 0) {
            User player = deck.getPlayer();
            userRepository.save(player);
            existingUsers.add(player);
            return 1;
        } 
        deck.setPlayer(existingPlayer.get());
        return 0;
    }

    private int updateWithDeck(Set<Deck> existingDecks, Standing standing, Deck deck) {
        int deckId = deck.getId();
        Optional<Deck> existingDeck = existingDecks.stream().filter(x -> x.getId() == deckId).findFirst();
        if (!existingDeck.isPresent()) {
            deckRepository.save(deck);
            existingDecks.add(deck);
            return 1;
        } 
        standing.setDeck(existingDeck.get());
        return 0;
    }

    private void updateMetaCounts(Meta meta) {
        Long metaId = meta.getId();
        meta.setTournamentCount(metaRepository.countTournaments(metaId));
        meta.setStandingsCount(metaRepository.countStandings(metaId));
        meta.setDecksPlayedCount(metaRepository.countDecks(metaId));
        meta.setMatchesCount(metaRepository.countMatches(metaId));
        meta.setLastUpdate(new Date());
        metaRepository.save(meta);
    }

    private int updateStandingsWithMatchData(int tournamentId, Set<Standing> existingStandings) {
        int matchUpdatedCount = 0;
        Set<Standing> matches = abrBroker.loadMatches(tournamentId);
        for (Standing match : matches) {
            // TODO: team tournaments with multiple players on same rank
            Optional<Standing> existingStanding = existingStandings.stream().filter(x -> x.getRank() == match.getRank() && x.getIsRunner() == match.getIsRunner()).findFirst();
            if (existingStanding.isPresent() && !existingStanding.get().areMatchDataIdentical(match)) {
                    Standing standing = existingStanding.get();
                    standing.copyFrom(match);
                    standingRepository.save(standing);
                    matchUpdatedCount++;
            }
        }
        return matchUpdatedCount;
    }
}