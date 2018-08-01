package com.juran.kirihara.stockanalyzer.controllers;

import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
import com.juran.kirihara.stockanalyzer.dto.MaxDailyProfitResponse;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.dto.WikiTableResponse;
import com.juran.kirihara.stockanalyzer.services.StockAnalyzerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("stock")
public class StockAnalyzerController {
    private static Logger logger = LoggerFactory.getLogger(StockAnalyzerController.class);

    @Autowired
    private StockAnalyzerService service;

    @PostMapping(value = "getPrice", consumes = "application/json", produces = "application/json")
    @ApiOperation("Request to get information for stock data")
    public ResponseEntity<WikiTableResponse> getPrice(
            @ApiParam("Information to get from Database")
            @RequestBody QuandlRequest request
    ) {
        try {
            WikiTableResponse response = service.getPrice(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            WikiTableResponse error = new WikiTableResponse();
            logger.error(e.getMessage());
            error.setError(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }

    @PostMapping(value = "getAverageMonthlyPrice", consumes = "application/json", produces = "application/json")
    @ApiOperation("Request to get monthly open and close date for a stock")
    public ResponseEntity<List<AverageMonthlyPriceResponse>> getAverageMonthlyPrice(
            @ApiParam("Information to get from Database")
            @RequestBody QuandlRequest request
    ) {
        try {
            return ResponseEntity.ok().body(service.getAverageMonthlyPrice(request));
        } catch (Exception e) {
            List<AverageMonthlyPriceResponse> errorResponse = new ArrayList<>();
            AverageMonthlyPriceResponse error = new AverageMonthlyPriceResponse();
            logger.error(e.getMessage());
            error.setError(e.getMessage());
            errorResponse.add(error);
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @PostMapping(value = "getMaxDailyProfit", consumes = "application/json", produces = "application/json")
    @ApiOperation("Request to get daily maximum profit for each ticker")
    public ResponseEntity<List<MaxDailyProfitResponse>> getMaxDailyProfit(
            @ApiParam("Information to get from Database")
            @RequestBody QuandlRequest request
    ) {
        try {
            return ResponseEntity.ok().body(service.getMaxDailyProfit(request));
        } catch (Exception e) {
            List<MaxDailyProfitResponse> errorResponse = new ArrayList<>();
            MaxDailyProfitResponse error = new MaxDailyProfitResponse();
            logger.error(e.getMessage());
            error.setError(e.getMessage());
            errorResponse.add(error);
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }
}
