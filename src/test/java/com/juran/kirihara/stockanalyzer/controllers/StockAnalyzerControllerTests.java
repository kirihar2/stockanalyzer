package com.juran.kirihara.stockanalyzer.controllers;

import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
import com.juran.kirihara.stockanalyzer.dto.MaxDailyProfitResponse;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.dto.WikiTableResponse;
import com.juran.kirihara.stockanalyzer.services.StockAnalyzerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class StockAnalyzerControllerTests {
    @Mock
    private StockAnalyzerService service;

    @InjectMocks
    private StockAnalyzerController controller = new StockAnalyzerController();


    @Test
    public void testGetStockPriceControllerPass() {
        QuandlRequest mockRequest = new QuandlRequest();
        WikiTableResponse expectedResponse = new WikiTableResponse();
        when(service.getPrice(mockRequest)).thenReturn(expectedResponse);
        ResponseEntity<WikiTableResponse> response = controller.getPrice(mockRequest);
        Assert.assertSame(expectedResponse, response.getBody());
    }

    @Test
    public void testGetStockPriceControllerWhenServiceThrowsError() {
        QuandlRequest mockRequest = new QuandlRequest();
        WikiTableResponse expectedResponse = new WikiTableResponse();
        expectedResponse.setError("something went wrong in service");
        when(service.getPrice(any())).thenThrow(new RuntimeException("something went wrong in service"));
        ResponseEntity<WikiTableResponse> response = controller.getPrice(mockRequest);
        Assert.assertEquals(expectedResponse.getError(), response.getBody().getError());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetAverageMonthlyStockPriceControllerPass() throws Exception {
        QuandlRequest mockRequest = new QuandlRequest();
        List<AverageMonthlyPriceResponse> expectedResponseList = new ArrayList<>();
        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        expectedResponseList.add(expectedResponse);
        when(service.getAverageMonthlyPrice(mockRequest)).thenReturn(expectedResponseList);
        ResponseEntity<List<AverageMonthlyPriceResponse>> response = controller.getAverageMonthlyPrice(mockRequest);
        Assert.assertSame(expectedResponse, response.getBody().get(0));
    }

    @Test
    public void testGetAverageMonthlyStockPriceControllerWhenServiceThrowsError() throws Exception {
        QuandlRequest mockRequest = new QuandlRequest();
        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        expectedResponse.setError("something went wrong in service");
        when(service.getAverageMonthlyPrice(any())).thenThrow(new RuntimeException("something went wrong in service"));
        ResponseEntity<List<AverageMonthlyPriceResponse>> response = controller.getAverageMonthlyPrice(mockRequest);
        Assert.assertEquals(expectedResponse.getError(), response.getBody().get(0).getError());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetMaximumProfitControllerPass() throws Exception {
        QuandlRequest mockRequest = new QuandlRequest();
        List<MaxDailyProfitResponse> expectedResponseList = new ArrayList<>();
        MaxDailyProfitResponse expectedResponse = new MaxDailyProfitResponse();
        expectedResponseList.add(expectedResponse);
        expectedResponse.setError("something went wrong in service");
        when(service.getMaxDailyProfit(mockRequest)).thenReturn(expectedResponseList);
        ResponseEntity<List<MaxDailyProfitResponse>> response = controller.getMaxDailyProfit(mockRequest);
        Assert.assertSame(expectedResponse, response.getBody().get(0));
    }

    @Test
    public void testGetMaximumProfitControllerWhenServiceThrowsError() throws Exception {
        QuandlRequest mockRequest = new QuandlRequest();
        MaxDailyProfitResponse expectedResponse = new MaxDailyProfitResponse();
        expectedResponse.setError("something went wrong in service");
        when(service.getMaxDailyProfit(any())).thenThrow(new RuntimeException("something went wrong in service"));
        ResponseEntity<List<MaxDailyProfitResponse>> response = controller.getMaxDailyProfit(mockRequest);
        Assert.assertEquals(expectedResponse.getError(), response.getBody().get(0).getError());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
