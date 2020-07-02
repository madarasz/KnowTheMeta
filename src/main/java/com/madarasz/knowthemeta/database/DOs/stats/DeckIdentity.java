package com.madarasz.knowthemeta.database.DOs.stats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.serializer.IdentitySerializer;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class DeckIdentity extends Entity{
    @JsonSerialize(using = IdentitySerializer.class)
    private Card identity;
    @JsonIgnore
    private Meta meta;
    // TODO: private distanceMatrix
    @Relationship(type = "DECKSTAT")
    private List<DeckStats> decks = new ArrayList<>();

    public DeckIdentity() {
    }

    public DeckIdentity(Card identity, Meta meta) {
        this.identity = identity;
        this.meta = meta;
    }

    public Card getIdentity() {
        return identity;
    }

    public void setIdentity(Card identity) {
        this.identity = identity;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<DeckStats> getDecks() {
        return decks;
    }

    public void setDecks(List<DeckStats> decks) {
        this.decks = decks;
    }

    public void addDeck(DeckStats deck) {
        this.decks.add(deck);
    }

    public void sortDecks() {
        this.decks.sort(Comparator.comparing(DeckStats::getSuccessScore).reversed());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identity == null) ? 0 : identity.getTitle().hashCode());
        result = prime * result + ((meta == null) ? 0 : meta.getTitle().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeckIdentity other = (DeckIdentity) obj;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.getTitle().equals(other.identity.getTitle()))
            return false;
        if (meta == null) {
            if (other.meta != null)
                return false;
        } else if (!meta.getTitle().equals(other.meta.getTitle()))
            return false;
        return true;
    }

    
    
}