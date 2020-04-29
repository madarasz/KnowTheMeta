package com.madarasz.knowthemeta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.brokers.HttpBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.helper.TestHelper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ABRBrokerTournamentDataTests {

    @Mock
    HttpBroker httpBroker;
    @InjectMocks
    ABRBroker abrBroker;

    private static final TestHelper testHelper = new TestHelper();

    @Test
    public void testLoadTournamentData() {
        // setup
        String input = testHelper.getTestResource("MockTournamentData.json");
        JsonElement testData = JsonParser.parseString(input);
        Mockito.when(httpBroker.readJSONFromURL(anyString())).thenReturn(testData);
        CardCycle cycle = new CardCycle("test", "test cycle", 1, false);
        CardPack pack = new CardPack("ur", "Uprising", 1, cycle, new Date());
        MWL mwl = new MWL("mwl", "test mwl", true, new Date(), 15);
        Meta meta = new Meta(pack, mwl, false, "test meta");

        // test
        List<Tournament> tournaments = abrBroker.getTournamentData(meta);

        // asserts
        assertEquals(2, tournaments.size(), "Tournament list size is not correct");
        assertEquals(2763, tournaments.get(0).getId(), "Tournament ID is incorrect");
        assertEquals("Hacktivist Tournament 4/28/2020", tournaments.get(0).getTitle(), "Tournament title is incorrect");
        Date correctDate = new Date();
        try {
            correctDate = new SimpleDateFormat("yyyy.MM.dd.").parse("2020.04.28.");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(correctDate, tournaments.get(0).getDate(), "Tournament date is incorrect");
        assertEquals(10, tournaments.get(0).getPlayers_count(), "Tournament ID is incorrect");
        assertEquals(4, tournaments.get(0).getTop_count(), "Tournament ID is incorrect");
        assertEquals(true, tournaments.get(0).getMatchdata(), "Tournament matchdata is incorrect");
        assertEquals(meta, tournaments.get(0).getMeta(), "Tournament meta is incorrect");
    }

}