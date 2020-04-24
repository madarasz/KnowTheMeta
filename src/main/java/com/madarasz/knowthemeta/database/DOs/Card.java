package com.madarasz.knowthemeta.database.DOs;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Card {
    @Id @GeneratedValue private Long id;
    @Nullable private int cost;
    private int deck_limit;
    private String faction_code;
    @Nullable private int faction_cost;
    @Nullable private int influence_limit;  // identities only
    @Nullable private int minimum_deck_size;  // identities only
    @Nullable private int memory_cost;  // programs only
    @Nullable private int strength;  // programs, ice only
    @Nullable private int advancement_cost;  // agendas only
    @Nullable private int agenda_points;  // agendas only
    @Nullable private int trash_cost;  // corp cards sometimes
    private String keywords;
    private String side_code;
    private String text;
    @Index(unique = true) String title;
    private String type_code;
    private Boolean uniqueness;

    public Card() {
    }

    public Card(int cost, int deck_limit, String faction_code, int faction_cost, int influence_limit,
            int minimum_deck_size, int memory_cost, int strength, int advancement_cost, int agenda_points,
            int trash_cost, String keywords, String side_code, String text, String title, String type_code,
            Boolean uniqueness) {
        this.cost = cost;
        this.deck_limit = deck_limit;
        this.faction_code = faction_code;
        this.faction_cost = faction_cost;
        this.influence_limit = influence_limit;
        this.minimum_deck_size = minimum_deck_size;
        this.memory_cost = memory_cost;
        this.strength = strength;
        this.advancement_cost = advancement_cost;
        this.agenda_points = agenda_points;
        this.trash_cost = trash_cost;
        this.keywords = keywords;
        this.side_code = side_code;
        this.text = text;
        this.title = title;
        this.type_code = type_code;
        this.uniqueness = uniqueness;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDeck_limit() {
        return deck_limit;
    }

    public void setDeck_limit(int deck_limit) {
        this.deck_limit = deck_limit;
    }

    public String getFaction_code() {
        return faction_code;
    }

    public void setFaction_code(String faction_code) {
        this.faction_code = faction_code;
    }

    public int getFaction_cost() {
        return faction_cost;
    }

    public void setFaction_cost(int faction_cost) {
        this.faction_cost = faction_cost;
    }

    public int getInfluence_limit() {
        return influence_limit;
    }

    public void setInfluence_limit(int influence_limit) {
        this.influence_limit = influence_limit;
    }

    public int getMinimum_deck_size() {
        return minimum_deck_size;
    }

    public void setMinimum_deck_size(int minimum_deck_size) {
        this.minimum_deck_size = minimum_deck_size;
    }

    public int getMemory_cost() {
        return memory_cost;
    }

    public void setMemory_cost(int memory_cost) {
        this.memory_cost = memory_cost;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAdvancement_cost() {
        return advancement_cost;
    }

    public void setAdvancement_cost(int advancement_cost) {
        this.advancement_cost = advancement_cost;
    }

    public int getAgenda_points() {
        return agenda_points;
    }

    public void setAgenda_points(int agenda_points) {
        this.agenda_points = agenda_points;
    }

    public int getTrash_cost() {
        return trash_cost;
    }

    public void setTrash_cost(int trash_cost) {
        this.trash_cost = trash_cost;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSide_code() {
        return side_code;
    }

    public void setSide_code(String side_code) {
        this.side_code = side_code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public Boolean getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(Boolean uniqueness) {
        this.uniqueness = uniqueness;
    }


}