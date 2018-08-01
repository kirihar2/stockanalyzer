package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.QuandlTableModel;

public class WikiTableResponse {
    private QuandlTableModel quandlTableModel;
    private String error;

    public WikiTableResponse() {
        super();
    }

    public QuandlTableModel getQuandlTableModel() {
        return quandlTableModel;
    }

    public void setQuandlTableModel(QuandlTableModel quandlTableModel) {
        this.quandlTableModel = new QuandlTableModel(quandlTableModel);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
