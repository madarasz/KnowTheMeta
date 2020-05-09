package com.madarasz.knowthemeta;

import java.util.Date;
import java.util.List;
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
import com.madarasz.knowthemeta.helper.Searcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * Class for operations related to Meta entities.
 */
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
    @Autowired Searcher searcher;

    private static final Logger log = LoggerFactory.getLogger(MetaOperations.class);
    private static final StopWatch stopwatch = new StopWatch();
    private int tournamentCreatedCount;
    private int standingCreatedCount;
    private int matchUpdatedCount ;
    private int userCreatedCount;
    private int deckCreatedCount;

    
    /** 
     * Adds Meta entity
     * @param mwlCode mwlCode for MWL
     * @param packCode packCode of cardpool
     * @param newCards does meta contain new cards
     * @param title meta title
     */
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

    
    /**
     * Deletes meta with that title  
     * @param title
     */
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

    
    /**
     * Loads tournament, standing, deck, player and match data for a meta and persists it into DB. 
     * @param meta
     * @return String update message
     */
    @Transactional
    public String getMetaData(Meta meta) {
        // init
        stopwatch.start();
        tournamentCreatedCount = 0;
        standingCreatedCount = 0;
        matchUpdatedCount = 0;
        userCreatedCount = 0;
        deckCreatedCount = 0;

        // load data
        Set<CardInPack> identities = cardInPackRepository.listIdentities();
        Set<CardInPack> cards = cardInPackRepository.listAll();
        List<Tournament> existingTournaments = tournamentRepository.listForMeta(meta.getId());
        List<User> existingUsers = userRepository.listAll();
        Set<Deck> existingDecks = deckRepository.listAll();
        List<Tournament> tournaments = abrBroker.getTournamentData(meta);
        log.info("*** Starting meta update for " + meta.getTitle());
        log.info(String.format("Existing tournaments for meta: %d", existingTournaments.size()));

        // iterate on ABR data
        for (Tournament tournament : tournaments) {
            // tournament
            log.trace(String.format("Looking at tournament %s (#%d)", tournament.getTitle(), tournament.getId()));
            updateTournaments(existingTournaments, tournament);
            // standings
            updateStandings(tournament, identities, cards, existingDecks, existingUsers);
        }
        // update meta counters
        updateMetaCounts(meta);

        // logging
        stopwatch.stop();
        String message = String.format("Meta update \"%s\" finished (%.3f sec) - New tournament: %d, new stading: %d, new deck: %d, new player: %d, match updates: %d", 
            meta.getTitle(), stopwatch.getTotalTimeSeconds(), tournamentCreatedCount, standingCreatedCount, deckCreatedCount, userCreatedCount, matchUpdatedCount);
        log.info("*** " + message);
        return message;
    }

    
    /** 
     * Saves tournament to DB if it did not exist there. Increments tournamentCreatedCount counter.
     * @param existingTournaments list of existing tournaments in DB
     * @param tournament tournament in question
     */
    private void updateTournaments(List<Tournament> existingTournaments, Tournament tournament) {
        int tournamentId = tournament.getId();
        if (!existingTournaments.stream().filter(x -> x.getId() == tournamentId).findFirst().isPresent()) {
            // new tournament
            log.debug("New tournament saved: " + tournament.toString());
            tournamentRepository.save(tournament);
            existingTournaments.add(tournament);
            tournamentCreatedCount++;
        }
    }

    
    /** 
     * @param tournament
     * @param identities
     * @param cards
     * @param existingDecks
     * @param existingUsers
     */
    private void updateStandings(Tournament tournament, Set<CardInPack> identities, Set<CardInPack> cards, Set<Deck> existingDecks, List<User> existingUsers) {
        int tournamentId = tournament.getId();
        List<Standing> standings = abrBroker.getStadingData(tournament, identities, cards, existingDecks);
        Set<Standing> existingStandings = standingRepository.findByTournament(tournamentId);
        for (Standing standing : standings) {
            // decks
            if (standing.getDeck() != null) {
                Deck deck = standing.getDeck();     
                // player
                updateDeckWithPlayer(existingUsers, deck);
                // deck
                updateStandingWithDeck(existingDecks, standing, deck);
            }
            Standing existingStanding = searcher.getStadingByRankSide(existingStandings, standing.getRank(), standing.getIsRunner());
            if (existingStanding == null) {
                // new standing
                standingRepository.save(standing);
                existingStandings.add(standing);
                standingCreatedCount++;
            }
        }
        // matches
        if (tournament.isMatchDataAvailable()) {
            updateStandingsWithMatchData(tournamentId, existingStandings);
        }
    }

    
    /**
     * Updates deck with its user. 
     * Checks if user exists in DB, if not, it creates it.
     * Increments userCreatedCount counter. 
     * @param existingUsers list of existing users in DB
     * @param deck deck in question
     */
    private void updateDeckWithPlayer(List<User> existingUsers, Deck deck) {
        int userId = deck.getPlayer().getUser_id();
        User existingPlayer = searcher.getUserById(existingUsers, userId);
        if (existingPlayer == null && userId > 0) { // undefined users have userID=0 in JSON
            User player = deck.getPlayer();
            userRepository.save(player);
            existingUsers.add(player);
            userCreatedCount++;
        } else {
            deck.setPlayer(existingPlayer);
        }
    }

    
    /**
     * Updates standing with its deck.
     * Checks if deck is in DB, if not, it creates it.
     * Increaments deckCreatedCount counter.
     * @param existingDecks list of existing decks in db
     * @param standing related stading of deck
     * @param deck deck in question
     */
    private void updateStandingWithDeck(Set<Deck> existingDecks, Standing standing, Deck deck) {
        Deck existingDeck = searcher.getDeckById(existingDecks, deck.getId());
        if (existingDeck == null) {
            deckRepository.save(deck);
            existingDecks.add(deck);
            deckCreatedCount++;
        } else {
            standing.setDeck(existingDeck);
        }
    }


    /** 
     * Updates tournament stadings in DB with their match data coming from ABR.
     * @param tournamentId id of tournament in question
     * @param existingStandings list of existing stadings of the tournament in DB.
     */
    private void updateStandingsWithMatchData(int tournamentId, Set<Standing> existingStandings) {
        Set<Standing> matches = abrBroker.loadMatches(tournamentId);
        for (Standing match : matches) {
            // Pair match with standings only if there is one match. (Team tournaments are excluded)
            if (searcher.countStadingsByRankSide(existingStandings, match.getRank(), match.getIsRunner()) == 1) {
                Standing standing = searcher.getStadingByRankSide(existingStandings, match.getRank(), match.getIsRunner());
                // update stading if existing match data is different
                if (!standing.areMatchDataIdentical(match)) {
                        standing.copyFrom(match);
                        standingRepository.save(standing);
                        matchUpdatedCount++;
                }
            }
        }
    }

    
    /** 
     * Updates counters of meta in DB.
     * @param meta meta in question
     */
    private void updateMetaCounts(Meta meta) {
        Long metaId = meta.getId();
        meta.setTournamentCount(metaRepository.countTournaments(metaId));
        meta.setStandingsCount(metaRepository.countStandings(metaId));
        meta.setDecksPlayedCount(metaRepository.countDecks(metaId));
        meta.setMatchesCount(metaRepository.countMatches(metaId));
        meta.setLastUpdate(new Date());
        metaRepository.save(meta);
    }
}