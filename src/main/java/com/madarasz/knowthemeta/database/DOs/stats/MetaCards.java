package com.madarasz.knowthemeta.database.DOs.stats;

import com.madarasz.knowthemeta.database.DOs.Meta;

public class MetaCards {
    private Meta meta;
    private CardStats factions;
    private CardStats identities;
    private CardStats cards;

    public MetaCards() {
    }

    public MetaCards(Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public CardStats getFactions() {
        return factions;
    }

    public void setFactions(CardStats factions) {
        this.factions = factions;
    }

    public CardStats getIdentities() {
        return identities;
    }

    public void setIdentities(CardStats identities) {
        this.identities = identities;
    }

    public CardStats getCards() {
        return cards;
    }

    public void setCards(CardStats cards) {
        this.cards = cards;
    }

    
}