package com.juran.kirihara.stockanalyzer.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = QuandleTableModelDeserializer.class)
public class QuandleTableModel {
    private List<QuandleTableEntry> entries;
    private String error;

    public QuandleTableModel() {
        super();
    }

    public QuandleTableModel(QuandleTableModel model) {
        setEntries(model.entries);
        setError(model.error);
    }

    public QuandleTableModel(List<QuandleTableEntry> entries) {
        setEntries(entries);
    }

    public List<QuandleTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<QuandleTableEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
