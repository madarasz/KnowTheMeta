package com.madarasz.knowthemeta.database.DRs.queryresult;

import com.madarasz.knowthemeta.database.DOs.Card;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class CardCode {
    private Card card;
    private String code;

    public CardCode() {
    }

    public CardCode(Card card, String code) {
        this.card = card;
        this.code = code;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}