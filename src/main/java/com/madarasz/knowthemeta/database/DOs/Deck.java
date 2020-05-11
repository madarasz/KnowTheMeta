package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInDeck;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class Deck extends Entity {
    private int id; // NetrunnerDB deck ID
    private User player;
    private String name;
    @Relationship(type = "CARD_IN_DECK")
    private Set<CardInDeck> deckCards = new HashSet<CardInDeck>();
    private Card identity; 
    @DateString("yyyy-MM-dd HH:mm:ss") private Date date_update;
    private CardPack upTo;

    public Deck() {
    }

    public Deck(int id, String name, Date date_update) {
        this.id = id;
        this.name = name;
        this.date_update = date_update;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CardInDeck> getCards() {
        return deckCards;
    }

    public void addCard(CardInDeck card) {
        this.deckCards.add(card);
    }

    public Card getIdentity() {
        return identity;
    }

    public void setIdentity(Card identity) {
        this.identity = identity;
    }

    public Date getDate_update() {
        return date_update;
    }

    public void setDate_update(Date date_update) {
        this.date_update = date_update;
    }

    public CardPack getUpTo() {
        return upTo;
    }

    public void calculateUpTo() {
        if (identity != null) {
            // TODO
        }   
    }

    public int cardCount() {
        int result = 0;
        for (CardInDeck card : this.deckCards) {
            result += card.getQuantity();
        }
        return result;
    }

    @Override
    public String toString() {
        return "Deck [date_update=" + date_update + ", id=" + id + ", identity=" + identity.getTitle() + ", name=" + name + ", size=" + cardCount() + ", player=" + player.getUser_name() + "]";
    }

    
}