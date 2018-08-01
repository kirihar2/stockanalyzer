package com.juran.kirihara.stockanalyzer.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = QuandlTableModelDeserializer.class)
public class QuandlTableModel {
    private List<QuandlTableEntry> entries;
    private String error;

    public QuandlTableModel() {
        super();
    }

    public QuandlTableModel(QuandlTableModel model) {
        setEntries(model.entries);
        setError(model.error);
    }

    public QuandlTableModel(List<QuandlTableEntry> entries) {
        setEntries(entries);
    }

    public List<QuandlTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<QuandlTableEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
