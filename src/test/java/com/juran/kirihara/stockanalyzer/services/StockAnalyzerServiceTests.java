package com.juran.kirihara.stockanalyzer.services;

import com.juran.kirihara.stockanalyzer.Constants;
import com.juran.kirihara.stockanalyzer.components.QuandlConnector;
import com.juran.kirihara.stockanalyzer.dto.*;
import com.juran.kirihara.stockanalyzer.models.AverageMonthPriceFromQuandlTable;
import com.juran.kirihara.stockanalyzer.models.BusyDaysForTicker;
import com.juran.kirihara.stockanalyzer.models.QuandlTableEntry;
import com.juran.kirihara.stockanalyzer.models.QuandlTableModel;
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
import java.util.Date;
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
    private QuandlTableModel mockQuandlTableModel;
    private QuandlTableEntry mockQuandlTableEntry;

    @Before
    public void setUp() throws ParseException {
        List<String> tickers = new ArrayList<>();
        tickers.add(TICKER);
        request = new QuandlRequest();
        request.setTickers(tickers);
        request.setStartDate(Constants.formatWithDate.parse("2018-01-01"));
        request.setEndDate(Constants.formatWithDate.parse("2018-02-01"));
        mockQuandlTableModel = new QuandlTableModel();
        mockQuandlTableEntry = new QuandlTableEntry();
        mockQuandlTableEntry.setDate(Constants.formatWithDate.parse("2018-01-01"));
        mockQuandlTableEntry.setOpen(4);
        mockQuandlTableEntry.setClose(10);
        mockQuandlTableEntry.setLow(1);
        mockQuandlTableEntry.setHigh(14);
        mockQuandlTableEntry.setTicker(TICKER);
        mockQuandlTableEntry.setVolume(10);
    }

    /* Test that models related to getting the stock prices from API works properly.
     *  A general happy path with both Quandl API is working and service is working properly
     * */
    @Test
    public void testGetStockPricesPass() {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);
        this.mockQuandlTableModel.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(mockQuandlTableModel));
        WikiTableResponse response = this.service.getPrice(request);
        Assert.assertTrue(response.getQuandlTableModel() != null && response.getQuandlTableModel().getEntries() != null);
        Assert.assertEquals(mockQuandleTableEntries, response.getQuandlTableModel().getEntries());
    }

    /* Test that response will correctly contain the correct error message if the Quandl Api threw an error
     */
    @Test
    public void testGetStockPricesReceivedErrorOnResponseFromQuandlApi() {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        model.setError("Something went wrong with Quandl");
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        WikiTableResponse response = this.service.getPrice(request);
        Assert.assertEquals("Something went wrong with Quandl", response.getQuandlTableModel().getError());
        Assert.assertEquals("Error in the api request call to quandl, check inner error", response.getError());
    }

    /* Test that response will correctly contain the correct error message if the Quandl Api threw an error
     */
    @Test
    public void testGetStockPricesReceivedNoEntriesFromApi() {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        WikiTableResponse response = this.service.getPrice(request);
        Assert.assertEquals("Error in the api request call to quandl, could not find entries with the given parameters. " +
                "Please check dates are valid dates and ticker is a valid stock name", response.getError());
    }

    /* Test to validate the average is correctly calculated for the open and close price
     */
    @Test
    public void testGetAveragePricesForMonth() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableEntry quandlTableEntry = new QuandlTableEntry();
        quandlTableEntry.setTicker(TICKER);
        quandlTableEntry.setDate(Constants.formatWithDate.parse("2018-01-02"));
        quandlTableEntry.setOpen(3);
        quandlTableEntry.setClose(5);
        mockQuandleTableEntries.add(quandlTableEntry);
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);

        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(model));
        List<AverageMonthPriceFromQuandlTable> expectedMonthlyPricesForResponse = new ArrayList<>();
        AverageMonthPriceFromQuandlTable expectedMonthlyPrice = new AverageMonthPriceFromQuandlTable();
        expectedMonthlyPrice.setMonth("2018-01");
        expectedMonthlyPrice.setAverage_open(3.5);
        expectedMonthlyPrice.setAverage_close(7.5);
        expectedMonthlyPricesForResponse.add(expectedMonthlyPrice);
        AverageMonthlyPriceResponse expectedResponse = new AverageMonthlyPriceResponse();
        expectedResponse.setAverageMonthPriceFromQuandlTables(expectedMonthlyPricesForResponse);
        expectedResponse.setTicker(TICKER);
        List<AverageMonthlyPriceResponse> actualResponse = this.service.getAverageMonthlyPrice(request);
        Assert.assertEquals(expectedResponse.getTicker(), actualResponse.get(0).getTicker());
        Assert.assertNull(actualResponse.get(0).getError());
        Assert.assertEquals(expectedResponse.getAverageMonthPriceFromQuandlTables().get(0).getMonth(), actualResponse.get(0).getAverageMonthPriceFromQuandlTables().get(0).getMonth());
        Assert.assertTrue(expectedResponse.getAverageMonthPriceFromQuandlTables().get(0).getAverage_open() == actualResponse.get(0).getAverageMonthPriceFromQuandlTables().get(0).getAverage_open());
        Assert.assertTrue(expectedResponse.getAverageMonthPriceFromQuandlTables().get(0).getAverage_close() == actualResponse.get(0).getAverageMonthPriceFromQuandlTables().get(0).getAverage_close());

    }


    //
    /*  Tests for maximum profit covers the following cases
     *  case 1: low before high :
     *               In this case the true maximum would be buy at open and at low then sell both at high.
     *
     *       open      low       high        close
     *       <--------------------------------->
     *
     *  case 2: high before low :
     *                   In this case the true maximum would be to buy at open and sell at high, then buy at low
     *                   and sell at close.
     *
     *       open      high       low        close
     *       <--------------------------------->
     *
     *       With the above, we can give the best estimate by using buying at open then selling it at high,
     *       then buying at low and sell in at close. The if the open is the same value as high and close is the same
     *       as low then the maximum profit would be 0 because the trend would be decreasing in price.
     *
     *   case 3:
     *          high                            low
     *         open                           close
     *       <--------------------------------->
     *
     * case 4:
     *         this could also impact the calculation, because it would double the profit that is possible.
     *         condition would be if high-open = low-close then maximum would be high-open.
     *          low                            high
     *         open                           close
     *       <--------------------------------->
     */
    @Test
    public void testCase1and2ForMaximumProfitToReturnEstimatedValues() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableEntry quandlTableEntry = new QuandlTableEntry();
        quandlTableEntry.setTicker(TICKER);
        quandlTableEntry.setDate(Constants.formatWithDate.parse("2018-01-01"));
        quandlTableEntry.setOpen(2);
        quandlTableEntry.setClose(5);
        quandlTableEntry.setLow(1);
        quandlTableEntry.setHigh(7);
        mockQuandleTableEntries.add(quandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(model));
        Date expectedDate = Constants.formatWithDate.parse("2018-01-01");
        //Construct expected response
//        List<MaxDailyProfitResponse> expectedResponse = new ArrayList<>();
//        MaxDailyProfitResponse expectedResponseForTicker = new MaxDailyProfitResponse();
//        List<MaxDailyProfitFromQuandlTable> expectedResponseConstructedFromQuandl = new ArrayList<>();
//        MaxDailyProfitFromQuandlTable expectedMaxDailyProfitForTicker = new MaxDailyProfitFromQuandlTable();
        double expectedMaximumProfit = 9;//7-2+5-1
//        expectedMaxDailyProfitForTicker.setAmountProfit(expectedMaximumProfit);
//        expectedMaxDailyProfitForTicker.setDate(Constants.formatWithDate.parse("2018-01-01"));
//        expectedResponseConstructedFromQuandl.add(expectedMaxDailyProfitForTicker);
//        expectedResponseForTicker.setTicker(TICKER);
//        expectedResponseForTicker.setMaxDailyProfitFromQuandlTable(expectedResponseConstructedFromQuandl);
//        expectedResponse.add(expectedResponseForTicker);

        List<MaxDailyProfitResponse> actualResponse = this.service.getMaxDailyProfitNew(request);
        Assert.assertNotNull(actualResponse);
        Assert.assertNotNull(actualResponse.get(0));
        Assert.assertNotNull(actualResponse.get(0).getMaxDailyProfitFromQuandlTable());
        Assert.assertEquals(expectedDate, actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getDate());
        Assert.assertTrue(expectedMaximumProfit == actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getAmountProfit());
    }

    @Test
    public void testCase3ForMaximumProfit() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableEntry quandlTableEntry = new QuandlTableEntry();
        quandlTableEntry.setTicker(TICKER);
        quandlTableEntry.setDate(Constants.formatWithDate.parse("2018-01-01"));
        quandlTableEntry.setOpen(5);
        quandlTableEntry.setClose(1);
        quandlTableEntry.setLow(1);
        quandlTableEntry.setHigh(5);
        mockQuandleTableEntries.add(quandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(model));
        Date expectedDate = Constants.formatWithDate.parse("2018-01-01");
        double expectedMaximumProfit = 0;//decreasing trend so best profit is not to buy or sell
        List<MaxDailyProfitResponse> actualResponse = this.service.getMaxDailyProfitNew(request);
        Assert.assertNotNull(actualResponse);
        Assert.assertNotNull(actualResponse.get(0));
        Assert.assertNotNull(actualResponse.get(0).getMaxDailyProfitFromQuandlTable());
        Assert.assertNotNull(actualResponse.get(0).getMaxDailyProfitFromQuandlTable());
        Assert.assertEquals(expectedDate, actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getDate());
        Assert.assertTrue(expectedMaximumProfit == actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getAmountProfit());
    }

    @Test
    public void testCase4ForMaximumProfit() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableEntry quandlTableEntry = new QuandlTableEntry();
        quandlTableEntry.setTicker(TICKER);
        quandlTableEntry.setDate(Constants.formatWithDate.parse("2018-01-01"));
        quandlTableEntry.setOpen(1);
        quandlTableEntry.setClose(5);
        quandlTableEntry.setLow(1);
        quandlTableEntry.setHigh(5);
        mockQuandleTableEntries.add(quandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(model));
        Date expectedDate = Constants.formatWithDate.parse("2018-01-01");
        double expectedMaximumProfit = 4;//increasing trend so high-low 5-1
        List<MaxDailyProfitResponse> actualResponse = this.service.getMaxDailyProfitNew(request);
        Assert.assertNotNull(actualResponse);
        Assert.assertNotNull(actualResponse.get(0));
        Assert.assertNotNull(actualResponse.get(0).getMaxDailyProfitFromQuandlTable());
        Assert.assertNotNull(actualResponse.get(0).getMaxDailyProfitFromQuandlTable());
        Assert.assertEquals(expectedDate, actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getDate());
        Assert.assertTrue(expectedMaximumProfit == actualResponse.get(0).getMaxDailyProfitFromQuandlTable().getAmountProfit());
    }

    //Test exception handling
    @Test(expected = Exception.class)
    public void getMonthlyAverageHandleQuandlApiReturnedWithError() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        model.setError("Something went wrong with Quandl");
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        List<AverageMonthlyPriceResponse> response = this.service.getAverageMonthlyPrice(request);
        Assert.assertTrue(response != null);//should never hit here
    }

    //Test exception handling
    @Test(expected = Exception.class)
    public void getMaxDailyProfitHandleQuandlApiReturnedWithError() throws Exception {
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);
        QuandlTableModel model = new QuandlTableModel();
        model.setEntries(mockQuandleTableEntries);
        model.setError("Something went wrong with Quandl");
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.badRequest().body(model));
        List<MaxDailyProfitResponse> response = this.service.getMaxDailyProfitNew(request);
        Assert.assertTrue(response != null);//should never hit here
    }

    /* Test to validate the busy days by trade volume is correctly calculated
     */
    @Test
    public void testGetBusyDays() throws Exception {
        QuandlTableModel model = new QuandlTableModel();
        List<QuandlTableEntry> mockQuandleTableEntries = new ArrayList<>();
        QuandlTableEntry expectedEntry = new QuandlTableEntry();
        expectedEntry.setTicker(TICKER);
        expectedEntry.setDate(Constants.formatWithDate.parse("2018-01-02"));
        expectedEntry.setVolume(20);
        mockQuandleTableEntries.add(expectedEntry);
        mockQuandleTableEntries.add(this.mockQuandlTableEntry);
        QuandlTableEntry quandlTableEntry1 = new QuandlTableEntry();
        quandlTableEntry1.setTicker(TICKER);
        quandlTableEntry1.setDate(Constants.formatWithDate.parse("2018-01-03"));
        quandlTableEntry1.setVolume(15);
        mockQuandleTableEntries.add(quandlTableEntry1);
        model.setEntries(mockQuandleTableEntries);
        when(quandlConnector.getWikiTableResponse(request)).thenReturn(ResponseEntity.ok().body(model));
        // expectedAverageVolume = 15 10+20+15 = 45/3 = 15
        // threshold 15*1.1 = 16.5

        List<BusyDaysResponse> actualResponse = this.service.getBusyDays(request);
        Assert.assertNull(actualResponse.get(0).getError());
        Assert.assertEquals(1, actualResponse.get(0).getBusyDaysForTickerList().size());
        Assert.assertNotNull(actualResponse.get(0).getBusyDaysForTickerList().get(0));
        BusyDaysForTicker actualValue = actualResponse.get(0).getBusyDaysForTickerList().get(0);
        Assert.assertEquals(expectedEntry.getDate(), actualValue.getDate());
        Assert.assertTrue(expectedEntry.getVolume() == actualValue.getVolume());
    }
}
