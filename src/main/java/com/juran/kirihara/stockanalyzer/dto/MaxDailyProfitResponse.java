package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.MaxDailyProfitFromQuandlTable;

import java.util.ArrayList;
import java.util.List;

public class MaxDailyProfitResponse extends ResponseForTicker {
    private List<MaxDailyProfitFromQuandlTable> maxDailyProfitsFromQuandlTable;

    public MaxDailyProfitResponse() {
        super();
    }
    public List<MaxDailyProfitFromQuandlTable> getMaxDailyProfitsFromQuandlTable() {
        return maxDailyProfitsFromQuandlTable;
    }

    public void setMaxDailyProfitsFromQuandlTable(List<MaxDailyProfitFromQuandlTable> maxDailyProfitsFromQuandlTable) {
        this.maxDailyProfitsFromQuandlTable = new ArrayList<>(maxDailyProfitsFromQuandlTable);
    }

}
