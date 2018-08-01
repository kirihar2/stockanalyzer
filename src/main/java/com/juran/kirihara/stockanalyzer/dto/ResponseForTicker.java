package com.juran.kirihara.stockanalyzer.dto;

public abstract class ResponseForTicker {
    private String ticker;
    private String error;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
