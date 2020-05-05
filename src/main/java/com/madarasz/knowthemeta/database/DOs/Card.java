package com.madarasz.knowthemeta.database.DOs;

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
    @Nullable private int base_link;  // runner identities only
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
    @Relationship(type = "CARD_IN_PACK", direction = Relationship.OUTGOING)
    private CardPack pack;  // TODO: What if in multiple packs?

    public Card() {
    }

    public Card(int cost, int deck_limit, String faction_code, int faction_cost, int influence_limit,
            int minimum_deck_size, int memory_cost, int strength, int advancement_cost, int agenda_points,
            int trash_cost, String keywords, String side_code, String text, String title, String type_code,
            Boolean uniqueness, int base_link) {
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
        this.base_link = base_link;
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

    public CardPack getPack() {
        return pack;
    }

    public void setPack(CardPack pack) {
        this.pack = pack;
    }

    public int getBase_link() {
        return base_link;
    }

    public void setBase_link(int base_link) {
        this.base_link = base_link;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + advancement_cost;
        result = prime * result + agenda_points;
        result = prime * result + base_link;
        result = prime * result + cost;
        result = prime * result + deck_limit;
        result = prime * result + ((faction_code == null) ? 0 : faction_code.hashCode());
        result = prime * result + faction_cost;
        result = prime * result + influence_limit;
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + memory_cost;
        result = prime * result + minimum_deck_size;
        result = prime * result + ((side_code == null) ? 0 : side_code.hashCode());
        result = prime * result + strength;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + trash_cost;
        result = prime * result + ((type_code == null) ? 0 : type_code.hashCode());
        result = prime * result + ((uniqueness == null) ? 0 : uniqueness.hashCode());
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
        Card other = (Card) obj;
        if (advancement_cost != other.advancement_cost)
            return false;
        if (agenda_points != other.agenda_points)
            return false;
        if (base_link != other.base_link)
            return false;
        if (cost != other.cost)
            return false;
        if (deck_limit != other.deck_limit)
            return false;
        if (faction_code == null) {
            if (other.faction_code != null)
                return false;
        } else if (!faction_code.equals(other.faction_code))
            return false;
        if (faction_cost != other.faction_cost)
            return false;
        if (influence_limit != other.influence_limit)
            return false;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (memory_cost != other.memory_cost)
            return false;
        if (minimum_deck_size != other.minimum_deck_size)
            return false;
        if (side_code == null) {
            if (other.side_code != null)
                return false;
        } else if (!side_code.equals(other.side_code))
            return false;
        if (strength != other.strength)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (trash_cost != other.trash_cost)
            return false;
        if (type_code == null) {
            if (other.type_code != null)
                return false;
        } else if (!type_code.equals(other.type_code))
            return false;
        if (uniqueness == null) {
            if (other.uniqueness != null)
                return false;
        } else if (!uniqueness.equals(other.uniqueness))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Card [advancement_cost=" + advancement_cost + ", agenda_points=" + agenda_points + ", base_link="
                + base_link + ", cost=" + cost + ", deck_limit=" + deck_limit + ", faction_code=" + faction_code
                + ", faction_cost=" + faction_cost + ", influence_limit=" + influence_limit + ", keywords=" + keywords
                + ", memory_cost=" + memory_cost + ", minimum_deck_size=" + minimum_deck_size + ", side_code="
                + side_code + ", strength=" + strength + ", text=" + text + ", title=" + title + ", trash_cost="
                + trash_cost + ", type_code=" + type_code + ", uniqueness=" + uniqueness + "]";
    }

}