package com.madarasz.knowthemeta.database.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.stats.WinRateUsedCounter;
import com.madarasz.knowthemeta.database.DRs.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class WinRateUsedCounterSerializer extends StdSerializer<WinRateUsedCounter> {

    @Autowired CardRepository cardRepository;

    public WinRateUsedCounterSerializer() {
        this(null);
    }

    public WinRateUsedCounterSerializer(Class<WinRateUsedCounter> t) {
        super(t);
    }
    
    private static final long serialVersionUID = -2550415871512245385L;

    @Override
    public void serialize(WinRateUsedCounter value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Card card = (Card)value.getStatAbout();
        String title = card.getTitle();
        gen.writeStartObject();
        if (card.getFaction() != null) {
            gen.writeStringField("title", title);
            gen.writeStringField("code", cardRepository.getLastCode(title));
            gen.writeStringField("faction", card.getFaction().getFactionCode());
        }
        gen.writeNumberField("used", value.getUsedCounter());
        gen.writeNumberField("wins", value.getWinCounter());
        gen.writeNumberField("draws", value.getDrawCounter());
        gen.writeNumberField("losses", value.getLossCounter());
        if (!card.getType_code().equals("identity")) {
            gen.writeNumberField("avgPerDeck", value.getAvgPerDeck());
            gen.writeStringField("tags", value.getTags());
        }
        gen.writeEndObject();
    }
    
}