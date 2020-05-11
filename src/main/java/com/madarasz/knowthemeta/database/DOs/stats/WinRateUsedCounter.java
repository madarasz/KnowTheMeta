package com.madarasz.knowthemeta.database.DOs.stats;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.serializer.WinRateUsedCounterSerializer;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@JsonSerialize(using = WinRateUsedCounterSerializer.class)
public class WinRateUsedCounter extends Entity {
    private int winCounter;
    private int drawCounter;
    private int lossCounter;
    private int usedCounter;
    private Meta meta;
    private Entity statAbout;

    public WinRateUsedCounter() {
    }

    public WinRateUsedCounter(Meta meta, Entity statAbout) {
        this.meta = meta;
        this.statAbout = statAbout;
    }

    public WinRateUsedCounter(int winCounter, int drawCounter, int lossCounter, int usedCounter, Meta meta,
            Entity statAbout) {
        this.winCounter = winCounter;
        this.drawCounter = drawCounter;
        this.lossCounter = lossCounter;
        this.usedCounter = usedCounter;
        this.meta = meta;
        this.statAbout = statAbout;
    }

    public int getWinCounter() {
        return winCounter;
    }

    public void setWinCounter(int winCounter) {
        this.winCounter = winCounter;
    }

    public int getDrawCounter() {
        return drawCounter;
    }

    public void setDrawCounter(int drawCounter) {
        this.drawCounter = drawCounter;
    }

    public int getLossCounter() {
        return lossCounter;
    }

    public void setLossCounter(int lossCounter) {
        this.lossCounter = lossCounter;
    }

    public int getUsedCounter() {
        return usedCounter;
    }

    public void setUsedCounter(int usedCounter) {
        this.usedCounter = usedCounter;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Entity getStatAbout() {
        return statAbout;
    }

    public void setStatAbout(Entity statAbout) {
        this.statAbout = statAbout;
    }

    public void copyFrom(WinRateUsedCounter other) {
        this.winCounter = other.winCounter;
        this.drawCounter = other.drawCounter;
        this.lossCounter = other.lossCounter;
        this.usedCounter = other.usedCounter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meta == null) ? 0 : meta.hashCode());
        result = prime * result + ((statAbout == null) ? 0 : statAbout.hashCode());
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
        WinRateUsedCounter other = (WinRateUsedCounter) obj;
        if (meta == null) {
            if (other.meta != null)
                return false;
        } else if (!meta.equals(other.meta))
            return false;
        if (statAbout == null) {
            if (other.statAbout != null)
                return false;
        } else if (!statAbout.equals(other.statAbout))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String titleString = "";
        if (statAbout instanceof Card) titleString = ((Card)statAbout).getTitle();
        if (statAbout instanceof Faction) titleString = ((Faction)statAbout).getName();
        return "WinRateUsedCounter [ meta=" + meta.getTitle() + ", statAbout=" + titleString 
                + ", drawCounter=" + drawCounter + ", lossCounter=" + lossCounter 
                + ", usedCounter=" + usedCounter + ", winCounter=" + winCounter + "]";
    }
}