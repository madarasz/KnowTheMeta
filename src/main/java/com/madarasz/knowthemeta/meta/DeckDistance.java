package com.madarasz.knowthemeta.meta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInDeck;
import com.madarasz.knowthemeta.database.DOs.stats.DeckIdentity;
import com.madarasz.knowthemeta.database.DOs.stats.DeckStats;
import mdsj.MDSJ;

import org.springframework.stereotype.Service;

@Service
public class DeckDistance {
    public final static double influenceSensisitivity = 1.2; // cards costing influence result in bigger distance

    public void calculateDeckCoordinates(DeckIdentity deckIdentity) {
        int deckSize = deckIdentity.getDeckCount();
        double [][] distanceMatrix = new double [deckSize][deckSize];
        int i = 0;
        List<DeckStats> decks = deckIdentity.getDecks();
        for (DeckStats deck1 : decks) {
            int u = 0;
            for (DeckStats deck2 : decks) {
                distanceMatrix[i][u] = getDeckDistance(deck1.getDeck(), deck2.getDeck());
                u++;
            }
            i++;
        }
        double [][] coordinates = calculateMDS(distanceMatrix);
        i = 0;
        for (DeckStats deck : decks) {
            deck.setxCoordinate(NaNFix(coordinates[0][i]));
            deck.setyCoordinate(NaNFix(coordinates[1][i]));
            i++;
        }
    }

    public double getDeckDistance(Deck deckA, Deck deckB) {
        // same decks have 0 distance
        if (deckA.getId() == deckB.getId()) {
            return 0;
        }
        // TODO: influence sensitivity
        boolean sameIdentity = deckA.getIdentity().getTitle().equals(deckB.getIdentity().getTitle());
        double influencePower = sameIdentity ? influenceSensisitivity : 1;
        String deckFactionCode = deckA.getIdentity().getFaction().getFactionCode();
        double distance = 0;
        // union of all cards
        Set<Card> cards = new HashSet<>();
        for (CardInDeck card : deckA.getCards()) {
            cards.add(card.getCard());
        }
        for (CardInDeck card : deckB.getCards()) {
            cards.add(card.getCard());
        }
        // iterate all cards
        for (Card card : cards) {
            int difference = 0;
            if (!deckA.doesContainCard(card)) {
                difference = deckB.getCardQuantity(card);
            } else if (!deckB.doesContainCard(card)) {
                difference = deckA.getCardQuantity(card);
            } else {
                difference = Math.abs(deckA.getCardQuantity(card) - deckB.getCardQuantity(card));
            }
            if (card.getFaction().getFactionCode().equals(deckFactionCode)) {
                distance += difference;
            } else {
                distance += difference * Math.pow(influencePower, card.getFaction_cost());
            }
        }
        return distance;
    }

    public double[][] calculateMDS(double[][] input) {
        return MDSJ.classicalScaling(input);
    }

    private double NaNFix(double input) {
        if (Double.isNaN(input)) {
            return 0;
        } else {
            return input;
        }
    }
}