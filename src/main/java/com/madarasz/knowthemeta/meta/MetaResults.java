package com.madarasz.knowthemeta.meta;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.stats.SideStats;
import com.madarasz.knowthemeta.database.DOs.stats.CardStats;
import com.madarasz.knowthemeta.database.DOs.stats.DeckIdentity;
import com.madarasz.knowthemeta.database.DOs.stats.DeckSideStats;
import com.madarasz.knowthemeta.database.DOs.stats.MetaCards;
import com.madarasz.knowthemeta.database.DOs.stats.WinRateUsedCounter;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.DeckIdentityRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.WinRateUsedCounterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Generating meta data output for endpoints.
 */
@Service
public class MetaResults {
    @Autowired MetaRepository metaRepository;
    @Autowired MetaStatistics metaStatistics;
    @Autowired WinRateUsedCounterRepository winRateUsedCounterRepository;
    @Autowired CardInPackRepository cardInPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired DeckIdentityRepository deckIdentityRepository;

    public MetaCards getCardResultsForMeta(String metaTitle) {
        Meta meta = metaRepository.findByTitle(metaTitle);

        if (!meta.getStatsCalculated()) {
            metaStatistics.calculateStats(metaTitle);
        }

        MetaCards result = new MetaCards(meta);
        List<WinRateUsedCounter> factionStats = winRateUsedCounterRepository.listFactionStatsForMetaOrdered(metaTitle);
        List<WinRateUsedCounter> runnerFactionStats = factionStats.stream().filter(x -> ((Faction)x.getStatAbout()).isRunner())
            .collect(Collectors.toList());
        List<WinRateUsedCounter> corpFactionStats = factionStats.stream().filter(x -> !((Faction)x.getStatAbout()).isRunner())
            .collect(Collectors.toList());
        List<WinRateUsedCounter> idStats = winRateUsedCounterRepository.listIDStatsForMetaOrdered(metaTitle);
        Set<WinRateUsedCounter> cardStats = winRateUsedCounterRepository.listNonIDCardStatsForMeta(metaTitle);        
        System.out.println("cardstats:" + cardStats.size());
        // filter out neutral IDs
        idStats = idStats.stream().filter(x -> !((Card)x.getStatAbout()).getFaction().getFactionCode().contains("neutral")).collect(Collectors.toList());

        // filter
        List<WinRateUsedCounter> runnerIdStats = idStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("runner"))
            .collect(Collectors.toList());
        List<WinRateUsedCounter> corpIdStats = idStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("corp"))
            .collect(Collectors.toList());
        List<WinRateUsedCounter> runnerCardStats = cardStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("runner"))
            .collect(Collectors.toList());
        List<WinRateUsedCounter> corpCardStats = cardStats.stream().filter(x -> ((Card)x.getStatAbout()).getSide_code().equals("corp"))
            .collect(Collectors.toList());

        // set values
        SideStats idStat = new SideStats();
        SideStats cardStat = new SideStats();
        SideStats factionStat = new SideStats();
        idStat.setRunner(runnerIdStats);
        idStat.setCorp(corpIdStats);
        result.setIdentities(idStat);
        cardStat.setRunner(runnerCardStats);
        cardStat.setCorp(corpCardStats);
        result.setCards(cardStat);
        factionStat.setRunner(runnerFactionStats);
        factionStat.setCorp(corpFactionStats);
        result.setFactions(factionStat);
        return result;
    }

    public CardStats getCardStats(String cardcode) {
        Card card = cardRepository.findByCode(cardcode);
        List<CardInPack> prints = cardInPackRepository.findAllByTitle(card.getTitle());
        CardPack firstPrintPack = prints.get(prints.size()-1).getCardPack();
        CardStats cardStats = new CardStats();
        cardStats.setCard(card);
        cardStats.setPrints(prints);
        List<Meta> metaList = metaRepository.listMetas();
        Map<String, WinRateUsedCounter> metaData = new LinkedHashMap<String, WinRateUsedCounter>();
        for (Meta meta : metaList) {
            // don't add meta info if the card is not yet printed for the first time
            if (!meta.getCardpool().isOlderThan(firstPrintPack)) {
                metaData.put(meta.getTitle(), metaStatistics.calculateCardStats(meta, card));
            }
        }
        cardStats.setMetaData(metaData);
        return cardStats;
    }

    public DeckSideStats getDeckStats(String metaTitle) {
        DeckSideStats result = new DeckSideStats();
        Set<DeckIdentity> deckIdentities = deckIdentityRepository.findByMetaTitle(metaTitle);
        List<DeckIdentity> runnerDecks = deckIdentities.stream().filter(x -> x.getIdentity().getSide_code().equals("runner")).collect(Collectors.toList());
        List<DeckIdentity> corpDecks = deckIdentities.stream().filter(x -> x.getIdentity().getSide_code().equals("corp")).collect(Collectors.toList());
        sortDeckStats(runnerDecks);
        sortDeckStats(corpDecks);
        result.setRunner(runnerDecks);
        result.setCorp(corpDecks);
        return result;
    }

    private void sortDeckStats(List<DeckIdentity> deckIdentities) {
        deckIdentities.sort(Comparator.comparing(DeckIdentity::getDeckCount).reversed());
        for (DeckIdentity deckId : deckIdentities) {
            deckId.sortDecks();
        }
    }
}