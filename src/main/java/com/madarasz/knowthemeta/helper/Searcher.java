package com.madarasz.knowthemeta.helper;

import java.util.Collection;
import java.util.Optional;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Searcher {
    private final static Logger log = LoggerFactory.getLogger(Searcher.class);

    public Card getCardByCode(Collection<CardInPack> searchFrom, String cardCode) {
        Optional<CardInPack> result = searchFrom.stream().filter(x -> x.getCode().equals(cardCode)).findFirst();
        if (!result.isPresent()) {
            String errorMessage = "Could not find card for code: " + cardCode;
            log.error(errorMessage);
            // TODO: throw exception
        } 
        return result.get().getCard();
    }
}