package com.madarasz.knowthemeta.database.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.madarasz.knowthemeta.database.DOs.Deck;

public class DeckMinimalSerializer extends StdSerializer<Deck> {

    private static final long serialVersionUID = -2691032733046519786L;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DeckMinimalSerializer() {
        this(null);
    }

    public DeckMinimalSerializer(Class<Deck> t) {
        super(t);
    }   

    @Override
    public void serialize(Deck value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        if (value.getPlayer() != null) {
            gen.writeStringField("player", value.getPlayer().getUser_name()); // TODO: not getting it
        }
        gen.writeStringField("title", value.getName());
        gen.writeStringField("date_update", simpleDateFormat.format(value.getDate_update()));
        gen.writeEndObject();
    }
    
}