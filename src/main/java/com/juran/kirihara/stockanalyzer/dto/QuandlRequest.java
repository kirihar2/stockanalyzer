package com.juran.kirihara.stockanalyzer.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonDeserialize(using = QuandlRequestDeserializer.class)
public class QuandlRequest {
    @ApiModelProperty(example = "GOOG,COF")
    private List<String> tickers;
    @ApiModelProperty(example = "2017-01-01")
    private Date startDate;
    @ApiModelProperty(example = "2017-06-01")
    private Date endDate;



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

    public List<String> getTickers() {
        return tickers;
    }

    public void setTickers(List<String> tickers) {
        this.tickers = new ArrayList<>(tickers);
    }
}
