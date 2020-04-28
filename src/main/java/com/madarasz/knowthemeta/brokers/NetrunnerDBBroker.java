package com.madarasz.knowthemeta.brokers;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DOs.relationships.MWLCard;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.queryresult.CardCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
public class NetrunnerDBBroker {

    @Autowired
    HttpBroker httpBroker;
    @Autowired
    CardCycleRepository cardCycleRepository;
    @Autowired
    CardPackRepository cardPackRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardInPackRepository cardInPackRepository;

    private final static String NETRUNNERDB_API_URL = "https://netrunnerdb.com/api/2.0/public/";
    private final static String NETRUNNERDB_PRIVATEDECK_URL = "https://netrunnerdb.com/en/deck/view/";
    private final static String NETRUNNERDB_DECKLIST_URL = "https://netrunnerdb.com/en/decklist/";
    private final static Logger log = LoggerFactory.getLogger(NetrunnerDBBroker.class);
    private final static StopWatch stopwatch = new StopWatch();
    private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private int newCount;
    private int editCount;

    private final static Gson gson = new GsonBuilder().serializeNulls().create();

    // loads Cycles from NetrunnerDB
    public Set<CardCycle> loadCycles() {
        log.info("Loading cycles");
        JsonObject cycleData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cycles").getAsJsonObject();
        Type collectionType = new TypeToken<Set<CardCycle>>() {}.getType();
        return gson.fromJson(cycleData.get("data").toString(), collectionType);
    }

    // loads Packs from NetrunnerDB
    public Set<CardPack> loadPacks() {
        log.info("Loading packs");
        JsonObject packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "packs").getAsJsonObject();
        Set<CardPack> results = new HashSet<CardPack>();
        packData.get("data").getAsJsonArray().forEach(item -> {
            JsonObject packItem = (JsonObject) item;
            String cycleCode = packItem.get("cycle_code").getAsString();
            CardCycle cycle = cardCycleRepository.findByCode(cycleCode);
            CardPack pack = gson.fromJson(item, CardPack.class);
            if (cycle != null) {
                pack.setCycle(cycle);
            } else {
                log.error("No cycle found for code: " + cycleCode);
            }
            results.add(pack);
        });
        return results;
    }

    // loads Cards from NetrunnerDB, ALSO performs DB update
    @Transactional
    public void loadCards() {
        log.info("Loading cards");
        stopwatch.start();
        newCount = 0;
        editCount = 0;

        JsonObject packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cards").getAsJsonObject();
        String imageUrlTemplate = packData.get("imageUrlTemplate").getAsString();
        List<CardCode> existingCards = cardRepository.listCards();

        packData.get("data").getAsJsonArray().forEach(item -> {
            // get fields
            JsonObject packItem = (JsonObject) item;
            String title = packItem.get("title").getAsString();
            String code = packItem.get("code").getAsString();
            String packCode = packItem.get("pack_code").getAsString();
            String imageUrl = packItem.has("image_url") ? packItem.get("image_url").getAsString()
                    : imageUrlTemplate.replaceAll("\\{code\\}", code);

            // get existing objects
            Optional<CardCode> cardExists = existingCards.stream().filter(x -> x.getCard().getTitle().equals(title)).findFirst();
            CardPack pack = cardPackRepository.findByCode(packCode);
            if (pack == null) {
                log.error(title + " - No pack found for code: " + packCode);
            }
            if (!cardExists.isPresent()) {
                // new card
                Card card = gson.fromJson(item, Card.class);
                pack.addCards(new CardInPack(card, pack, code, imageUrl));
                cardPackRepository.save(pack);
                newCount++;
                log.debug(String.format("New card: %s - %s", card.getTitle(), pack.getName()));
            } else {
                // card exists
                Optional<CardCode> printExists = existingCards.stream().filter(x -> x.getCode().equals(code)).findFirst();
                if (!printExists.isPresent()) {
                    // new reprint
                    Card card = cardExists.get().getCard();
                    pack.addCards(new CardInPack(card, pack, code, imageUrl));
                    cardPackRepository.save(pack);
                    editCount++;
                    log.debug(String.format("New reprint: %s - %s", card.getTitle(), pack.getName()));
                }
            }
        });

        // logging
        stopwatch.stop();
        if (newCount + editCount == 0) {
            log.info(String.format("Cards: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cards: %d new cards, %d reprints (%.3f sec)", newCount, editCount,
                    stopwatch.getTotalTimeSeconds()));
        }
    }

    public Set<MWL> loadMWL() {
        log.info("Loading MWLs");
        JsonObject packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "mwl").getAsJsonObject();
        Set<MWL> result = new HashSet<MWL>();

        // iterate on MWL entries
        packData.get("data").getAsJsonArray().forEach(item -> {
            JsonObject mwlItem = (JsonObject) item;
            String name = mwlItem.get("name").getAsString();
            String code = mwlItem.get("code").getAsString();
            Boolean active = mwlItem.get("active").getAsBoolean();
            String date_start_string = mwlItem.get("date_start").getAsString();
            int id = mwlItem.get("id").getAsInt();
            Date date_start = new Date();
            try {
                date_start = dateFormatter.parse(date_start_string);
            } catch (ParseException e) {
                log.error("Could not format MWL start date: " + date_start_string);
            }
            MWL mwl = new MWL(code, name, active, date_start, id);
            // iterate on cards
            for (Map.Entry<String, JsonElement> card : mwlItem.get("cards").getAsJsonObject().entrySet()) {
                String cardCode = card.getKey();
                JsonObject penalty = card.getValue().getAsJsonObject();
                MWLCard mwlCard = new MWLCard(mwl, cardRepository.findByCode(cardCode), false, 0, false, false);
                // update with penalty
                if (penalty.has("global_penalty")) mwlCard.setGlobal_penalty(true);
                if (penalty.has("universal_faction_cost")) mwlCard.setUniversal_faction_cost(penalty.get("universal_faction_cost").getAsInt());
                if (penalty.has("is_restricted")) mwlCard.setIs_restricted(true);
                if (penalty.has("deck_limit")) mwlCard.setIs_restricted(true);
                // add to MWL
                mwl.addCard(mwlCard);
            }
            result.add(mwl);
        });
        return result;
    }
}