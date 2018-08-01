package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.AverageMonthPriceFromQuandlTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;

public class AverageMonthlyPriceResponse extends ResponseForTicker {
    private List<AverageMonthPriceFromQuandlTable> averageMonthPriceFromQuandlTables;

    public AverageMonthlyPriceResponse() {
        super();
    }

    public AverageMonthlyPriceResponse(String ticker, Map<String, Map<String, Double>> bucketForMonths) {
        setTicker(ticker);
        List<AverageMonthPriceFromQuandlTable> averageMonthlyPrices = new ArrayList<>();
        List<String> orderedMonthKeys = new ArrayList<>(bucketForMonths.keySet());
        sort(orderedMonthKeys);
        for (String monthKey : orderedMonthKeys) {
            AverageMonthPriceFromQuandlTable monthlyPrice = new AverageMonthPriceFromQuandlTable(monthKey, bucketForMonths.get(monthKey));
            averageMonthlyPrices.add(monthlyPrice);
        }
        setAverageMonthPriceFromQuandlTables(averageMonthlyPrices);
    }

    public List<AverageMonthPriceFromQuandlTable> getAverageMonthPriceFromQuandlTables() {
        return averageMonthPriceFromQuandlTables;
    }

    public void setAverageMonthPriceFromQuandlTables(List<AverageMonthPriceFromQuandlTable> averageMonthPriceFromQuandlTables) {
        this.averageMonthPriceFromQuandlTables = new ArrayList<>(averageMonthPriceFromQuandlTables);
    }
}
