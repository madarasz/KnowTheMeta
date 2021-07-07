package com.madarasz.knowthemeta.brokers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.helper.Searcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ABRBroker {

    private final HttpBroker httpBroker;
    private final NetrunnerDBBroker netrunnerDBBroker;
    private final Searcher searcher;
    public final static String ABR_TOURNAMENT_API_URL = "https://alwaysberunning.net/api/tournaments?concluded=1&approved=1&format=standard&cardpool={CARDPOOL_CODE}&mwl_id={MWL_ID}";
    public final static String ABR_STANDING_API_URL = "https://alwaysberunning.net/api/entries?id=";
    public final static String ABR_MATCHES_URL = "https://alwaysberunning.net/tjsons/{TOURNAMENT_ID}.json";
    private final static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy.MM.dd.").create();
    private final static Logger log = LoggerFactory.getLogger(ABRBroker.class);
    private final static Pattern deckUrlPattern = Pattern.compile("https://netrunnerdb.com/en/decklist/(\\d+)");

    public ABRBroker(HttpBroker httpBroker, NetrunnerDBBroker netrunnerDBBroker, Searcher searcher) {
        this.httpBroker = httpBroker;
        this.netrunnerDBBroker = netrunnerDBBroker;
        this.searcher = searcher;
    }

    public List<Tournament> getTournamentData(Meta meta) {
        log.info("Getting ABR tournament data for meta: " + meta.getTitle());
        JsonArray tournamentData = new JsonArray();

        // read JSON
        try {
            tournamentData = httpBroker
                    .readJSONFromURL(
                            ABR_TOURNAMENT_API_URL.replaceAll("\\{CARDPOOL_CODE\\}", meta.getCardpool().getCode())
                                    .replaceAll("\\{MWL_ID\\}", Integer.toString(meta.getMwl().getId())))
                    .getAsJsonArray();
        } catch (Exception e) {
            log.error("Cannot read tournaments JSON from ABR");
            return new ArrayList<Tournament>();
        }

        // parse
        Type collectionType = new TypeToken<List<Tournament>>() {
        }.getType();
        List<Tournament> result = gson.fromJson(tournamentData.toString(), collectionType);
        for (Tournament tournament : result) {
            tournament.setMeta(meta);
        }
        return result;
    }

    public List<Standing> getStadingData(Tournament tournament, Set<CardInPack> identities, Set<CardInPack> cards,
            Set<Deck> existingDecks) {
        List<Standing> result = new ArrayList<Standing>();
        JsonArray standingData = new JsonArray();

        // read JSON
        try {
            standingData = httpBroker.readJSONFromURL(ABR_STANDING_API_URL + tournament.getId()).getAsJsonArray();
        } catch (Exception e) {
            log.error("Cannot read standings JSON for tournament #" + tournament.getId() + " from ABR");
            return result;
        }

        // iterate on items
        standingData.forEach(item -> {
            JsonObject stadingItem = (JsonObject) item;
            int rank = (stadingItem.get("rank_top").isJsonNull()) ? stadingItem.get("rank_swiss").getAsInt()
                    : stadingItem.get("rank_top").getAsInt();
            String runnerId = stadingItem.get("runner_deck_identity_id").getAsString();
            String corpId = stadingItem.get("corp_deck_identity_id").getAsString();
            Card runner = searcher.getCardByCode(identities, runnerId);
            Card corp = searcher.getCardByCode(identities, corpId);
            String runnerDeckUrl = stadingItem.get("runner_deck_url").getAsString();
            String corpDeckUrl = stadingItem.get("corp_deck_url").getAsString();
            // TODO leave deckloading for MetaOperations
            if (runnerDeckUrl.length() > 0) {
                Deck runnerDeck = netrunnerDBBroker.loadDeck(this.deckIdFromUrl(runnerDeckUrl), existingDecks, cards);
                result.add(new Standing(tournament, runner, runnerDeck, rank, true));
            } else {
                result.add(new Standing(tournament, runner, rank, true));
            }
            if (corpDeckUrl.length() > 0) {
                Deck corpDeck = netrunnerDBBroker.loadDeck(this.deckIdFromUrl(corpDeckUrl), existingDecks, cards);
                result.add(new Standing(tournament, corp, corpDeck, rank, false));
            } else {
                result.add(new Standing(tournament, corp, rank, false));
            }
        });
        return result;
    }

    public Set<Standing> loadMatches(int tournamentId) {
        Set<Standing> stadings = new HashSet<Standing>();
        JsonObject matchData = new JsonObject();

        // read JSON
        try {
            matchData = httpBroker
                    .readJSONFromURL(
                            ABR_MATCHES_URL.replaceAll("\\{TOURNAMENT_ID\\}", String.valueOf(tournamentId)))
                    .getAsJsonObject();
        } catch (Exception e) {
            log.error("Cannot read matches JSON for tournament #" + tournamentId + " from ABR");
            return stadings;
        }

        // if there are no matches to parse
        if (!matchData.has("rounds")) {
            return stadings;
        }

        // parse players
        matchData.get("players").getAsJsonArray().forEach(playerItem -> readMatchPlayers(stadings, playerItem.getAsJsonObject()));
        // pase rounds
        matchData.get("rounds").getAsJsonArray().forEach(roundItem -> {
            // pase matches
            log.trace("--- New round ---");
            roundItem.getAsJsonArray().forEach(matchItem -> readMatchRounds(stadings, matchItem.getAsJsonObject()));
        });
        // result logging
        for (Standing standing : stadings) {
            log.trace("Player id:" + standing.getPlayerId() + " isRunner:" + standing.getIsRunner() + 
                " wins:" + standing.getWinCount() + " draws:" + standing.getDrawCount() + " losses:" + standing.getLossCount());
        }
        return stadings;
    }

    private void readMatchRounds(Set<Standing> stadings, JsonObject matchItem) {
        // get player Ids
        // log.trace("Table #" + (matchItem.get("table").isJsonNull() ? "null" : matchItem.get("table").getAsString()));

        // Cobra.ai
        if (matchItem.has("player1") && matchItem.has("player2")) {
            JsonObject player1 = matchItem.get("player1").getAsJsonObject();
            JsonObject player2 = matchItem.get("player2").getAsJsonObject();
            int player1Id = player1.get("id").isJsonNull() ? 0 : player1.get("id").getAsInt();
            int player2Id = player2.get("id").isJsonNull() ? 0 : player2.get("id").getAsInt();
            // check if it was bye or intentional draw (does not count for stats)
            if (matchItem.has("intentionalDraw") && (matchItem.get("intentionalDraw").getAsBoolean() || player1Id == 0 || player2Id == 0)) {
                // bye or intentionalDraw
                log.trace("bye or intentional draw");
            } else {
                // get player standing objects
                Standing player1Runner = stadings.stream().filter(x -> x.getPlayerId() == player1Id && x.getIsRunner()).findFirst().get();
                Standing player1Corp = stadings.stream().filter(x -> x.getPlayerId() == player1Id && !x.getIsRunner()).findFirst().get();
                Standing player2Runner = stadings.stream().filter(x -> x.getPlayerId() == player2Id && x.getIsRunner()).findFirst().get();
                Standing player2Corp = stadings.stream().filter(x -> x.getPlayerId() == player2Id && !x.getIsRunner()).findFirst().get();                
                if (matchItem.get("eliminationGame").getAsBoolean()) {
                    // top cut
                    readMatchTopCut(player1, player2, player1Id, player2Id, player1Runner, player1Corp, player2Runner, player2Corp);
                } else {
                    // swiss
                    readMatchSwiss(player1, player2, player1Runner, player1Corp, player2Runner, player2Corp);
                }
            }
        }

        // sass https://github.com/Chemscribbler/sass
        else if (matchItem.has("corp") && matchItem.has("runner")) {
            JsonObject corp = matchItem.get("corp").getAsJsonObject();
            JsonObject runner = matchItem.get("runner").getAsJsonObject();
            int corpId = corp.get("id").getAsInt();
            int runnerId = runner.get("id").getAsInt();
            int corpScore = corp.get("score").getAsInt();
            int runnerScore = runner.get("score").getAsInt();
            Standing corpPlayer = stadings.stream().filter(x -> x.getPlayerId() == corpId && !x.getIsRunner()).findFirst().get();
            Standing runnerPlayer = stadings.stream().filter(x -> x.getPlayerId() == runnerId && x.getIsRunner()).findFirst().get();
            if (corpScore > runnerScore) {
                corpPlayer.incWinCount();
                runnerPlayer.incLossCount();
            } else if (runnerScore > corpScore) {
                corpPlayer.incLossCount();
                runnerPlayer.incWinCount();
            } else {
                corpPlayer.incDrawCount();
                runnerPlayer.incDrawCount();
            }
        } else {
            log.error("Could not read match");
        }
    }

    private void readMatchSwiss(JsonObject player1, JsonObject player2, Standing player1Runner, Standing player1Corp,
            Standing player2Runner, Standing player2Corp) {
        // get scores
        int player1RunnerScore = (!player1.has("runnerScore") || player1.get("runnerScore").isJsonNull()) ? 0 :  player1.get("runnerScore").getAsInt();
        int player2RunnerScore = (!player2.has("runnerScore") || player2.get("runnerScore").isJsonNull()) ? 0 :  player2.get("runnerScore").getAsInt();
        int player1CorpScore = (!player1.has("corpScore") || player1.get("corpScore").isJsonNull()) ? 0 :  player1.get("corpScore").getAsInt();
        int player2CorpScore = (!player1.has("corpScore") || player2.get("corpScore").isJsonNull()) ? 0 :  player2.get("corpScore").getAsInt();
        // decide if NRTM or Cobr.ai, apply match scores
        if (player1.has("combinedScore") && player2.has("combinedScore") 
                && !player1.get("combinedScore").isJsonNull() && !player2.get("combinedScore").isJsonNull()) {
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

    private void readMatchTopCut(JsonObject player1, JsonObject player2, int player1Id, int player2Id, Standing player1Runner,
            Standing player1Corp, Standing player2Runner, Standing player2Corp) {
        if ((!player1.get("winner").isJsonNull()) && (!player2.get("winner").isJsonNull()) && (!player1.get("role").isJsonNull()) && (!player2.get("role").isJsonNull())) {
            // check which player played which side
            Standing player1Role = player1.get("role").getAsString().equals("corp") ? player1Corp : player1Runner;
            Standing player2Role = player2.get("role").getAsString().equals("corp") ? player2Corp : player2Runner;
            // apply match scores
            if (player1.get("winner").getAsBoolean()) {
                log.trace("Top-cut, player #" + player1Id + " wins");
                player1Role.incWinCount();
                player2Role.incLossCount();
            } else {
                log.trace("Top-cut, player #" + player2Id + " wins");
                player1Role.incLossCount();
                player2Role.incWinCount();
            }
        }
    }

    private void readMatchPlayers(Set<Standing> stadings, JsonObject playerData) {
        int playerId = playerData.get("id").getAsInt();
        int rank = playerData.get("rank").getAsInt();
        stadings.add(new Standing(true, rank, playerId));
        stadings.add(new Standing(false, rank, playerId));
        log.trace("New match player " + playerData.get("name").getAsString() + " no" + rank + " #" + playerId);
    }

    // apply match outcome if there is a combinedScore field (Cobr.ai)
    private void applyMatch(int runnerScore, int corpScore, int combinedScore, Standing runner, Standing corp) {
        if (combinedScore != runnerScore + corpScore) {
            // old school cobr.ai
            if (combinedScore == 6) {
                log.trace("Player #" + runner.getPlayerId() + " wins both");
                runner.incWinCount();
                corp.incWinCount();
            } else {
                log.trace("Could not figure out results");
            }
        } else {
            applyMatch(runnerScore, corpScore, runner, corp);   // gets executed even if combinedScore = 0
        }
    }

    // apply match outcome based on runnerScore and corpScore fields (NRTM, newer Cobr.ai)
    private void applyMatch(int runnerScore, int corpScore, Standing runner, Standing corp) {
        applyMatch(runnerScore, runner);
        applyMatch(corpScore, corp);
    }

    // apply match outcome based on a score field
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
            default:
                log.error("Player #" + standing.getPlayerId() + " with unexpected score:" + score);
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