package com.madarasz.knowthemeta.database.DOs;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CardCycle {
    @Id @GeneratedValue private Long id;
    private String code;
    private String name;
    private int position;
    private Boolean rotated;
    @Relationship(type = "CYCLE", direction = Relationship.INCOMING)
    private Set<CardPack> packs;

    public CardCycle() {
        this.packs = new HashSet<>();
    }

    public CardCycle(String code, String name, int position, Boolean rotated) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.rotated = rotated;
        this.packs = new HashSet<>();
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

    public Boolean getRotated() {
        return rotated;
    }

    public void setRotated(Boolean rotated) {
        this.rotated = rotated;
    }

    public Set<CardPack> getPacks() {
        return packs;
    }

    public void setPacks(Set<CardPack> packs) {
        this.packs = packs;
    }

    public void addPack(CardPack pack) {
        this.packs.add(pack);
    }

    @Override
    public String toString() {
        return "CardCycle [code=" + code + ", name=" + name + ", position=" + position + ", rotated=" + rotated + ", size=" + packs.size() + "]";
    }

}