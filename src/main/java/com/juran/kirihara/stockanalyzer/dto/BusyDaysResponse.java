package com.juran.kirihara.stockanalyzer.dto;

import com.juran.kirihara.stockanalyzer.models.BusyDaysForTicker;

import java.util.ArrayList;
import java.util.List;

public class BusyDaysResponse extends ResponseForTicker {
    private List<BusyDaysForTicker> busyDaysForTickerList;

    public BusyDaysResponse() {
        super();
    }

    public List<BusyDaysForTicker> getBusyDaysForTickerList() {
        return busyDaysForTickerList;
    }

    public void setBusyDaysForTickerList(List<BusyDaysForTicker> busyDaysForTickerList) {
        this.busyDaysForTickerList = new ArrayList<>(busyDaysForTickerList);
    }
}
