package com.madarasz.knowthemeta.database.DOs;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Faction {
    @Id @GeneratedValue private Long id;
    private String factionCode;
    private String name;

    public Faction() {
    }

    public Faction(String factionCode, String name) {
        this.factionCode = factionCode;
        this.name = name;
    }

    public String getFactionCode() {
        return factionCode;
    }

    public void setFactionCode(String factionCode) {
        this.factionCode = factionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factionCode == null) ? 0 : factionCode.hashCode());
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
        Faction other = (Faction) obj;
        if (factionCode == null) {
            if (other.factionCode != null)
                return false;
        } else if (!factionCode.equals(other.factionCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Faction [factionCode=" + factionCode + ", name=" + name + "]";
    }
    
}