package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.relationships.MWLCard;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class MWL extends Entity {
    private int id; // coming from NetrunnerDB, needed for ABR query
    private String code;
    private String name;
    private Boolean active;
    @DateString("yyyy-MM-dd") private Date date_start;
    @Relationship(type = "MWL_CARD")
    private Set<MWLCard> cards = new HashSet<MWLCard>();

    public MWL() {
    }

    public MWL(String code, String name, Boolean active, Date date_start, int id) {
        this.code = code;
        this.name = name;
        this.active = active;
        this.date_start = date_start;
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Set<MWLCard> getCards() {
        return cards;
    }

    public void addCard(MWLCard card) {
        this.cards.add(card);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MWL [active=" + active + ", code=" + code + ", date_start=" + date_start + ", name=" + name + ", size =" + cards.size() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((active == null) ? 0 : active.hashCode());
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((date_start == null) ? 0 : date_start.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        MWL other = (MWL) obj;
        if (active == null) {
            if (other.active != null)
                return false;
        } else if (!active.equals(other.active))
            return false;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (date_start == null) {
            if (other.date_start != null)
                return false;
        } else if (!date_start.equals(other.date_start))
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public void setCards(Set<MWLCard> cards) {
        this.cards = cards;
    }
}