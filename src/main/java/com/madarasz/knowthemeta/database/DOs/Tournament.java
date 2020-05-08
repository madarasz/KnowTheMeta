package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class Tournament {
    @Id @GeneratedValue private Long graphId;
    private int id; // ABR id
    private String title;
    @DateString("yyyy.MM.dd.") private Date date;
    private int players_count;
    private int top_count;
    private Boolean matchdata;
    private Meta meta;

    public Tournament() {
    }

    public Tournament(int id, String title, Date date_start, int players_count, int top_count, Boolean matchdata) {
        this.id = id;
        this.title = title;
        this.date = date_start;
        this.players_count = players_count;
        this.top_count = top_count;
        this.matchdata = matchdata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPlayers_count() {
        return players_count;
    }

    public void setPlayers_count(int players_count) {
        this.players_count = players_count;
    }

    public int getTop_count() {
        return top_count;
    }

    public void setTop_count(int top_count) {
        this.top_count = top_count;
    }

    public Boolean isMatchDataAvailable() {
        return matchdata;
    }

    public void setMatchdata(Boolean matchdata) {
        this.matchdata = matchdata;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "Tournament [date_start=" + date + ", id=" + id + ", matchdata=" + matchdata + ", players_count="
                + players_count + ", title=" + title + ", top_count=" + top_count + "]";
    }

    
    
}