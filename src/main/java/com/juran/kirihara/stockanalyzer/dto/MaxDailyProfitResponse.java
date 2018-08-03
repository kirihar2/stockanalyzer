package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.MaxDailyProfitFromQuandlTable;

public class MaxDailyProfitResponse extends ResponseForTicker {
    private MaxDailyProfitFromQuandlTable maxDailyProfitFromQuandlTable;

    public MaxDailyProfitResponse() {
        super();
    }

    public MaxDailyProfitFromQuandlTable getMaxDailyProfitFromQuandlTable() {
        return maxDailyProfitFromQuandlTable;
    }

    public void setMaxDailyProfitFromQuandlTable(MaxDailyProfitFromQuandlTable maxDailyProfitFromQuandlTable) {
        this.maxDailyProfitFromQuandlTable = maxDailyProfitFromQuandlTable;
    }

}
