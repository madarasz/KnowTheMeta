package com.madarasz.knowthemeta.database.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class IdentitySerializer extends StdSerializer<Card> {

    @Autowired CardInPackRepository cardInPackRepository;

    private static final long serialVersionUID = 1456456253465L;

    public IdentitySerializer() {
        this(null);
    }

    public IdentitySerializer(Class<Card> t) {
        super(t);
    }

    @Override
    public void serialize(Card value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("title", value.getTitle());
        gen.writeStringField("factionCode", value.getFaction().getFactionCode());
        CardInPack cardInPack = cardInPackRepository.findAllByTitle(value.getTitle()).get(0);
        gen.writeStringField("code", cardInPack.getCode());
        gen.writeEndObject();
    }
    
}