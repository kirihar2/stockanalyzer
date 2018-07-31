package com.juran.kirihara.stockanalyzer.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class QuandlRequest {
    @ApiModelProperty(example = "GOOG")
    private String ticker;
    @ApiModelProperty(example = "2017-01-01")
    private Date startDate;
    @ApiModelProperty(example = "2017-06-01")
    private Date endDate;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
