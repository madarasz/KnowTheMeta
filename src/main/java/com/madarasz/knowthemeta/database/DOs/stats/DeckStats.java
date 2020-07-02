package com.madarasz.knowthemeta.database.DOs.stats;

import com.madarasz.knowthemeta.database.Entity;
import com.madarasz.knowthemeta.database.DOs.Deck;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DeckStats extends Entity {
    private Deck deck;
    private double successScore;
    private String deckSummary;
    private String rankSummary;
    private double xCoordinate;
    private double yCoordinate;
    private int rank;
    private int tournamentId;

    public DeckStats() {
    }
    
    public DeckStats(Deck deck, double successScore) {
        this.deck = deck;
        this.successScore = successScore;
    }

    public DeckStats(Deck deck, double successScore, int rank, int tournamentId) {
        this.deck = deck;
        this.successScore = successScore;
        this.rank = rank;
        this.tournamentId = tournamentId;
    }

    public DeckStats(Deck deck, double successScore, String deckSummary, String rankSummary,
            double xCoordinate, double yCoordinate) {
        this.deck = deck;
        this.successScore = successScore;
        this.deckSummary = deckSummary;
        this.rankSummary = rankSummary;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public double getSuccessScore() {
        return successScore;
    }

    public void setSuccessScore(double successScore) {
        this.successScore = successScore;
    }

    public String getDeckSummary() {
        return deckSummary;
    }

    public void setDeckSummary(String deckSummary) {
        this.deckSummary = deckSummary;
    }

    public String getRankSummary() {
        return rankSummary;
    }

    public void setRankSummary(String rankSummary) {
        this.rankSummary = rankSummary;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    @Override
    public String toString() {
        return "DeckStats [deckSummary=" + deckSummary + ", rankSummary=" + rankSummary + ", successScore="
                + successScore + "]";
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deck == null) ? 0 : deck.getId());
        result = prime * result + rank;
        result = prime * result + tournamentId;
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
        DeckStats other = (DeckStats) obj;
        if (deck == null) {
            if (other.deck != null)
                return false;
        } else if (deck.getId() != other.deck.getId())
            return false;
        if (rank != other.rank)
            return false;
        if (tournamentId != other.tournamentId)
            return false;
        return true;
    }

   

}