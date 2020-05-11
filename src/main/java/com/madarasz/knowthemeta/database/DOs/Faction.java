package com.madarasz.knowthemeta.database.DOs;

import com.madarasz.knowthemeta.database.Entity;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Faction extends Entity {
    private String factionCode;
    private String name;
    private Boolean runner;

    public Faction() {
    }

    public Faction(String factionCode, String name) {
        this.factionCode = factionCode;
        this.name = name;
    }

    public Faction(String factionCode, String name, Boolean runner) {
        this.factionCode = factionCode;
        this.name = name;
        this.runner = runner;
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

    public Boolean isRunner() {
        return runner;
    }

    public void setRunner(Boolean runner) {
        this.runner = runner;
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