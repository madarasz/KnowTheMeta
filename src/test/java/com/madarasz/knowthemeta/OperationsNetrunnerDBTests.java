package com.madarasz.knowthemeta;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.helper.TestHelper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class OperationsNetrunnerDBTests {

    @Spy NetrunnerDBBroker netrunnerDBBroker;
    @Spy CardCycleRepository cardCycleRepository;
    @InjectMocks Operations operations;

    private static final TestHelper testHelper = new TestHelper();
    private static Driver driver;

    @BeforeAll
    private static void init() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "root"));
    }

    @AfterAll
    private static void tearDown() {
        driver.close();
    }

    @Test
    public void testNetrunnerDBUpdate() {
        // setup
        Set<CardCycle> cycleTestSet = new HashSet<CardCycle>();
        cycleTestSet.add(new CardCycle("test", "First Test Cycle", 9998, true));
        doReturn(cycleTestSet).when(netrunnerDBBroker).loadCycles();
        operations.updateFromNetrunnerDB();
        verify(cardCycleRepository, times(1)).save(any(CardCycle.class));
        assertEquals("First Test Cycle", testHelper.getQueryStringResult(driver, "MATCH (a:CardCycle {code: \"test\"}) RETURN a.name"));    // node does not get persisted :(
    }
}