package com.madarasz.knowthemeta;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.helper.TestData;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.text.ParseException;

@SpringBootTest
public class OperationsNetrunnerDBTests {

    @Spy NetrunnerDBBroker netrunnerDBBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired CardInPackRepository cardInPackRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired Operations operations;

    public static final TestData testData = new TestData();

    @Test
    @Transactional
    public void testNetrunnerDBUpdate() throws ParseException {
        // setup
        doReturn(testData.cycleTestSet).when(netrunnerDBBroker).loadCycles();
        doReturn(testData.packTestSet).when(netrunnerDBBroker).loadPacks(any());
        doReturn(testData.cardInPackTestSet).when(netrunnerDBBroker).loadCards(any());
        doReturn(testData.mwlTestSet).when(netrunnerDBBroker).loadMWL(any());
        operations.setNetrunnerDBBroker(netrunnerDBBroker);

        // run
        operations.updateFromNetrunnerDB();

        // assert cycles
        CardCycle foundCycle1 = cardCycleRepository.findByCode(testData.testCycle1.getCode());
        CardCycle foundCycle2 = cardCycleRepository.findByCode(testData.testCycle2.getCode());
        assertNotNull(foundCycle1, "Test cycle missing");
        assertNotNull(foundCycle2, "Test cycle missing");
        assertEquals(testData.testCycle1, foundCycle1, "Test cycle data is incorrect");
        assertEquals(testData.testCycle2, foundCycle2, "Test cycle data is incorrect");
        // assert packs
        CardPack foundPack1 = cardPackRepository.findByCode(testData.testPack1.getCode());
        CardPack foundPack2 = cardPackRepository.findByCode(testData.testPack2.getCode());
        CardPack foundPack3 = cardPackRepository.findByCode(testData.testPack3.getCode());
        assertNotNull(foundPack1, "Test pack missing");
        assertNotNull(foundPack2, "Test pack missing");
        assertNotNull(foundPack3, "Test pack missing");
        assertEquals(testData.testPack1, foundPack1, "Test pack data is incorrect");
        assertEquals(testData.testPack2, foundPack2, "Test pack data is incorrect");
        assertEquals(testData.testPack3, foundPack3, "Test pack data is incorrect");
        // assert cards
        Card foundCard1 = cardRepository.findByTitle(testData.testCard1.getTitle());
        Card foundCard2 = cardRepository.findByTitle(testData.testCard2.getTitle());
        assertNotNull(foundCard1, "Test card missing");
        assertNotNull(foundCard2, "Test card missing");
        assertEquals(testData.testCard1, foundCard1, "Test card data is incorrect");
        assertEquals(testData.testCard2, foundCard2, "Test card data is incorrect");
        // assert cards in pack
        CardInPack foundCardInPack1 = cardInPackRepository.findByCode(testData.testCardInPack1.getCode());
        CardInPack foundCardInPack2 = cardInPackRepository.findByCode(testData.testCardInPack2.getCode());
        CardInPack foundCardInPack3 = cardInPackRepository.findByCode(testData.testCardInPack3.getCode());
        assertNotNull(foundCardInPack1, "Test card in pack missing");
        assertNotNull(foundCardInPack2, "Test card in pack missing");
        assertNotNull(foundCardInPack3, "Test card in pack missing");
        assertEquals(testData.testCardInPack1, foundCardInPack1, "Test card in pack data is incorrect");
        assertEquals(testData.testCardInPack2, foundCardInPack2, "Test card in pack data is incorrect");
        assertEquals(testData.testCardInPack3, foundCardInPack3, "Test card in pack data is incorrect");
        // assert MWL
        MWL foundMwl = mwlRepository.findByCode(testData.testMwl.getCode());
        assertNotNull(foundMwl, "Test MWL missing");
        assertEquals(testData.testMwl, foundMwl, "Test MWL data is incorrect");
        // assert cycle-pack relationship
        assertTrue(cardPackRepository.verifyPackInCycleRelationship(testData.testCycle1.getCode(), testData.testPack1.getCode()));
        assertTrue(cardPackRepository.verifyPackInCycleRelationship(testData.testCycle1.getCode(), testData.testPack2.getCode()));
        assertTrue(cardPackRepository.verifyPackInCycleRelationship(testData.testCycle2.getCode(), testData.testPack3.getCode()));
        // assert card-cardinpack relationship
        assertTrue(cardInPackRepository.verifyCardInPackRelationship(testData.testCard1.getTitle(), testData.testCardInPack1.getCode()));
        assertTrue(cardInPackRepository.verifyCardInPackRelationship(testData.testCard2.getTitle(), testData.testCardInPack2.getCode()));
        assertTrue(cardInPackRepository.verifyCardInPackRelationship(testData.testCard1.getTitle(), testData.testCardInPack3.getCode()));
        // assert card-mwl relationship
        assertTrue(mwlRepository.verifyCardInMWLRelationship(testData.testCard1.getTitle(), testData.testMwl.getCode()));
    }
}