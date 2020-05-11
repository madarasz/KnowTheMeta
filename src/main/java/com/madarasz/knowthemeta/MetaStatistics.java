package com.madarasz.knowthemeta;

import java.util.Set;
import java.util.stream.Collectors;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.stats.WinRateUsedCounter;
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
    @Autowired MetaRepository metaRepository;
    @Autowired StandingRepository standingRepository;
    @Autowired WinRateUsedCounterRepository winRateUsedCounterRepository;
    @Autowired Searcher searcher;
    private static final Logger log = LoggerFactory.getLogger(MetaStatistics.class);


    public void calculateStats(String metaTitle) {
        log.info("Calculating statistics for meta " + metaTitle);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // gather data
        Meta meta = metaRepository.findByTitle(metaTitle);
        Set<Standing> standings = standingRepository.findByMeta(metaTitle);
        Set<Card> identities = standings.stream().map(x -> x.getIdentity()).collect(Collectors.toSet());
        Set<Faction> factions = standings.stream().map(x -> x.getIdentity().getFaction()).collect(Collectors.toSet());
        factions = factions.stream().filter(x -> !x.getFactionCode().contains("neutral")).collect(Collectors.toSet()); // filter out neutral factions
        Set<WinRateUsedCounter> existingIDStats = winRateUsedCounterRepository.listIDStatsForMeta(metaTitle);
        Set<WinRateUsedCounter> existingFactionStats = winRateUsedCounterRepository.listFactionStatsForMeta(metaTitle);

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
            WinRateUsedCounter idStat = searcher.getStatsByCardTitle(existingIDStats, identity.getTitle());
            if (idStat == null) {
                // new stat
                idStat = tempStat;
            } else {
                // update existing stat
                idStat.copyFrom(tempStat);
            }
            winRateUsedCounterRepository.save(idStat); 
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