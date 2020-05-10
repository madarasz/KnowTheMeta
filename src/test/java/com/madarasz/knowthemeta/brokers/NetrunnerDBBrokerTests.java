package com.madarasz.knowthemeta.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import com.google.gson.JsonElement;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DOs.relationships.MWLCard;
import com.madarasz.knowthemeta.helper.TestData;
import com.madarasz.knowthemeta.helper.TestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for loadCycles, loadPacks, loadCards, loadMWL methods of ABRBroker
 */
@ExtendWith(MockitoExtension.class)
public class NetrunnerDBBrokerTests {

    @Mock HttpBroker httpBroker;
    @InjectMocks NetrunnerDBBroker netrunnerDBBroker;
    public static final TestData testData = new TestData();
    private static final TestHelper testHelper = new TestHelper();

    @Test
    public void testCycleLoad() {
        // setup
        JsonElement jsonMock = testHelper.getJsonFromTestResource("MockNetrunnerDBCycles.json");
        Mockito.when(httpBroker.readJSONFromURL(anyString())).thenReturn(jsonMock);
        // run
        Set<CardCycle> results = netrunnerDBBroker.loadCycles();
        // validate
        Optional<CardCycle> cycle1 = results.stream().filter(x -> x.getCode().equals(testData.testCycle1.getCode())).findFirst();
        Optional<CardCycle> cycle2 = results.stream().filter(x -> x.getCode().equals(testData.testCycle2.getCode())).findFirst();
        assertEquals(2, results.size(), "Wrong number of test cycles returned");
        assertTrue(cycle1.isPresent(), "Test cycle not found");
        assertTrue(cycle2.isPresent(), "Test cycle not found");
        assertEquals(testData.testCycle1, cycle1.get(), "Test cycle data is incorrect");
        assertEquals(testData.testCycle2, cycle2.get(), "Test cycle data is incorrect");
    }

    @Test
    public void testPackLoad() {
        // setup
        JsonElement jsonMock = testHelper.getJsonFromTestResource("MockNetrunnerDBPacks.json");
        Mockito.when(httpBroker.readJSONFromURL(anyString())).thenReturn(jsonMock);
        // run
        Set<CardPack> results = netrunnerDBBroker.loadPacks(testData.cycleTestSet);
        // validate
        assertEquals(3, results.size(), "Wrong number of test packs returned");
        Optional<CardPack> pack1 = results.stream().filter(x -> x.getCode().equals(testData.testPack1.getCode())).findFirst();
        Optional<CardPack> pack2 = results.stream().filter(x -> x.getCode().equals(testData.testPack2.getCode())).findFirst();
        Optional<CardPack> pack3 = results.stream().filter(x -> x.getCode().equals(testData.testPack3.getCode())).findFirst();
        assertTrue(pack1.isPresent(), "Test pack not found");
        assertTrue(pack2.isPresent(), "Test pack not found");
        assertTrue(pack3.isPresent(), "Test pack not found");
        assertEquals(testData.testPack1, pack1.get(), "Test pack data is incorrect");
        assertEquals(testData.testPack2, pack2.get(), "Test pack data is incorrect");
        assertEquals(testData.testPack3, pack3.get(), "Test pack data is incorrect");
    }

    @Test
    public void testCardLoad() {
        // setup
        JsonElement jsonMock = testHelper.getJsonFromTestResource("MockNetrunnerDBCards.json");
        Mockito.when(httpBroker.readJSONFromURL(anyString())).thenReturn(jsonMock);
        // run
        List<CardInPack> results = netrunnerDBBroker.loadCards(testData.packTestSet);
        // validate
        assertEquals(3, results.size(), "Wrong number of test cards returned");
        Optional<CardInPack> card1 = results.stream().filter(x -> x.getCode().equals(testData.testCardInPack1.getCode())).findFirst();
        Optional<CardInPack> card2 = results.stream().filter(x -> x.getCode().equals(testData.testCardInPack2.getCode())).findFirst();
        Optional<CardInPack> card3 = results.stream().filter(x -> x.getCode().equals(testData.testCardInPack3.getCode())).findFirst();
        assertTrue(card1.isPresent(), "Test card not found:" + testData.testCardInPack1.toString());
        assertTrue(card2.isPresent(), "Test card not found:" + testData.testCardInPack2.toString());
        assertTrue(card3.isPresent(), "Test card not found:" + testData.testCardInPack3.toString());
        assertEquals(testData.testCardInPack1, card1.get(), "Test card in pack data is incorrect");
        assertEquals(testData.testCardInPack2, card2.get(), "Test card in pack data is incorrect");
        assertEquals(testData.testCardInPack3, card3.get(), "Test card in pack data is incorrect");
        assertEquals(testData.testCard1, card1.get().getCard(), "Test card data is incorrect");
        assertEquals(testData.testCard2, card2.get().getCard(), "Test card data is incorrect");
        assertEquals(testData.testCard1, card3.get().getCard(), "Test card data is incorrect");
    }

    @Test
    public void testMWLLoad() {
        // setup
        JsonElement jsonMock = testHelper.getJsonFromTestResource("MockNetrunnerDBMWLs.json");
        Mockito.when(httpBroker.readJSONFromURL(anyString())).thenReturn(jsonMock);
        // run
        Set<MWL> results = netrunnerDBBroker.loadMWL(testData.cardInPackTestSet);
        // validate
        assertEquals(1, results.size(), "Wrong number of MWL items returned");
        Optional<MWL> mwl = results.stream().filter(x -> x.getCode().equals(testData.testMwl.getCode())).findFirst();
        assertTrue(mwl.isPresent(), "Test MWL not found");
        assertEquals(testData.testMwl, mwl.get(), "MWL data is incorrect");
        MWLCard mwlCard = mwl.get().getCards().stream().filter(x -> x.getCard().getTitle().equals(testData.testMWLCard.getCard().getTitle())).findFirst().get();
        assertEquals(testData.testMWLCard, mwlCard, "MWL card data is incorrect");
    }
    
}