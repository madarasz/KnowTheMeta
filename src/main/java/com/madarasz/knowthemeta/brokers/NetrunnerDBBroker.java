package com.madarasz.knowthemeta.brokers;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
public class NetrunnerDBBroker {

    @Autowired HttpBroker httpBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired CardInPackRepository cardInPackRepository;

    private final static String NETRUNNERDB_API_URL = "https://netrunnerdb.com/api/2.0/public/";
    private final static String NETRUNNERDB_PRIVATEDECK_URL = "https://netrunnerdb.com/en/deck/view/";
    private final static String NETRUNNERDB_DECKLIST_URL = "https://netrunnerdb.com/en/decklist/";
    private final static Logger log = LoggerFactory.getLogger(NetrunnerDBBroker.class);
    private final static StopWatch stopwatch = new StopWatch();
    private int newCount;
    private int reprintCount;

    private final static Gson gson = new GsonBuilder().serializeNulls().create();

    // loads Cycles from NetrunnerDB
    public Set<CardCycle> loadCycles() {
        log.info("Loading cycles");
        JsonObject cycleData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cycles");
        Type collectionType = new TypeToken<Set<CardCycle>>() {
        }.getType();
        return gson.fromJson(cycleData.get("data").toString(), collectionType);
    }

    // loads Packs from NetrunnerDB
    public Set<CardPack> loadPacks() {
        log.info("Loading packs");
        JsonObject packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "packs");
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
        reprintCount = 0;

        JsonObject packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cards");
        String imageUrlTemplate = packData.get("imageUrlTemplate").getAsString();

        packData.get("data").getAsJsonArray().forEach(item -> {
            // get fields
            JsonObject packItem = (JsonObject) item;
            String title = packItem.get("title").getAsString();
            String code = packItem.get("code").getAsString();
            String packCode = packItem.get("pack_code").getAsString();
            String imageUrl = packItem.has("image_url") ? packItem.get("image_url").getAsString() : imageUrlTemplate.replaceAll("\\{code\\}", code);

            // get existing objects
            Card card = cardRepository.findByTitle(title);
            CardPack pack = cardPackRepository.findByCode(packCode);
            if (pack == null) {
                log.error(title + " - No pack found for code: " + packCode);
            }
            if (card == null) {
                // new card
                card = gson.fromJson(item, Card.class);
                pack.addCards(new CardInPack(card, pack, code, imageUrl));
                cardPackRepository.save(pack);
                newCount++;
                log.debug(String.format("New card: %s - %s", card.getTitle(), pack.getName()));
            } else {
                // card exists
                Card cardWithCode = cardRepository.findByCode(code);
                if (cardWithCode == null) {
                    // new reprint
                    pack.addCards(new CardInPack(card, pack, code, imageUrl));
                    cardPackRepository.save(pack);
                    reprintCount++;
                    log.debug(String.format("New reprint: %s - %s", card.getTitle(), pack.getName()));
                }
            }
        });

        // logging
        stopwatch.stop();
        if (newCount + reprintCount == 0) {
            log.info(String.format("Cards: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cards: %d new cards, %d reprints (%.3f sec)", newCount, reprintCount, stopwatch.getTotalTimeSeconds()));
        }
    }
}