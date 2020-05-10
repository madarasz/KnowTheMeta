package com.madarasz.knowthemeta.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * Integration test - ABRBroker.getStandingsData via HttpBroker to real ABR endpoints 
 * + NetrunnerDB data gathering in the beginning
 */
@SpringBootTest
public class ABRBrokerGetStadingsDataIntegrationTests {
    @Autowired ABRBroker abrBroker;
    @SpyBean NetrunnerDBBroker netrunnerDBBroker; 

    // loads: https://alwaysberunning.net/api/entries?id=2731
    @Test
    public void testGetStandings() {
        // setup
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        Set<CardPack> packs = netrunnerDBBroker.loadPacks(cycles);
        Set<CardInPack> cards = new HashSet<CardInPack>(netrunnerDBBroker.loadCards(packs));
        Tournament tournament = new Tournament(2731, "The Huns Strike Back - Online Hungarian Store Champs", new Date(), 11, 3, true);
        doReturn(new Deck()).when(netrunnerDBBroker).loadDeck(anyInt(), anySet(), anySet());
        // run
        List<Standing> results = abrBroker.getStadingData(tournament, cards, cards, new HashSet<Deck>());
        // verify
        assertEquals(22, results.size(), "Wrond number of standings ");
        // swiss 3rd, top-cut 2nd
        Standing secondRunner = results.stream().filter(x -> x.getRank() == 2 && x.getIsRunner()).findFirst().get();
        assertTrue(secondRunner.getIdentity().getTitle().contains("Geist"), "Second runner has wrong identity");
        // swiss 4th, no top-cut
        Standing fourthCorp = results.stream().filter(x -> x.getRank() == 4 && !x.getIsRunner()).findFirst().get();
        assertTrue(fourthCorp.getIdentity().getTitle().contains("Jinteki: Personal Evolution"), "Fourth corp has wrong identity");
    }
}