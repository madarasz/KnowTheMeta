package com.madarasz.knowthemeta.database.DOs.relationships;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Deck;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="CARD_IN_DECK")
public class CardInDeck extends Entity {
    @StartNode private Deck deck;
    @EndNode private Card card;
    @Property int quantity;

    public CardInDeck() {
    }

    public CardInDeck(Deck deck, Card card, int quantity) {
        this.deck = deck;
        this.card = card;
        this.quantity = quantity;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}