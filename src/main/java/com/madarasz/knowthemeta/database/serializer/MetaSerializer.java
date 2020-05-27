package com.madarasz.knowthemeta.database.serializer;

import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.madarasz.knowthemeta.database.DOs.Meta;

public class MetaSerializer extends StdSerializer<Meta> {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[^\\p{Alnum}]+", "-");
    }

    public MetaSerializer() {
        this(null);
    }

    public MetaSerializer(Class<Meta> t) {
        super(t);
    }
    
    private static final long serialVersionUID = -2550415871512245385L;

    @Override
    public void serialize(Meta value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("title", value.getTitle());
        gen.writeStringField("cardpool", value.getCardpool().getName());
        gen.writeStringField("mwl", value.getMwl().getName());
        gen.writeBooleanField("newCards", value.getNewCards());
        gen.writeNumberField("tournaments", value.getTournamentCount());
        gen.writeNumberField("standings", value.getStandingsCount());
        gen.writeNumberField("runnerDecks", value.getRunnerDecksCount());
        gen.writeNumberField("corpDecks", value.getCorpDecksCount());
        gen.writeNumberField("matches", value.getMatchesCount());
        gen.writeStringField("file", toPrettyURL(value.getTitle()) + ".json");
        gen.writeStringField("lastUpdate", simpleDateFormat.format(value.getLastUpdate()));
        gen.writeEndObject();
    }
    
}