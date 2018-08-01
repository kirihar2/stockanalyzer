package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.MaxDailyProfitFromQuandlTable;

import java.util.ArrayList;
import java.util.List;

public class MaxDailyProfitResponse {
    private String ticker;
    private List<MaxDailyProfitFromQuandlTable> maxDailyProfitsFromQuandlTable;
    private String error;

    public List<MaxDailyProfitFromQuandlTable> getMaxDailyProfitsFromQuandlTable() {
        return maxDailyProfitsFromQuandlTable;
    }

    public void setMaxDailyProfitsFromQuandlTable(List<MaxDailyProfitFromQuandlTable> maxDailyProfitsFromQuandlTable) {
        this.maxDailyProfitsFromQuandlTable = new ArrayList<>(maxDailyProfitsFromQuandlTable);
    }

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
