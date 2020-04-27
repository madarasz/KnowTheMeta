package com.madarasz.knowthemeta.database.DOs;

import java.util.Date;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class Meta {
    @Id @GeneratedValue private Long id;
    private CardPack cardpool;
    private MWL mwl;
    private Boolean newCards;
    private Boolean statsCalculated = false;
    private String title;
    @DateString("yyyy-MM-dd HH:mm:ss") @Nullable Date lastUpdate;
    private int tournamentCount = 0;
    private int decksPlayedCount = 0;
    private int matchesCount = 0;

    public Meta(){
    }

    public Meta(CardPack cardpool, MWL mwl, Boolean newCards, String title) {
        this.cardpool = cardpool;
        this.mwl = mwl;
        this.newCards = newCards;
        this.title = title;
    }

    public CardPack getCardpool() {
        return cardpool;
    }

    public void setCardpool(CardPack cardpool) {
        this.cardpool = cardpool;
    }

    public MWL getMwl() {
        return mwl;
    }

    public void setMwl(MWL mwl) {
        this.mwl = mwl;
    }

    public Boolean getNewCards() {
        return newCards;
    }

    public void setNewCards(Boolean newCards) {
        this.newCards = newCards;
    }

    public Boolean getStatsCalculated() {
        return statsCalculated;
    }

    public void setStatsCalculated(Boolean statsCalculated) {
        this.statsCalculated = statsCalculated;
    }

    public int getTournamentCount() {
        return tournamentCount;
    }

    public void setTournamentCount(int tournamentCount) {
        this.tournamentCount = tournamentCount;
    }

    public int getDecksPlayedCount() {
        return decksPlayedCount;
    }

    public void setDecksPlayedCount(int decksPlayedCount) {
        this.decksPlayedCount = decksPlayedCount;
    }

    public int getMatchesCount() {
        return matchesCount;
    }

    public void setMatchesCount(int matchesCount) {
        this.matchesCount = matchesCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }      

    @Override
    public String toString() {
        return "Meta [cardpool=" + cardpool +  ", mwl=" + mwl + ", title=" + title + "]";
    }

}