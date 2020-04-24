package com.madarasz.knowthemeta.database.DOs.relationships;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardPack;

import org.neo4j.driver.internal.shaded.reactor.util.annotation.Nullable;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="CARD_IN_PACK")
public class CardInPack {
    @StartNode private Card card;
    @EndNode private CardPack cardPack;
    private String code;
    @Nullable private String image_url;

    public CardInPack() {
    }

    public CardInPack(Card card, CardPack cardPack, String code, String image_url) {
        this.card = card;
        this.cardPack = cardPack;
        this.code = code;
        this.image_url = image_url;
        // add relationship
        this.cardPack.getCards().add(card);
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
    
}