package com.madarasz.knowthemeta.database.DOs.stats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

public class CardStats {
    private Card card;
    private List<CardInPack> prints;
    private Map<String, WinRateUsedCounter> metaData = new LinkedHashMap<String, WinRateUsedCounter>();

    public CardStats() {
    }

    public CardStats(Card card, List<CardInPack> prints, Map<String, WinRateUsedCounter> metaData) {
        this.card = card;
        this.prints = prints;
        this.metaData = metaData;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<CardInPack> getPrints() {
        return prints;
    }

    public void setPrints(List<CardInPack> prints) {
        this.prints = prints;
    }

    public Map<String, WinRateUsedCounter> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, WinRateUsedCounter> metaData) {
        this.metaData = metaData;
    }

    
}