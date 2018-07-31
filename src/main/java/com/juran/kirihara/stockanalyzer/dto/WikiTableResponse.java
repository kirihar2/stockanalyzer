package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.QuandleTableModel;

public class WikiTableResponse {
    private QuandleTableModel quandleTableModel;
    private String error;

    public WikiTableResponse() {
        super();
    }

    public QuandleTableModel getQuandleTableModel() {
        return quandleTableModel;
    }

    public void setQuandleTableModel(QuandleTableModel quandleTableModel) {
        this.quandleTableModel = new QuandleTableModel(quandleTableModel);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
