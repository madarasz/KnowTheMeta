package com.madarasz.knowthemeta.database.DOs.stats;

import com.madarasz.knowthemeta.database.DOs.Meta;

public class MetaCards {
    private Meta meta;
    private SideStats factions;
    private SideStats identities;
    private SideStats cards;

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

    public SideStats getFactions() {
        return factions;
    }

    public void setFactions(SideStats factions) {
        this.factions = factions;
    }

    public SideStats getIdentities() {
        return identities;
    }

    public void setIdentities(SideStats identities) {
        this.identities = identities;
    }

    public SideStats getCards() {
        return cards;
    }

    public void setCards(SideStats cards) {
        this.cards = cards;
    }
    
}