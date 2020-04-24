package com.madarasz.knowthemeta.database.DOs.relationships;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.MWL;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="MWL_CARD")
public class MWLCard {
    @Id @GeneratedValue private Long id;
    @StartNode private MWL mwl;
    @EndNode private Card card;
    @Property private Boolean global_penalty; // 1 or no value
    @Property private int universal_faction_cost;   // 1 or 3 or no value
    @Property private Boolean is_restricted; // 1 or no value
    @Property private Boolean deck_limit;   // deck_limit = 0 or no value

    public MWLCard() {
    }

    public MWLCard(MWL mwl, Card card, Boolean global_penalty, int universal_faction_cost, Boolean is_restricted,
            Boolean deck_limit) {
        this.mwl = mwl;
        this.card = card;
        this.global_penalty = global_penalty;
        this.universal_faction_cost = universal_faction_cost;
        this.is_restricted = is_restricted;
        this.deck_limit = deck_limit;
    }

    public MWL getMwl() {
        return mwl;
    }

    public void setMwl(MWL mwl) {
        this.mwl = mwl;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Boolean getGlobal_penalty() {
        return global_penalty;
    }

    public void setGlobal_penalty(Boolean global_penalty) {
        this.global_penalty = global_penalty;
    }

    public int getUniversal_faction_cost() {
        return universal_faction_cost;
    }

    public void setUniversal_faction_cost(int universal_faction_cost) {
        this.universal_faction_cost = universal_faction_cost;
    }

    public Boolean getIs_restricted() {
        return is_restricted;
    }

    public void setIs_restricted(Boolean is_restricted) {
        this.is_restricted = is_restricted;
    }

    public Boolean getDeck_limit() {
        return deck_limit;
    }

    public void setDeck_limit(Boolean deck_limit) {
        this.deck_limit = deck_limit;
    }

    
    
}