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

    public void copyFrom(CardCycle cardCycle) {
        this.code = cardCycle.getCode();
        this.name = cardCycle.getName();
        this.position = cardCycle.getPosition();
        this.rotated = cardCycle.getRotated();
    }

    @Override
    public String toString() {
        return "CardCycle [code=" + code + ", name=" + name + ", position=" + position + ", rotated=" + rotated + ", size=" + packs.size() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + position;
        result = prime * result + ((rotated == null) ? 0 : rotated.hashCode());
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
        CardCycle other = (CardCycle) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (position != other.position)
            return false;
        if (rotated == null) {
            if (other.rotated != null)
                return false;
        } else if (!rotated.equals(other.rotated))
            return false;
        return true;
    }

}