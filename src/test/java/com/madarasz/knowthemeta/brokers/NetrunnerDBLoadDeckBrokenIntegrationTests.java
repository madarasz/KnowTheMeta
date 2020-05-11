package com.madarasz.knowthemeta.brokers;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;

import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetrunnerDBLoadDeckBrokenIntegrationTests {

    @Spy HttpBroker httpBroker;
    @InjectMocks NetrunnerDBBroker netrunnerDBBroker;

    // tries to load https://netrunnerdb.com/api/2.0/public/decklist/58287 which got probably deleted
    @Test
    public void testBrokenDeckLoad() {
        Deck testDeck = netrunnerDBBroker.loadDeck(58287, new HashSet<Deck>(), new HashSet<CardInPack>());
        assertNull(testDeck);
    }
}