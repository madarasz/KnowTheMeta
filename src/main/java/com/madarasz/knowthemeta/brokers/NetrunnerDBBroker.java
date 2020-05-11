package com.madarasz.knowthemeta.brokers;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.Faction;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.User;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInDeck;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DOs.relationships.MWLCard;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private final static Logger log = LoggerFactory.getLogger(NetrunnerDBBroker.class);
    private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy.MM.dd.").create();

    // loads Cycles from NetrunnerDB
    public Set<CardCycle> loadCycles() {
        log.info("Loading cycles");
        JsonObject cycleData = new JsonObject();

        // load JSON
        try {
            cycleData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cycles").getAsJsonObject();
        } catch (Exception e) {
            log.error("Cannot read cycles from NetrunnerDB");
            return new HashSet<CardCycle>();
        }

        // parse
        Type collectionType = new TypeToken<Set<CardCycle>>() {}.getType();
        return gson.fromJson(cycleData.get("data").toString(), collectionType);
    }

    // loads Packs from NetrunnerDB
    public Set<CardPack> loadPacks(Set<CardCycle> cycles) {
        log.info("Loading packs");
        Set<CardPack> results = new HashSet<CardPack>();
        JsonObject packData = new JsonObject();

        // read JSON
        try {
            packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "packs").getAsJsonObject();
        } catch (Exception e) {
            log.error("Cannot read packs from NetrunnerDB");
            return results;
        }

        // parse
        packData.get("data").getAsJsonArray().forEach(item -> {
            JsonObject packItem = (JsonObject) item;
            String cycleCode = packItem.get("cycle_code").getAsString();
            CardCycle cycle = cycles.stream().filter(x -> x.getCode().equals(cycleCode)).findFirst().get();
            CardPack pack = gson.fromJson(item, CardPack.class);
            pack.setCycle(cycle);
            results.add(pack);
        });
        return results;
    }

    // loads Cards from NetrunnerDB
    public List<CardInPack> loadCards(Set<CardPack> packs) {
        log.info("Loading cards");
        List<CardInPack> results = new ArrayList<CardInPack>();

        // load JSON
        JsonObject packData = new JsonObject();
        try {
            packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "cards").getAsJsonObject();
        } catch (Exception e) {
            log.error("Cannot read cards from NetrunnerDB");
            return results;
        }
        String imageUrlTemplate = packData.get("imageUrlTemplate").getAsString();

        // parse
        packData.get("data").getAsJsonArray().forEach(item -> {
            // get fields
            JsonObject packItem = (JsonObject) item;
            String code = packItem.get("code").getAsString();
            String packCode = packItem.get("pack_code").getAsString();
            String imageUrl = packItem.has("image_url") ? packItem.get("image_url").getAsString()
                    : imageUrlTemplate.replaceAll("\\{code\\}", code);
            Optional<CardPack> foundPack = packs.stream().filter(x -> x.getCode().equals(packCode)).findFirst();
            CardPack pack = new CardPack();
            if (foundPack.isPresent()) {
                pack = foundPack.get();
            } else {
                log.error("No cardpack found for: " + packCode);
            }
            Card card = gson.fromJson(item, Card.class);
            card.setFaction(new Faction(packItem.get("faction_code").getAsString(), "temp")); // adding temporary
                                                                                              // faction
            results.add(new CardInPack(card, pack, code, imageUrl));
        });

        return results;
    }

    public Set<MWL> loadMWL(List<CardInPack> cards) {
        log.info("Loading MWLs");
        Set<MWL> result = new HashSet<MWL>();

        // read JSON
        JsonObject packData = new JsonObject();
        try {
            packData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "mwl").getAsJsonObject();
        } catch (Exception e1) {
            log.error("Could not read MWL data from NetrunnerDB");
            return result;
        }

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
                MWLCard mwlCard = new MWLCard(mwl, cards.stream().filter(x -> x.getCode().equals(cardCode)).findFirst().get().getCard(), false, 0, false, false);
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

    public Deck loadDeck(int deckId, Set<Deck> existingDecks, Set<CardInPack> cards) {
        // check if already in DB
        Optional<Deck> exists = existingDecks.stream().filter(x -> x.getId() == deckId).findFirst();
        if (exists.isPresent()) {
            return exists.get();
        }

        // load deck JSON
        JsonObject deckData = new JsonObject();
        try {
            deckData = httpBroker.readJSONFromURL(NETRUNNERDB_API_URL + "decklist/" + deckId).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject();
        } catch (Exception ex) {
            log.error("Cannot read deck #" + deckId + " from NetrunnerDB");
            return null;
        }

        // parse
        Deck deck = gson.fromJson(deckData, Deck.class);
        deck.setPlayer(new User(deckData.get("user_id").getAsInt(), deckData.get("user_name").getAsString()));
        // iterate on cards
        for (Map.Entry<String, JsonElement> card : deckData.get("cards").getAsJsonObject().entrySet()) {
            String cardCode = card.getKey();
            int quantity = card.getValue().getAsInt();
            Card deckCard = cards.stream().filter(x -> x.getCode().equals(cardCode)).findFirst().get().getCard();
            if (deckCard.getType_code().equals("identity")) {
                // deck identity
                deck.setIdentity(deckCard);
            } else {
                // deck card
                deck.addCard(new CardInDeck(deck, deckCard, quantity));
            }
        }
        log.debug("Loaded deck: " + deck.toString());
        return deck;
    }
}