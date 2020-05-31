package com.madarasz.knowthemeta.database.serializer;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

public class CardInPackSerializer extends StdSerializer<CardInPack> {

    private static final long serialVersionUID = -2691092723046819786L;

    public static String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[^\\p{Alnum}]+", "-");
    }

    public CardInPackSerializer() {
        this(null);
    }

    public CardInPackSerializer(Class<CardInPack> t) {
        super(t);
    }   

    @Override
    public void serialize(CardInPack value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("code", value.getCode());
        gen.writeStringField("image_url", value.getImage_url());
        gen.writeStringField("pack_title", value.getCardPack().getName());
        gen.writeEndObject();
    }
    
}