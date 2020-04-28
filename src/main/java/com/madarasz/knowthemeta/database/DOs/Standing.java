package com.madarasz.knowthemeta.database.DOs;

import java.util.Arrays;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Standing {
    @Id @GeneratedValue private Long graphId;
    private Tournament tournament;
    private Card identity;
    private Boolean isRunner;
    private Deck deck;
    private int rank;
    private int winCount = 0;
    private int lossCount = 0;

    public Standing() {
    }

    public Standing(Tournament tournament, Card identity, int rank, Boolean isRunner) {
        this.tournament = tournament;
        this.identity = identity;
        this.rank = rank;
        this.isRunner = isRunner;
        this.adjustIsRunner();
    }

    public Standing(Tournament tournament, Card identity, Deck deck, int rank, Boolean isRunner) {
        this.tournament = tournament;
        this.identity = identity;
        this.deck = deck;
        this.rank = rank;
        this.isRunner = isRunner;
        this.adjustIsRunner();
    }

    public void adjustIsRunner() {
        String[] runnerFactionCodes = {"neutral-runner", "shaper", "criminal", "anarch", "apex", "adam", "sunny-lebeau"};
        isRunner = Arrays.stream(runnerFactionCodes).anyMatch(identity.getFaction_code()::equals);
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Card getIdentity() {
        return identity;
    }

    public void setIdentity(Card identity) {
        this.identity = identity;
    }

    public Boolean getIsRunner() {
        return isRunner;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public void setLossCount(int lossCount) {
        this.lossCount = lossCount;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    @Override
    public String toString() {
        return "Standing [identity=" + identity.getTitle() + ", isRunner=" + isRunner + ", rank=" + rank + ", tournament="
                + tournament.getTitle() + "]";
    }
}