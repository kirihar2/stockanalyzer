package com.juran.kirihara.stockanalyzer.services;

import com.juran.kirihara.stockanalyzer.Constants;
import com.juran.kirihara.stockanalyzer.components.QuandlConnector;
import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.dto.WikiTableResponse;
import com.juran.kirihara.stockanalyzer.models.AverageMonthPriceFromQuandlTable;
import com.juran.kirihara.stockanalyzer.models.QuandleTableEntry;
import com.juran.kirihara.stockanalyzer.models.QuandleTableModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockAnalyzerServiceTests {

    public static final String TICKER = "TEST";
    @Mock
    private QuandlConnector quandlConnector;

    @InjectMocks
    private StockAnalyzerService service = new StockAnalyzerService();

    private QuandlRequest request;
    private QuandleTableModel mockQuandleTableModel;
    private QuandleTableEntry mockQuandleTableEntry;

    @Before
    public void setUp() throws ParseException {
        request = new QuandlRequest();
        request.setTicker(TICKER);
        request.setStartDate(Constants.formatWithDate.parse("2018-01-01"));
        request.setEndDate(Constants.formatWithDate.parse("2018-02-01"));
        mockQuandleTableModel = new QuandleTableModel();
        mockQuandleTableEntry = new QuandleTableEntry();
        mockQuandleTableEntry.setDate(Constants.formatWithDate.parse("2018-01-01"));
        mockQuandleTableEntry.setOpen(1);
        mockQuandleTableEntry.setClose(10);
        mockQuandleTableEntry.setLow(1);
        mockQuandleTableEntry.setHigh(14);
        mockQuandleTableEntry.setTicker(TICKER);
        mockQuandleTableEntry.setVolume(10);
    }

    /* Test that models related to getting the stock prices from API works properly.
     *  A general happy path with both Quandl API is working and service is working properly
     * */
    @Test
    public void testGetStockPricesPass() {
        List<QuandleTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandleTableEntry);
        this.mockQuandleTableModel.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(mockQuandleTableModel));
        WikiTableResponse response = this.service.getPrice(request);
        Assert.assertTrue(response.getQuandleTableModel() != null && response.getQuandleTableModel().getEntries() != null);
        Assert.assertEquals(mockQuandleTableEntries, response.getQuandleTableModel().getEntries());
    }

    /* Test that response will correctly contain the correct error message if the Quandl Api threw an error
     */
    @Test
    public void testGetStockPricesReceivedErrorOnResponseFromQuandlApi() {
        List<QuandleTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandleTableEntry);
        QuandleTableModel model = new QuandleTableModel();
        model.setEntries(mockQuandleTableEntries);
        model.setError("Something went wrong with Quandl");
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        WikiTableResponse response = this.service.getPrice(request);
        Assert.assertEquals("Something went wrong with Quandl", response.getQuandleTableModel().getError());
        Assert.assertEquals("Error in the api request call to quandl, check inner error", response.getError());
    }


    /* Test to validate the average is correctly calcluated for the open and close price
     */
    @Test
    public void testGetAveragePricesForMonth() throws ParseException {
        List<QuandleTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandleTableEntry quandleTableEntry = new QuandleTableEntry();
        quandleTableEntry.setDate(Constants.formatWithDate.parse("2018-01-02"));
        quandleTableEntry.setOpen(2);
        quandleTableEntry.setClose(5);
        mockQuandleTableEntries.add(quandleTableEntry);
        mockQuandleTableEntries.add(this.mockQuandleTableEntry);

        QuandleTableModel model = new QuandleTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        List<AverageMonthPriceFromQuandlTable> expectedMonthlyPricesForResponse = new ArrayList<>();
        AverageMonthPriceFromQuandlTable expectedMonthlyPrice = new AverageMonthPriceFromQuandlTable();
        expectedMonthlyPrice.setMonth("2018-01");
        expectedMonthlyPrice.setAverage_open(1.5);
        expectedMonthlyPrice.setAverage_close(7.5);
        expectedMonthlyPricesForResponse.add(expectedMonthlyPrice);

        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        expectedResponse.setAverageMonthPriceFromQuandlTables(expectedMonthlyPricesForResponse);
        expectedResponse.setTicker(TICKER);
        AverageMonthlyPriceResponse actualResponse = this.service.getAverageMonthlyPrice(request);
        Assert.assertEquals(expectedResponse.getTicker(), actualResponse.getTicker());
        Assert.assertNull(actualResponse.getError());
        Assert.assertEquals(expectedResponse.getAverageMonthPriceFromQuandlTables().get(0), actualResponse.getAverageMonthPriceFromQuandlTables().get(0));
    }
}
