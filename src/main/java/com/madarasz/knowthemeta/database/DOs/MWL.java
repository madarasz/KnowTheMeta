package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.relationships.MWLCard;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class MWL {
    @Id @GeneratedValue private Long id;
    private String code;
    private String name;
    private Boolean active;
    @DateString("yyyy-MM-dd") private Date date_start;
    @Relationship(type = "MWL_CARD")
    private Set<MWLCard> cards = new HashSet<MWLCard>();

    public MWL() {
    }

    public MWL(String code, String name, Boolean active, Date date_start) {
        this.code = code;
        this.name = name;
        this.active = active;
        this.date_start = date_start;
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

    @Override
    public String toString() {
        return "MWL [active=" + active + ", code=" + code + ", date_start=" + date_start + ", name=" + name + ", size =" + cards.size() + "]";
    }
    
}