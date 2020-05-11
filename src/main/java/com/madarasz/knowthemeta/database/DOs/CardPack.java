package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class CardPack extends Entity {
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
        return "CardPack [code=" + code + ", name=" + name + ", position=" + position + ", date_release=" + date_release + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((date_release == null) ? 0 : date_release.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + position;
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
        CardPack other = (CardPack) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (date_release == null) {
            if (other.date_release != null)
                return false;
        } else if (!date_release.equals(other.date_release))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (position != other.position)
            return false;
        return true;
    }

}