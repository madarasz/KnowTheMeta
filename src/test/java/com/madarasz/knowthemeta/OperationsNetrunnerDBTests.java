package com.madarasz.knowthemeta;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class OperationsNetrunnerDBTests {

    @Spy NetrunnerDBBroker netrunnerDBBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired Operations operations;

    @Test
    @Transactional
    public void testNetrunnerDBUpdate() {
        // setup
        Set<CardCycle> cycleTestSet = new HashSet<CardCycle>();
        CardCycle cycleTest = new CardCycle("test", "First Test Cycle", 9998, true);
        cycleTestSet.add(cycleTest);
        ReflectionTestUtils.setField(operations, "netrunnerDBBroker", netrunnerDBBroker);
        doReturn(cycleTestSet).when(netrunnerDBBroker).loadCycles();
        operations.updateFromNetrunnerDB();
        assertNotNull(cardCycleRepository.findByCode(cycleTest.getCode()));
    }
}