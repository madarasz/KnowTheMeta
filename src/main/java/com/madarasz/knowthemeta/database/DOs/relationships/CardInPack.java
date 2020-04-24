package com.madarasz.knowthemeta.database.DOs.relationships;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardPack;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="CARD_IN_PACK")
public class CardInPack {
    @Id @GeneratedValue private Long id;
    @StartNode private Card card;
    @EndNode private CardPack cardPack;
    @Property String code;
    @Property @Nullable private String image_url;

    public CardInPack() {
    }

    public CardInPack(Card card, CardPack cardPack, String code, String image_url) {
        this.card = card;
        this.cardPack = cardPack;
        this.code = code;
        this.image_url = image_url;
        // add relationships
        this.cardPack.addCard(card);
        this.card.addPack(cardPack);
    }

    public Card getCard() {
        return card;
    }

    public CardPack getCardPack() {
        return cardPack;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setCardPack(CardPack cardPack) {
        this.cardPack = cardPack;
    }
}