package com.juran.kirihara.stockanalyzer.models;

import com.juran.kirihara.stockanalyzer.Constants;

import java.util.Map;

public class AverageMonthPriceFromQuandlTable {
    private String month;
    private double average_open;
    private double average_close;

    public AverageMonthPriceFromQuandlTable() {
        super();
    }

    public AverageMonthPriceFromQuandlTable(String month, Map<String, Double> averageForMonth) {
        super();
        setMonth(month);
        setAverage_open(averageForMonth.get(Constants.OPEN_PRICE_KEY));
        setAverage_close(averageForMonth.get(Constants.CLOSE_PRICE_KEY));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof AverageMonthPriceFromQuandlTable)) {
            return false;
        }
        AverageMonthPriceFromQuandlTable ave = (AverageMonthPriceFromQuandlTable) o;
        return this.month.equals(ave.getMonth()) &&
                this.average_close == ave.getAverage_close() &&
                this.average_open == ave.getAverage_open();
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAverage_open() {
        return average_open;
    }

    public void setAverage_open(double average_open) {
        this.average_open = average_open;
    }

    public double getAverage_close() {
        return average_close;
    }

    public void setAverage_close(double average_close) {
        this.average_close = average_close;
    }
}
