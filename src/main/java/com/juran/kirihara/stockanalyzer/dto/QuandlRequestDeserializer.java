package com.juran.kirihara.stockanalyzer.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.juran.kirihara.stockanalyzer.Constants;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class QuandlRequestDeserializer extends JsonDeserializer<QuandlRequest> {

    @Override
    public QuandlRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        QuandlRequest request = new QuandlRequest();
        JsonNode node = p.getCodec().readTree(p);
        if (node.get("tickers") == null || node.get("tickers").asText().isEmpty()
                || node.get("startDate") == null || node.get("startDate").asText().isEmpty()
                || node.get("endDate") == null || node.get("endDate").asText().isEmpty())
            throw new IOException("Invalid input, missing one or more of input needed for the request. " +
                    "Please check that startDate, endDate, and tickers is populated");
        String tickersListAsString = node.get("tickers").asText();
        if (!tickersListAsString.isEmpty()) {
            List<String> tickers;
            if (tickersListAsString.contains(",")) {
                tickers = Arrays.asList(tickersListAsString.split(","));
            } else {
                //Tickers inputted as a single value
                tickers = new ArrayList<>();
                tickers.add(tickersListAsString);
            }
            request.setTickers(tickers);

        }
        try {
            request.setStartDate(Constants.formatWithDate.parse(node.get("startDate").asText()));
            request.setEndDate(Constants.formatWithDate.parse(node.get("endDate").asText()));
        } catch (ParseException e) {
            //have to convert to IOException because parseexception clashes with the one in jackson
            throw new IOException(e.getMessage());
        }
        return request;
    }
}
