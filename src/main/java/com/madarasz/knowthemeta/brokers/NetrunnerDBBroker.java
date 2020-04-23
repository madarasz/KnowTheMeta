package com.madarasz.knowthemeta.brokers;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NetrunnerDBBroker {

    @Autowired HttpBroker httpBroker;
    @Autowired CardCycleRepository cardCycleRepository;

    private final static String NETRUNNERDB_API_URL = "https://netrunnerdb.com/api/2.0/public/";
    private final static String NETRUNNERDB_PRIVATEDECK_URL = "https://netrunnerdb.com/en/deck/view/";
    private final static String NETRUNNERDB_DECKLIST_URL = "https://netrunnerdb.com/en/decklist/";
    private final static Logger log = LoggerFactory.getLogger(NetrunnerDBBroker.class);

    private final static Gson gson = new Gson();

    public Set<CardCycle> loadCycles() {
        log.info("Loading cycles");
        JsonObject cycleData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cycles");
        Type collectionType = new TypeToken<Set<CardCycle>>(){}.getType();
        return gson.fromJson(cycleData.get("data").toString(), collectionType);
    }

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
}