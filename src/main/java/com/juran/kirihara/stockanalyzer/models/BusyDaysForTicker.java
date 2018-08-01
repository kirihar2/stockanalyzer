package com.juran.kirihara.stockanalyzer.models;

import java.util.Date;

public class BusyDaysForTicker {
    private Date date;
    private double volume;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
