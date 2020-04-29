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
        assertEquals(2, player1Runner.getWinCount(), "Claudiu/hobbes runner win count is incorrect");
        assertEquals(1, player1Runner.getDrawCount(), "Claudiu/hobbes runner draw count is incorrect");
        assertEquals(0, player1Runner.getLossCount(), "Claudiu/hobbes runner loss count is incorrect");
        assertEquals(2, player1Corp.getWinCount(), "Claudiu/hobbes corp win count is incorrect");
        assertEquals(0, player1Corp.getDrawCount(), "Claudiu/hobbes corp draw count is incorrect");
        assertEquals(1, player1Corp.getLossCount(), "Claudiu/hobbes corp loss count is incorrect");
        // Silviu/moks #2 has 1 bye round
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 2 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 2 && !x.getIsRunner()).findFirst().get();
        assertEquals(1, player2Runner.getWinCount(), "Silviu/moks runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "Silviu/moks runner draw count is incorrect");
        assertEquals(1, player2Runner.getLossCount(), "Silviu/moks runner loss count is incorrect");
        assertEquals(1, player2Corp.getWinCount(), "Silviu/moks corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "Silviu/moks corp draw count is incorrect");
        assertEquals(1, player2Corp.getLossCount(), "Silviu/moks corp loss count is incorrect");
        // Ionuts #3 has 1 bye round, loses everything
        Standing player3Runner = standings.stream().filter(x -> x.getPlayerId() == 3 && x.getIsRunner()).findFirst().get();
        Standing player3Corp = standings.stream().filter(x -> x.getPlayerId() == 3 && !x.getIsRunner()).findFirst().get();
        assertEquals(0, player3Runner.getWinCount(), "Ionuts runner win count is incorrect");
        assertEquals(0, player3Runner.getDrawCount(), "Ionuts runner draw count is incorrect");
        assertEquals(2, player3Runner.getLossCount(), "Ionuts runner loss count is incorrect");
        assertEquals(0, player3Corp.getWinCount(), "Ionuts corp win count is incorrect");
        assertEquals(0, player3Corp.getDrawCount(), "Ionuts corp draw count is incorrect");
        assertEquals(2, player3Corp.getLossCount(), "Ionuts corp loss count is incorrect");
    }

    // test NRTM v1.54 match output with bye and top-cut, 15 players
    // https://alwaysberunning.net/tournaments/2245
    // https://alwaysberunning.net/tjsons/2245.json
    @Test
    void testLoadMatchesNRTM154WithByeTopcutIntentionalDraw() {
        List<Standing> standings = abrBroker.loadMatches(2245);
        assertEquals(30, standings.size());
        // winner Bailey/CowboyTintin #5 has intentional draw in round 4
        Standing player1Runner = standings.stream().filter(x -> x.getPlayerId() == 5 && x.getIsRunner()).findFirst().get();
        Standing player1Corp = standings.stream().filter(x -> x.getPlayerId() == 5 && !x.getIsRunner()).findFirst().get();
        assertEquals(3, player1Runner.getWinCount(), "Bailey/CowboyTintin runner win count is incorrect");
        assertEquals(0, player1Runner.getDrawCount(), "Bailey/CowboyTintin runner draw count is incorrect");
        assertEquals(1, player1Runner.getLossCount(), "Bailey/CowboyTintin runner loss count is incorrect");
        assertEquals(3, player1Corp.getWinCount(), "Bailey/CowboyTintin corp win count is incorrect");
        assertEquals(0, player1Corp.getDrawCount(), "Bailey/CowboyTintin corp draw count is incorrect");
        assertEquals(1, player1Corp.getLossCount(), "Bailey/CowboyTintin corp loss count is incorrect");
        // 4th Bryan #8 has intentional draw in round 4
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 8 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 8 && !x.getIsRunner()).findFirst().get();
        assertEquals(3, player2Runner.getWinCount(), "Bryan runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "Bryan runner draw count is incorrect");
        assertEquals(2, player2Runner.getLossCount(), "Bryan runner loss count is incorrect");
        assertEquals(2, player2Corp.getWinCount(), "Bryan corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "Bryan corp draw count is incorrect");
        assertEquals(1, player2Corp.getLossCount(), "Bryan corp loss count is incorrect");
        // 10th Connor #12 swiss only
        Standing player3Runner = standings.stream().filter(x -> x.getPlayerId() == 12 && x.getIsRunner()).findFirst().get();
        Standing player3Corp = standings.stream().filter(x -> x.getPlayerId() == 12 && !x.getIsRunner()).findFirst().get();
        assertEquals(1, player3Runner.getWinCount(), "Connor runner win count is incorrect");
        assertEquals(0, player3Runner.getDrawCount(), "Connor runner draw count is incorrect");
        assertEquals(3, player3Runner.getLossCount(), "Connor runner loss count is incorrect");
        assertEquals(3, player3Corp.getWinCount(), "Connor corp win count is incorrect");
        assertEquals(0, player3Corp.getDrawCount(), "Connor corp draw count is incorrect");
        assertEquals(1, player3Corp.getLossCount(), "Connor corp loss count is incorrect");
    }

    // test old Cobr.ai match output with bye and top-cut, 16 players
    // https://alwaysberunning.net/tournaments/2073
    // https://alwaysberunning.net/tjsons/2073.json
    @Test
    void testLoadMatchesOldCobraiWithByeTopcut() {
        List<Standing> standings = abrBroker.loadMatches(2073);
        assertEquals(32, standings.size());
        // winner Cauhita #10165, last 2 rounds unknown 
        Standing player1Runner = standings.stream().filter(x -> x.getPlayerId() == 10165 && x.getIsRunner()).findFirst().get();
        Standing player1Corp = standings.stream().filter(x -> x.getPlayerId() == 10165 && !x.getIsRunner()).findFirst().get();
        assertEquals(4, player1Runner.getWinCount(), "Cauhita runner win count is incorrect");
        assertEquals(0, player1Runner.getDrawCount(), "Cauhita runner draw count is incorrect");
        assertEquals(0, player1Runner.getLossCount(), "Cauhita runner loss count is incorrect");
        assertEquals(5, player1Corp.getWinCount(), "Cauhita corp win count is incorrect");
        assertEquals(0, player1Corp.getDrawCount(), "Cauhita corp draw count is incorrect");
        assertEquals(0, player1Corp.getLossCount(), "Cauhita corp loss count is incorrect");
        // 3rd Schwarzer Peter #10153 has bye in first round
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 10153 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 10153 && !x.getIsRunner()).findFirst().get();
        assertEquals(2, player2Runner.getWinCount(), "Schwarzer Peter runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "Schwarzer Peter runner draw count is incorrect");
        assertEquals(0, player2Runner.getLossCount(), "Schwarzer Peter runner loss count is incorrect");
        assertEquals(1, player2Corp.getWinCount(), "Schwarzer Peter corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "Schwarzer Peter corp draw count is incorrect");
        assertEquals(2, player2Corp.getLossCount(), "Schwarzer Peter corp loss count is incorrect");
        // // 10th Johnahex #10156 swiss only, has 1-4 result
        Standing player3Runner = standings.stream().filter(x -> x.getPlayerId() == 10156 && x.getIsRunner()).findFirst().get();
        Standing player3Corp = standings.stream().filter(x -> x.getPlayerId() == 10156 && !x.getIsRunner()).findFirst().get();
        assertEquals(2, player3Runner.getWinCount(), "Johnahex runner win count is incorrect");
        assertEquals(0, player3Runner.getDrawCount(), "Johnahex runner draw count is incorrect");
        assertEquals(1, player3Runner.getLossCount(), "Johnahex runner loss count is incorrect");
        assertEquals(2, player3Corp.getWinCount(), "Johnahex corp win count is incorrect");
        assertEquals(0, player3Corp.getDrawCount(), "Johnahex corp draw count is incorrect");
        assertEquals(1, player3Corp.getLossCount(), "Johnahex corp loss count is incorrect");
    }

    // test new Cobr.ai match output no bye and top-cut, 32 players
    // https://alwaysberunning.net/tournaments/2735
    // https://alwaysberunning.net/tjsons/2735.json
    @Test
    void testLoadMatchesNewCobraiTopcut() {
        List<Standing> standings = abrBroker.loadMatches(2735);
        assertEquals(64, standings.size());
        // winner internet #17616 
        Standing player1Runner = standings.stream().filter(x -> x.getPlayerId() == 17616 && x.getIsRunner()).findFirst().get();
        Standing player1Corp = standings.stream().filter(x -> x.getPlayerId() == 17616 && !x.getIsRunner()).findFirst().get();
        assertEquals(5, player1Runner.getWinCount(), "internet runner win count is incorrect");
        assertEquals(0, player1Runner.getDrawCount(), "internet runner draw count is incorrect");
        assertEquals(1, player1Runner.getLossCount(), "internet runner loss count is incorrect");
        assertEquals(5, player1Corp.getWinCount(), "internet corp win count is incorrect");
        assertEquals(0, player1Corp.getDrawCount(), "internet corp draw count is incorrect");
        assertEquals(1, player1Corp.getLossCount(), "internet corp loss count is incorrect");
        // 15th Diomedes #17607, swiss only
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 17607 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 17607 && !x.getIsRunner()).findFirst().get();
        assertEquals(1, player2Runner.getWinCount(), "Diomedes runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "Diomedes runner draw count is incorrect");
        assertEquals(3, player2Runner.getLossCount(), "Diomedes runner loss count is incorrect");
        assertEquals(3, player2Corp.getWinCount(), "Diomedes corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "Diomedes corp draw count is incorrect");
        assertEquals(1, player2Corp.getLossCount(), "Diomedes corp loss count is incorrect");
    }

    // test new Cobr.ai match output no bye and no top-cut, 12 players, draw, mixed reporting (combined+detailed)
    // https://alwaysberunning.net/tournaments/2601
    // https://alwaysberunning.net/tjsons/2601.json
    @Test
    void testLoadMatchesNewCobraiNoTopcutDrawMixed() {
        List<Standing> standings = abrBroker.loadMatches(2601);
        assertEquals(24, standings.size());
        // winner Tradon #16032, combined reporting, draw 
        Standing player1Runner = standings.stream().filter(x -> x.getPlayerId() == 16032 && x.getIsRunner()).findFirst().get();
        Standing player1Corp = standings.stream().filter(x -> x.getPlayerId() == 16032 && !x.getIsRunner()).findFirst().get();
        assertEquals(2, player1Runner.getWinCount(), "Tradon runner win count is incorrect");
        assertEquals(0, player1Runner.getDrawCount(), "Tradon runner draw count is incorrect");
        assertEquals(0, player1Runner.getLossCount(), "Tradon runner loss count is incorrect");
        assertEquals(2, player1Corp.getWinCount(), "Tradon corp win count is incorrect");
        assertEquals(0, player1Corp.getDrawCount(), "Tradon corp draw count is incorrect");
        assertEquals(0, player1Corp.getLossCount(), "Tradon corp loss count is incorrect");
        // 4th ArminFirecracker #16838, detailed reporing
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 16838 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 16838 && !x.getIsRunner()).findFirst().get();
        assertEquals(1, player2Runner.getWinCount(), "ArminFirecracker runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "ArminFirecracker runner draw count is incorrect");
        assertEquals(1, player2Runner.getLossCount(), "ArminFirecracker runner loss count is incorrect");
        assertEquals(2, player2Corp.getWinCount(), "ArminFirecracker corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "ArminFirecracker corp draw count is incorrect");
        assertEquals(0, player2Corp.getLossCount(), "ArminFirecracker corp loss count is incorrect");
    }

    // test new Cobr.ai match output no bye and no top-cut, 5 players, draw, mixed reporting (combined+detailed)
    // https://alwaysberunning.net/tournaments/2564
    // https://alwaysberunning.net/tjsons/2564.json
    @Test
    void testLoadMatchesNewCobraiNoTopcutBye() {
        List<Standing> standings = abrBroker.loadMatches(2564);
        assertEquals(10, standings.size());
        // 2nd Tre #16643, detailed reporing
        Standing player2Runner = standings.stream().filter(x -> x.getPlayerId() == 16643 && x.getIsRunner()).findFirst().get();
        Standing player2Corp = standings.stream().filter(x -> x.getPlayerId() == 16643 && !x.getIsRunner()).findFirst().get();
        assertEquals(0, player2Runner.getWinCount(), "ArminFirecracker runner win count is incorrect");
        assertEquals(0, player2Runner.getDrawCount(), "ArminFirecracker runner draw count is incorrect");
        assertEquals(2, player2Runner.getLossCount(), "ArminFirecracker runner loss count is incorrect");
        assertEquals(2, player2Corp.getWinCount(), "ArminFirecracker corp win count is incorrect");
        assertEquals(0, player2Corp.getDrawCount(), "ArminFirecracker corp draw count is incorrect");
        assertEquals(0, player2Corp.getLossCount(), "ArminFirecracker corp loss count is incorrect");
    }
}