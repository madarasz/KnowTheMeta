package com.madarasz.knowthemeta.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInDeck;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test for loadDeck method of NetrunnerDBBroker with verifying results in DB
 * + NetrunnerDB data load at the beginning
 */
@SpringBootTest
public class NetrunnerDBLoadDeckIntegrationTests {
    @Autowired NetrunnerDBBroker netrunnerDBBroker;

    // Loading deck: https://netrunnerdb.com/en/decklist/51436
    @Test
    public void testLoadDeck() {
        // setup
        Set<Deck> existingDecks = new HashSet<Deck>();
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        Set<CardPack> packs = netrunnerDBBroker.loadPacks(cycles);
        Set<CardInPack> cards = new HashSet<CardInPack>(netrunnerDBBroker.loadCards(packs));
        // run
        Deck resultDeck = netrunnerDBBroker.loadDeck(51436, existingDecks, cards);
        // assert
        assertTrue(resultDeck.getIdentity().getTitle().contains("Valencia"), "Deck identity is incorrect");
        assertEquals(50, resultDeck.cardCount(), "Deck card count is incorrect");
        assertEquals("Necro", resultDeck.getPlayer().getUser_name(), "Player name is incorrect");
        Optional<CardInDeck> card = resultDeck.getCards().stream().filter(x -> x.getCard().getTitle().equals("Paperclip")).findFirst();
        assertTrue(card.isPresent(), "Card is missing from deck");
        assertEquals(2, card.get().getQuantity(), "Card quantity is incorrect");
        // adding deck to existing deck list, rerunning
        existingDecks.add(resultDeck);
        Deck resultDeck2 = netrunnerDBBroker.loadDeck(51436, existingDecks, cards);
        assertEquals(resultDeck, resultDeck2, "Re-loaded deck is not identical");
    }
}