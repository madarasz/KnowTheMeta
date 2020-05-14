package com.madarasz.knowthemeta;

import java.util.List;
import java.util.stream.Collectors;

// import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.stats.CardStats;
import com.madarasz.knowthemeta.database.DOs.stats.MetaCards;
import com.madarasz.knowthemeta.database.DOs.stats.WinRateUsedCounter;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.WinRateUsedCounterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetaResults {
    @Autowired MetaRepository metaRepository;
    @Autowired MetaStatistics metaStatistics;
    @Autowired WinRateUsedCounterRepository winRateUsedCounterRepository;

    public MetaCards getCardResultsForMeta(String metaTitle) {
        Meta meta = metaRepository.findByTitle(metaTitle);

        if (!meta.getStatsCalculated()) {
            metaStatistics.calculateStats(metaTitle);
        }

        MetaCards result = new MetaCards(meta);
        // List<WinRateUsedCounter> factionStats = winRateUsedCounterRepository.listFactionStatsForMetaOrdered(metaTitle);
        // List<WinRateUsedCounter> runnerFactionStats = factionStats.stream().filter(x -> ((Faction)x.getStatAbout())
        List<WinRateUsedCounter> idStats = winRateUsedCounterRepository.listIDStatsForMetaOrdered(metaTitle);
        // filter out neutral IDs - TODO: don't save in the first place
        idStats = idStats.stream().filter(x -> !((Card)x.getStatAbout()).getFaction().getFactionCode().contains("neutral")).collect(Collectors.toList());
        List<WinRateUsedCounter> runnerIdStats = idStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("runner"))
            .collect(Collectors.toList());
        List<WinRateUsedCounter> corpIdStats = idStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("corp"))
            .collect(Collectors.toList());
        CardStats idStat = new CardStats();
        idStat.setRunner(runnerIdStats);
        idStat.setCorp(corpIdStats);
        result.setIdentities(idStat);
        return result;
    }
}