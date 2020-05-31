package com.madarasz.knowthemeta;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.stats.WinRateUsedCounter;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.StandingRepository;
import com.madarasz.knowthemeta.database.DRs.WinRateUsedCounterRepository;
import com.madarasz.knowthemeta.helper.Searcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class MetaStatistics {
    @Autowired CardRepository cardRepository;
    @Autowired MetaRepository metaRepository;
    @Autowired StandingRepository standingRepository;
    @Autowired WinRateUsedCounterRepository winRateUsedCounterRepository;
    @Autowired Searcher searcher;
    private static final Logger log = LoggerFactory.getLogger(MetaStatistics.class);
    private static final double minimumCardPopularity = 0.05; // cards won't be tagged under this popularity
    private static final double minimumAdditionalWinrate = 0.12; // cards wont't be tagged as winning if they do not get at least faction_winrate+additional
    private static final List<String> pseudoBreakers = new ArrayList<String>(Arrays.asList("Always Be Running", "Boomerang", "D4v1d", "e3 Feedback Implants", "Gbahali", 
        "Grappling Hook", "Kongamato"));


    public void calculateStats(String metaTitle) {
        log.info("Calculating statistics for meta " + metaTitle);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // gather data
        Meta meta = metaRepository.findByTitle(metaTitle);
        Set<Standing> standings = standingRepository.findByMeta(metaTitle);
        Set<Card> identities = standings.stream().map(x -> x.getIdentity()).collect(Collectors.toSet());
        Set<Card> cardsInPack = cardRepository.listForPack(meta.getCardpool().getCode());
        Set<Card> cards = cardRepository.findByMeta(metaTitle);
        Set<Faction> factions = standings.stream().map(x -> x.getIdentity().getFaction()).collect(Collectors.toSet());
        factions = factions.stream().filter(x -> !x.getFactionCode().contains("neutral")).collect(Collectors.toSet()); // filter out neutral factions
        Set<WinRateUsedCounter> existingCardStats = winRateUsedCounterRepository.listCardStatsForMeta(metaTitle);
        Set<WinRateUsedCounter> existingFactionStats = winRateUsedCounterRepository.listFactionStatsForMeta(metaTitle);

        // get side winrates
        final int runnerWins = standingRepository.countRunnerWinsInMeta(metaTitle);
        final int runnerLosses = standingRepository.countRunnerLossesInMeta(metaTitle);
        final int runnerDraws = standingRepository.countRunnerDrawsInMeta(metaTitle);
        final int allRunnerMatches = runnerWins + runnerLosses + runnerDraws;
        final double runnerWinrate = (double)runnerWins / allRunnerMatches;
        final double corpWinrate = (double)runnerLosses / allRunnerMatches;
        log.debug(String.format("Runner wins: %d, losses: %d, draws: %d", runnerWins, runnerLosses, runnerDraws));
        meta.setRunnerWinRate(runnerWinrate);
        meta.setCorpWinRate(corpWinrate);

        // iterate on factions
        log.debug("Factions found: " + factions.size());
        for (Faction faction : factions) {
            WinRateUsedCounter tempStat = factionStatsFromStandings(standings, faction, meta);
            WinRateUsedCounter factionStat = searcher.getStatsByFactionName(existingFactionStats, faction.getName());
            if (factionStat == null) {
                // new stat
                factionStat = tempStat;
            } else {
                // update existing stat
                factionStat.copyFrom(tempStat);
            }
            winRateUsedCounterRepository.save(factionStat);
            log.debug(factionStat.toString());
        }
        
        // iterate on identities
        log.debug("IDs found: " + identities.size());
        for (Card identity : identities) {
            WinRateUsedCounter tempStat = idStatsFromStandings(standings, identity, meta);
            WinRateUsedCounter idStat = searcher.getStatsByCardTitle(existingCardStats, identity.getTitle());
            if (idStat == null) {
                // new stat
                idStat = tempStat;
            } else {
                // update existing stat
                idStat.copyFrom(tempStat);
            }
            winRateUsedCounterRepository.save(idStat); 
        }

        // iterate on non-ID cards
        final int runnerDeckCount = meta.getRunnerDecksCount();
        final int corpDeckCount = meta.getCorpDecksCount();
        log.debug(String.format("Cards found: %d / %d - decks: %d", cards.size(), cardRepository.count(), runnerDeckCount));
        for (Card card : cards) {
            final String cardTitle = card.getTitle();
            final Set<Standing> cardStandings = standingRepository.findByMetaAndCard(metaTitle, cardTitle);
            
            int winCount = 0;
            int drawCount = 0;
            int lossCount = 0;
            int perDeckCount = 0;
            final int deckCount = card.getSide_code().equals("runner") ? runnerDeckCount : corpDeckCount;
            final double factionWinRate = card.getSide_code().equals("runner") ? runnerWinrate : corpWinrate;
            List<String> tags = new ArrayList<String>();
            for (Standing standing : cardStandings) {
                winCount += standing.getWinCount();
                drawCount += standing.getDrawCount();
                lossCount += standing.getLossCount();
                perDeckCount += standing.getDeck().getCards().stream()
                    .filter(x -> x.getCard().getTitle().equals(cardTitle)).findFirst().get().getQuantity();
            }
            final float popularity = ((float)cardStandings.size()) / deckCount;
            final double winrate = (double)winCount/(winCount+drawCount+lossCount)*100.0;
            // add tags
            if (popularity > minimumCardPopularity) {
                // popular in pack
                if (cardsInPack.stream().filter(x -> x.getTitle().equals(cardTitle)).findFirst().isPresent()) {
                    tags.add("popular-in-pack");
                }
                // winning
                if (winrate > (factionWinRate + minimumAdditionalWinrate) * 100) {
                    tags.add("winning");
                }
                // icebreaker
                if (card.getKeywords() != null && card.getKeywords().contains("Icebreaker")) {
                    String subtype = card.getKeywords().split(" - ").length > 1 ? card.getKeywords().split(" - ")[1] : card.getKeywords();
                    tags.add("icebreaker-" + subtype);
                }
                // pseudo icebreaker
                if (pseudoBreakers.stream().anyMatch(cardTitle::equals)) {
                    tags.add("icebreaker-" + card.getType_code());
                }
                // ICE
                if (card.getType_code().equals("ice")) {
                    String subtype = card.getKeywords() == null ? "" : "-" + card.getKeywords().split(" - ")[0];
                    tags.add("ice" + subtype);
                }
            }
            
            // save/update
            WinRateUsedCounter cardStat = searcher.getStatsByCardTitle(existingCardStats, cardTitle);
            if (cardStat == null) {
                cardStat = new WinRateUsedCounter(meta, card);
            }
            cardStat.setAvgPerDeck((float)perDeckCount / cardStandings.size());
            cardStat.setTags(String.join(",", tags));
            cardStat.setUsedCounter(cardStandings.size());
            cardStat.setWinCounter(winCount);
            cardStat.setDrawCounter(drawCount);
            cardStat.setLossCounter(lossCount);
            winRateUsedCounterRepository.save(cardStat);
        }

        meta.setStatsCalculated(true);
        metaRepository.save(meta);
        stopWatch.stop();
        log.info(String.format("Meta statistics calculation finished (%.3f sec)", stopWatch.getTotalTimeSeconds()));
    }

    private WinRateUsedCounter idStatsFromStandings(Set<Standing> standings, Card card, Meta meta) {
        String cardTitle = card.getTitle();
        Set<Standing> filteredSet = standings.stream().filter(x -> x.getIdentity().getTitle().equals(cardTitle)).collect(Collectors.toSet());
        return statsFromStandings(card, meta, filteredSet);
    }

    private WinRateUsedCounter factionStatsFromStandings(Set<Standing> standings, Faction faction, Meta meta) {
        String factionName = faction.getName();
        Set<Standing> filteredSet = standings.stream().filter(x -> x.getIdentity().getFaction().getName().equals(factionName)).collect(Collectors.toSet());
        return statsFromStandings(faction, meta, filteredSet);
    }

    private WinRateUsedCounter statsFromStandings(Entity entity, Meta meta, Set<Standing> filteredSet) {
        int winCounter = filteredSet.stream().map(x -> x.getWinCount()).reduce(0, Integer::sum);
        int drawCounter = filteredSet.stream().map(x -> x.getDrawCount()).reduce(0, Integer::sum);
        int lossCounter = filteredSet.stream().map(x -> x.getLossCount()).reduce(0, Integer::sum);
        int usedCounter = filteredSet.size();
        return new WinRateUsedCounter(winCounter, drawCounter, lossCounter, usedCounter, meta, entity);
    }
}