package com.madarasz.knowthemeta.brokers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DRs.queryresult.CardCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ABRBroker {

    @Autowired HttpBroker httpBroker;
    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    private final static String ABR_TOURNAMENT_API_URL = 
        "https://alwaysberunning.net/api/tournaments?concluded=1&approved=1&format=standard&cardpool={CARDPOOL_CODE}&mwl_id={MWL_ID}";
    private final static String ABR_STANDING_API_URL = "https://alwaysberunning.net/api/entries?id=";
    private final static String ABR_MATCHES_URL = "https://alwaysberunning.net/tjsons/{TOURNAMENT_ID}.json";
    private final static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy.MM.dd.").create();
    private final static Logger log = LoggerFactory.getLogger(ABRBroker.class);
    private final static Pattern deckUrlPattern = Pattern.compile("https://netrunnerdb.com/en/decklist/(\\d+)");

    public List<Tournament> getTournamentData(Meta meta) {
        log.info("Getting ABR tournament data for meta: " + meta.getTitle());
        JsonArray tournamentData = httpBroker.readJSONFromURL(ABR_TOURNAMENT_API_URL
            .replaceAll("\\{CARDPOOL_CODE\\}", meta.getCardpool().getCode()).replaceAll("\\{MWL_ID\\}", Integer.toString(meta.getMwl().getId()))).getAsJsonArray();
        Type collectionType = new TypeToken<List<Tournament>>(){}.getType();
        List<Tournament> result = gson.fromJson(tournamentData.toString(), collectionType);
        for (Tournament tournament : result) {
            tournament.setMeta(meta);
        }
        return result;
    }

    public List<Standing> getStadingData(Tournament tournament, List<CardCode> identities, List<Deck> existingDecks) {
        List<Standing> result = new ArrayList<Standing>();
        JsonArray standingData = httpBroker.readJSONFromURL(ABR_STANDING_API_URL+tournament.getId()).getAsJsonArray();

        // iterate on items
        standingData.forEach(item -> {
            JsonObject stadingItem = (JsonObject) item;
            int rank = (stadingItem.get("rank_top").isJsonNull()) ? stadingItem.get("rank_swiss").getAsInt() : stadingItem.get("rank_top").getAsInt();
            String runnerId = stadingItem.get("runner_deck_identity_id").getAsString();
            String corpId = stadingItem.get("corp_deck_identity_id").getAsString();
            Card runner = identities.stream().filter(id -> id.getCode().equals(runnerId)).findFirst().get().getCard();
            Card corp = identities.stream().filter(id -> id.getCode().equals(corpId)).findFirst().get().getCard();
            String runnerDeckUrl = stadingItem.get("runner_deck_url").getAsString();
            String corpDeckUrl = stadingItem.get("corp_deck_url").getAsString();
            if (runnerDeckUrl.length() > 0) {
                Deck runnerDeck = netrunnerDBBroker.loadDeck(this.deckIdFromUrl(runnerDeckUrl), existingDecks);
                result.add(new Standing(tournament, runner, runnerDeck, rank, true));
            } else {
                result.add(new Standing(tournament, runner, rank, true));
            } 
            if (corpDeckUrl.length() > 0) {
                Deck corpDeck = netrunnerDBBroker.loadDeck(this.deckIdFromUrl(corpDeckUrl), existingDecks);
                result.add(new Standing(tournament, corp, corpDeck, rank, false));
            } else {
                result.add(new Standing(tournament, corp, rank, false));
            }     
        });
        return result;
    }

    public List<Standing> loadMatches(int tournamentId) {
        List<Standing> stadings = new ArrayList<Standing>();
        JsonObject matchData = httpBroker.readJSONFromURL(ABR_MATCHES_URL.replaceAll("\\{TOURNAMENT_ID\\}", new Integer(tournamentId).toString())).getAsJsonObject();
        // read players
        matchData.get("players").getAsJsonArray().forEach(item -> {
            JsonObject playerData = item.getAsJsonObject();
            int playerId = playerData.get("id").getAsInt();
            int rank = playerData.get("rank").getAsInt();
            stadings.add(new Standing(true, rank, playerId));
            stadings.add(new Standing(false, rank, playerId));
            log.trace("New match player " + playerData.get("name").getAsString() + " no" + rank + " #" + playerId);
        });
        // read rounds
        matchData.get("rounds").getAsJsonArray().forEach(roundItem -> {
            // read matches
            log.trace("--- New round ---");
            roundItem.getAsJsonArray().forEach(item -> {
                JsonObject matchItem = item.getAsJsonObject();
                log.trace("Table #" + matchItem.get("table").getAsInt());
                JsonObject player1 = matchItem.get("player1").getAsJsonObject();
                JsonObject player2 = matchItem.get("player2").getAsJsonObject();
                int player1Id = player1.get("id").isJsonNull() ? 0 : player1.get("id").getAsInt();
                int player2Id = player2.get("id").isJsonNull() ? 0 : player2.get("id").getAsInt();
                if (matchItem.get("intentionalDraw").getAsBoolean() || player1Id == 0 || player2Id == 0) {
                    // bye or intentionalDraw
                    log.trace("bye or intentional draw");
                } else {
                    Standing player1Runner = stadings.stream().filter(x -> x.getPlayerId() == player1Id && x.getIsRunner()).findFirst().get();
                    Standing player1Corp = stadings.stream().filter(x -> x.getPlayerId() == player1Id && !x.getIsRunner()).findFirst().get();
                    Standing player2Runner = stadings.stream().filter(x -> x.getPlayerId() == player2Id && x.getIsRunner()).findFirst().get();
                    Standing player2Corp = stadings.stream().filter(x -> x.getPlayerId() == player2Id && !x.getIsRunner()).findFirst().get();                
                    if (matchItem.get("eliminationGame").getAsBoolean()) {
                        // top cut
                        Standing player1Role = player1.get("role").getAsString().equals("corp") ? player1Corp : player1Runner;
                        Standing player2Role = player2.get("role").getAsString().equals("corp") ? player2Corp : player2Runner;
                        if (player1.get("winner").getAsBoolean()) {
                            log.trace("Top-cut, player #" + player1Id + " wins");
                            player1Role.incWinCount();
                            player2Role.incLossCount();
                        } else {
                            log.trace("Top-cut, player #" + player2Id + " wins");
                            player1Role.incLossCount();
                            player2Role.incWinCount();
                        }
                    } else {
                        // swiss
                        int player1RunnerScore = player1.get("runnerScore").getAsInt();
                        int player2RunnerScore = player2.get("runnerScore").getAsInt();
                        int player1CorpScore = player1.get("corpScore").getAsInt();
                        int player2CorpScore = player2.get("corpScore").getAsInt();
                        if (player1.has("combinedScore") && player2.has("combinedScore")) {
                            // cobr.ai
                            int player1CombinedScore = player1.get("combinedScore").getAsInt();
                            int player2CombinedScore = player2.get("combinedScore").getAsInt();
                            applyMatch(player1RunnerScore, player1CorpScore, player1CombinedScore, player1Runner, player1Corp);
                            applyMatch(player2RunnerScore, player2CorpScore, player2CombinedScore, player2Runner, player2Corp);
                        } else {
                            // nrtm
                            applyMatch(player1RunnerScore, player1CorpScore, player1Runner, player1Corp);
                            applyMatch(player2RunnerScore, player2CorpScore, player2Runner, player2Corp);
                        }
                    }
                }
            });
        });
        for (Standing standing : stadings) {
            log.trace("Player id:" + standing.getPlayerId() + " isRunner:" + standing.getIsRunner() + 
                " wins:" + standing.getWinCount() + " draws:" + standing.getDrawCount() + " losses:" + standing.getLossCount());
        }
        return stadings;
    }

    private void applyMatch(int runnerScore, int corpScore, int combinedScore, Standing runner, Standing corp) {
        if (combinedScore != runnerScore + corpScore) {
            // old school cobr.ai
            if (combinedScore == 6) {
                log.trace("Player #" + runner.getPlayerId() + " wins both");
                runner.incWinCount();
                corp.incWinCount();
            } else if (combinedScore == 0) {
                log.trace("Player #" + runner.getPlayerId() + " loses both");
                runner.incLossCount();
                corp.incLossCount();
            } else {
                log.trace("Could not figure out results");
            }
        } else {
            applyMatch(runnerScore, corpScore, runner, corp);
        }
    }

    private void applyMatch(int runnerScore, int corpScore, Standing runner, Standing corp) {
        applyMatch(runnerScore, runner);
        applyMatch(corpScore, corp);
    }

    private void applyMatch(int score, Standing standing) {
        switch (score) {
            case 0:
                standing.incLossCount();
                log.trace("Player #" + standing.getPlayerId() + " loses with " + (standing.getIsRunner() ? "runner" : "corp"));
                break;
            case 1:
                standing.incDrawCount();
                log.trace("Player #" + standing.getPlayerId() + " draws with " + (standing.getIsRunner() ? "runner" : "corp"));
                break;
            case 3:
                standing.incWinCount();
                log.trace("Player #" + standing.getPlayerId() + " wins with " + (standing.getIsRunner() ? "runner" : "corp"));
                break;
        }
    }

    private int deckIdFromUrl(String url) {
        Matcher matcher = deckUrlPattern.matcher(url);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            log.error("Not suitable decklist URL:" + url);
            throw new IllegalArgumentException();
        }
    }
}