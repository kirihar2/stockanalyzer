package com.juran.kirihara.stockanalyzer.controllers;

import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
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
    public void testGetAverageMonthlyStockPriceControllerPass() {
        QuandlRequest mockRequest = new QuandlRequest();
        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        when(service.getAverageMonthlyPrice(mockRequest)).thenReturn(expectedResponse);
        ResponseEntity<AverageMonthlyPriceResponse> response = controller.getAverageMonthlyPrice(mockRequest);
        Assert.assertSame(expectedResponse, response.getBody());
    }

    @Test
    public void testGetAverageMonthlyStockPriceControllerWhenServiceThrowsError() {
        QuandlRequest mockRequest = new QuandlRequest();
        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        expectedResponse.setError("something went wrong in service");
        when(service.getAverageMonthlyPrice(any())).thenThrow(new RuntimeException("something went wrong in service"));
        ResponseEntity<AverageMonthlyPriceResponse> response = controller.getAverageMonthlyPrice(mockRequest);
        Assert.assertEquals(expectedResponse.getError(), response.getBody().getError());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
