package com.madarasz.knowthemeta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.database.DOs.Standing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ABRBrokerMatchTests {
    @Autowired ABRBroker abrBroker;

    // test NRTM v1.53 match output with bye and no top-cut
    // https://alwaysberunning.net/tournaments/2387
    // https://alwaysberunning.net/tjsons/2387.json
    @Test
    void testLoadMatchesNRTM153WithByeNoTopcut() {
        List<Standing> standings = abrBroker.loadMatches(2387);
        assertEquals(10, standings.size());
        // Claudiu/hobbes #1 has 1 draw
        Standing player1Runner = standings.stream().filter(x -> x.getPlayerId() == 1 && x.getIsRunner()).findFirst().get();
        Standing player1Corp = standings.stream().filter(x -> x.getPlayerId() == 1 && !x.getIsRunner()).findFirst().get();
        assertEquals(2, player1Runner.getWinCount(), "Claudiu/hobbes runner win count");
        assertEquals(1, player1Runner.getDrawCount(), "Claudiu/hobbes runner draw count");
        assertEquals(0, player1Runner.getLossCount(), "Claudiu/hobbes runner loss count");
        assertEquals(2, player1Corp.getWinCount(), "Claudiu/hobbes corp win count");
        assertEquals(0, player1Corp.getDrawCount(), "Claudiu/hobbes corp draw count");
        assertEquals(1, player1Corp.getLossCount(), "Claudiu/hobbes corp loss count");
        // Silviu/moks #2 has 1 bye round
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 2 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 2 && !x.getIsRunner()).findFirst().get();
        assertEquals(1, player2Runner.getWinCount(), "Silviu/moks runner win count");
        assertEquals(0, player2Runner.getDrawCount(), "Silviu/moks runner draw count");
        assertEquals(1, player2Runner.getLossCount(), "Silviu/moks runner loss count");
        assertEquals(1, player2Corp.getWinCount(), "Silviu/moks corp win count");
        assertEquals(0, player2Corp.getDrawCount(), "Silviu/moks corp draw count");
        assertEquals(1, player2Corp.getLossCount(), "Silviu/moks corp loss count");
        // Ionuts #3 has 1 bye round, loses everything
        Standing player3Runner = standings.stream().filter(x -> x.getPlayerId() == 3 && x.getIsRunner()).findFirst().get();
        Standing player3Corp = standings.stream().filter(x -> x.getPlayerId() == 3 && !x.getIsRunner()).findFirst().get();
        assertEquals(0, player3Runner.getWinCount(), "Ionuts runner win count");
        assertEquals(0, player3Runner.getDrawCount(), "Ionuts runner draw count");
        assertEquals(2, player3Runner.getLossCount(), "Ionuts runner loss count");
        assertEquals(0, player3Corp.getWinCount(), "Ionuts corp win count");
        assertEquals(0, player3Corp.getDrawCount(), "Ionuts corp draw count");
        assertEquals(2, player3Corp.getLossCount(), "Ionuts corp loss count");
    }
}