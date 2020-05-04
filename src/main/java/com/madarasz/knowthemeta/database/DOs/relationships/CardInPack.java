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
    @StartNode private CardPack cardPack;
    @EndNode private Card card;
    @Property String code;
    @Property @Nullable private String image_url;

    public CardInPack() {
    }

    public CardInPack(Card card, CardPack cardPack, String code, String image_url) {
        this.card = card;
        this.cardPack = cardPack;
        this.code = code;
        this.image_url = image_url;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((image_url == null) ? 0 : image_url.hashCode());
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
        CardInPack other = (CardInPack) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (image_url == null) {
            if (other.image_url != null)
                return false;
        } else if (!image_url.equals(other.image_url))
            return false;
        return true;
    }
}