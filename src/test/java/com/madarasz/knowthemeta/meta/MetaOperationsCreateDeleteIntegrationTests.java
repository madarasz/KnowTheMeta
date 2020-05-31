package com.madarasz.knowthemeta.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.helper.TestData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for addMeta and deteMeta methods of MetaOperations with DB verification
 */
@SpringBootTest
public class MetaOperationsCreateDeleteIntegrationTests {
    @Autowired MetaOperations metaOperations;
    @Autowired MetaRepository metaRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired MWLRepository mwlRepository;
    private static final TestData testData = new TestData();
    
    @Test
    @Transactional
    public void testMetaCreateAndDelete() {
        // setup
        cardPackRepository.save(testData.testMeta.getCardpool());
        mwlRepository.save(testData.testMeta.getMwl());
        // add meta
        metaOperations.addMeta(testData.testMeta.getMwl().getCode(), testData.testMeta.getCardpool().getCode(), testData.testMeta.getNewCards(), testData.testMeta.getTitle());
        // verify data
        Meta foundMeta = metaRepository.findByTitle(testData.testMeta.getTitle());
        assertNotNull(foundMeta, "Meta not found");
        assertEquals(testData.testMeta, foundMeta, "Meta data not correct");
        // delete meta
        metaOperations.deleteMeta(foundMeta.getTitle());
        // verify meta delated
        assertNull(metaRepository.findByTitle(testData.testMeta.getTitle()), "Meta not deleted");
    }
}