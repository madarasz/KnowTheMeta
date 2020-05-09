package com.madarasz.knowthemeta.helper;

import java.util.Collection;
import java.util.Optional;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.User;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Filters collections of KnowTheMeta entities.
 * Returns first match. Returns null if no match exists.
 */
@Service
public class Searcher {
    private final static Logger log = LoggerFactory.getLogger(Searcher.class);

    public Card getCardByCode(Collection<CardInPack> searchFrom, String cardCode) {
        Optional<CardInPack> result = searchFrom.stream().filter(x -> x.getCode().equals(cardCode)).findFirst();
        if (result.isPresent()) {
            return result.get().getCard();
        } 
        String errorMessage = "Could not find card for code: " + cardCode;
        log.error(errorMessage);
        // TODO: throw exception
        return null;
    }

    public Standing getStadingByRankSide(Collection<Standing> searchFrom, int rank, Boolean isRunner) {
        Optional<Standing> result = searchFrom.stream().filter(x -> x.getRank() == rank && x.getIsRunner() == isRunner).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public long countStadingsByRankSide(Collection<Standing> searchFrom, int rank, Boolean isRunner) {
        return searchFrom.stream().filter(x -> x.getRank() == rank && x.getIsRunner() == isRunner).count();
    }

    public Deck getDeckById(Collection<Deck> searchFrom, int NetrunnerDBId) {
        Optional<Deck> result = searchFrom.stream().filter(x -> x.getId() == NetrunnerDBId).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public User getUserById(Collection<User> searchFrom, int NetrunnerDBId) {
        Optional<User> result = searchFrom.stream().filter(x -> x.getUser_id() == NetrunnerDBId).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
}