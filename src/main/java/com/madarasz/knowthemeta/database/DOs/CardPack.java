package com.madarasz.knowthemeta.database.DOs;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CardPack {
    @Id @GeneratedValue private Long id;
    private String code;
    private String name;
    private int position;
    private CardCycle cycle;
    @Relationship(type = "CARD_IN_PACK", direction = Relationship.UNDIRECTED)
    private Set<Card> cards;

    public CardPack() {
        this.cards = new HashSet<Card>();
    }

    public CardPack(String code, String name, int position, CardCycle cycle) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.cycle = cycle;
        this.cards = new HashSet<Card>();
    }    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CardCycle getCycle() {
        return cycle;
    }

    public void setCycle(CardCycle cycle) {
        this.cycle = cycle;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    @Override
    public String toString() {
        return "CardPack [code=" + code + ", name=" + name + ", position=" + position + "]";
    }

}