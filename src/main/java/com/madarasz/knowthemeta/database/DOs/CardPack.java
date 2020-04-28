package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class CardPack {
    @Id @GeneratedValue private Long id;
    private String code;
    private String name;
    private int position;
    @DateString("yyyy-MM-dd") @Nullable Date date_release;
    @Relationship(type = "CYCLE", direction = Relationship.OUTGOING)
    private CardCycle cycle;
    @Relationship(type = "CARD_IN_PACK", direction = Relationship.OUTGOING)
    private Set<CardInPack> cards = new HashSet<CardInPack>();

    public CardPack() {
    }

    public CardPack(String code, String name, int position, CardCycle cycle, Date date_release) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.cycle = cycle;
        this.date_release = date_release;
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

    public Set<CardInPack> getCards() {
        return this.cards;
    }

    public void addCards(CardInPack cardInPack) {
        this.cards.add(cardInPack);
    }

    public Date getDate_release() {
        return date_release;
    }

    public void setDate_release(Date date_release) {
        this.date_release = date_release;
    }

    @Override
    public String toString() {
        return "CardPack [code=" + code + ", name=" + name + ", position=" + position + "]";
    }

}