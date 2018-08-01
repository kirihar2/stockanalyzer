package com.juran.kirihara.stockanalyzer.models;

import java.util.Date;

public class MaxDailyProfitFromQuandlTable {
    private Date date;
    private double amountProfit;

    public MaxDailyProfitFromQuandlTable() {
        super();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmountProfit() {
        return amountProfit;
    }

    public void setAmountProfit(double amountProfit) {
        this.amountProfit = amountProfit;
    }
}
