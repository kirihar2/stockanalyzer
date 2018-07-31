package com.juran.kirihara.stockanalyzer.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class QuandleTableModelDeserializer extends StdDeserializer<QuandleTableModel> {
    private static Logger logger = LoggerFactory.getLogger(QuandleTableModelDeserializer.class);

    public QuandleTableModelDeserializer() {
        this(null);
    }

    public QuandleTableModelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public QuandleTableModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<QuandleTableEntry> quandleTableEntries = new ArrayList<>();
        if (node.get("datatable") != null && node.get("datatable").get("data") != null) {
            for (JsonNode element : node.get("datatable").get("data")) {
                QuandleTableEntry entry = new QuandleTableEntry();
                entry.setTicker(element.get(0).asText());
                try {
                    entry.setDate(element.get(1).asText());
                } catch (ParseException e) {
                    logger.warn(e.getMessage());
                }
                entry.setOpen(element.get(2).asDouble());
                entry.setHigh(element.get(3).asDouble());
                entry.setLow(element.get(4).asDouble());
                entry.setClose(element.get(5).asDouble());
                entry.setVolume(element.get(6).asDouble());
                quandleTableEntries.add(entry);
            }
        }
        return new QuandleTableModel(quandleTableEntries);
    }
}