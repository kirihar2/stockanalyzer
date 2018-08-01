package com.juran.kirihara.stockanalyzer.services;

import com.juran.kirihara.stockanalyzer.Constants;
import com.juran.kirihara.stockanalyzer.components.QuandlConnector;
import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
import com.juran.kirihara.stockanalyzer.dto.MaxDailyProfitResponse;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.dto.WikiTableResponse;
import com.juran.kirihara.stockanalyzer.models.MaxDailyProfitFromQuandlTable;
import com.juran.kirihara.stockanalyzer.models.QuandleTableEntry;
import com.juran.kirihara.stockanalyzer.models.QuandleTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@ConfigurationProperties(prefix = "service")
public class StockAnalyzerService {
    private static Logger logger = LoggerFactory.getLogger(StockAnalyzerService.class);
    private static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
    @Autowired
    private QuandlConnector quandlConnector;

    public WikiTableResponse getPrice(QuandlRequest request) {

        WikiTableResponse response = new WikiTableResponse();
        ResponseEntity<QuandleTableModel> responseFromQuandl = quandlConnector.getWikiTableResponse(request);
        if (responseFromQuandl.getBody().getError() != null &&
                !responseFromQuandl.getBody().getError().isEmpty()) {
            String errorMessage = "Error in the api request call to quandl, check inner error";
            logger.error(errorMessage);
            response.setError(errorMessage);
        } else if (responseFromQuandl.getBody().getEntries() == null || responseFromQuandl.getBody().getEntries().isEmpty()) {
            String errorMessage = "Error in the api request call to quandl, could not find entries with the given parameters. " +
                    "Please check dates are valid dates and ticker is a valid stock name";
            logger.error(errorMessage);
            response.setError(errorMessage);
        }
        response.setQuandleTableModel(responseFromQuandl.getBody());
        return response;
    }

    public List<AverageMonthlyPriceResponse> getAverageMonthlyPrice(QuandlRequest request) throws Exception {
        WikiTableResponse wikiTableResponse =
                getPrice(request);
        rethrowErrorThrownByQuandlApiIfExists(wikiTableResponse);
        QuandleTableModel quandleTableModel = wikiTableResponse.getQuandleTableModel();

        /*
            Outer map is for the ticker symbol.
            Looks at all dates and places the values for open and close prices into the same bucket
            as yyyy-MM as key for the second map.
            For maintenance the inside pair is {"open": double, "close": double, "count": double}
            count is the number of entries that fall into the same month
            So the Map structure is as follows:
            {
            ticker1: {
                        Month1: {"open": double, "close": double, "count": double},
                        Month2: {"open": double, "close": double, "count": double}...
                        },
            ticker2: {...}

            }
        */
        Map<String, Map<String, Map<String, Double>>> bucketForMonthsPerTicker = new HashMap<>();

        if (quandleTableModel != null) {
            for (QuandleTableEntry entry : quandleTableModel.getEntries()) {
                String currentTicker = entry.getTicker();
                if (!bucketForMonthsPerTicker.containsKey(currentTicker)) {
                    bucketForMonthsPerTicker.put(currentTicker, new HashMap<>());
                }
                Map<String, Map<String, Double>> bucketForMonths = bucketForMonthsPerTicker.get(currentTicker);
                String monthKey = fmt.format(entry.getDate());
                if (!bucketForMonths.containsKey(monthKey)) {
                    Map<String, Double> bucketForPrices = new HashMap<>();
                    bucketForPrices.put(Constants.OPEN_PRICE_KEY, entry.getOpen());
                    bucketForPrices.put(Constants.CLOSE_PRICE_KEY, entry.getClose());
                    bucketForPrices.put(Constants.COUNT_KEY, (double) 1);
                    bucketForMonths.put(monthKey, bucketForPrices);
                } else {
                    Map<String, Double> updatedPrices = updatePrices(bucketForMonths.get(monthKey), entry.getOpen(), entry.getClose());
                    bucketForMonths.put(monthKey, updatedPrices);
                }
                bucketForMonthsPerTicker.put(currentTicker, bucketForMonths);
            }
        }
        List<AverageMonthlyPriceResponse> response = new ArrayList<>();
        for (String ticker : bucketForMonthsPerTicker.keySet()) {
            response.add(new AverageMonthlyPriceResponse(ticker, calculateAverages(bucketForMonthsPerTicker.get(ticker))));
        }
        return response;
    }

    private void rethrowErrorThrownByQuandlApiIfExists(WikiTableResponse wikiTableResponse) throws Exception {
        if (wikiTableResponse.getError() != null && !wikiTableResponse.getError().isEmpty()) {
            logger.error(wikiTableResponse.getError());
            throw new Exception(wikiTableResponse.getError());
        }
    }

    //
    /*
     * Since data does not show the time series data for the stock, it is not possible to say that the
     * low happens before stock high price. The best (possible trade) is to compare the buy at open and sell at high
     * and buy at low and sell at close assuming we could do multiple trades per stock/day.
     * Diagram to explain possibilities:
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
    public List<MaxDailyProfitResponse> getMaxDailyProfit(QuandlRequest request) throws Exception {
        List<MaxDailyProfitResponse> response = new ArrayList<>();
        Map<String, Integer> tickersInTheResponse = new HashMap<>();
        WikiTableResponse wikiTableResponse = getPrice(request);
        rethrowErrorThrownByQuandlApiIfExists(wikiTableResponse);
        QuandleTableModel quandleTableModel = wikiTableResponse.getQuandleTableModel();
        if (quandleTableModel != null) {
            for (QuandleTableEntry entry : quandleTableModel.getEntries()) {
                String currentTicker = entry.getTicker();
                MaxDailyProfitResponse tickerMaxDailyProfit;
                MaxDailyProfitFromQuandlTable maxDailyProfitToAdd = new MaxDailyProfitFromQuandlTable();
                List<MaxDailyProfitFromQuandlTable> maxDailyProfitFromQuandlTables;
                if (!tickersInTheResponse.containsKey(currentTicker)) {
                    tickersInTheResponse.put(currentTicker, response.size());
                    tickerMaxDailyProfit = new MaxDailyProfitResponse();
                    tickerMaxDailyProfit.setTicker(currentTicker);
                    maxDailyProfitFromQuandlTables = new ArrayList<>();
                    tickerMaxDailyProfit.setMaxDailyProfitsFromQuandlTable(maxDailyProfitFromQuandlTables);
                    response.add(tickerMaxDailyProfit);
                }
                int ind = tickersInTheResponse.get(currentTicker);
                tickerMaxDailyProfit = response.get(ind);
                maxDailyProfitFromQuandlTables = tickerMaxDailyProfit.getMaxDailyProfitsFromQuandlTable();

                maxDailyProfitToAdd.setDate(entry.getDate());
                double maxProfitPotentialFromOpen = entry.getHigh() - entry.getOpen();
                double maxProfitPotentialToClose = entry.getClose() - entry.getLow();
                double maxProfit = calculateMaximumProfit(maxProfitPotentialFromOpen, maxProfitPotentialToClose);
                maxDailyProfitToAdd.setAmountProfit(maxProfit);
                maxDailyProfitFromQuandlTables.add(maxDailyProfitToAdd);
                tickerMaxDailyProfit.setMaxDailyProfitsFromQuandlTable(maxDailyProfitFromQuandlTables);
                response.set(ind, tickerMaxDailyProfit);
            }
        }
        return response;
    }

    private double calculateMaximumProfit(double maxProfitPotentialFromOpen, double maxProfitPotentialToClose) {
        double ret;
        //To cover case 3 and 4 just select one of the profits
        if (maxProfitPotentialFromOpen == maxProfitPotentialToClose) {
            ret = maxProfitPotentialFromOpen;
        } else {// to cover case 1 and 2 **this is a best estimate max profit, not entirely accurate without timeseries data
            ret = maxProfitPotentialFromOpen + maxProfitPotentialToClose;
        }
        return ret;
    }


    private Map<String, Double> updatePrices(Map<String, Double> originalPrices, Double open, Double close) {
        Map<String, Double> updatedAveragePrices = new HashMap<>(originalPrices);
        double updatedCount = updatedAveragePrices.get(Constants.COUNT_KEY) + 1;
        updatedAveragePrices.put(Constants.COUNT_KEY, updatedCount);
        updatedAveragePrices.put(Constants.OPEN_PRICE_KEY, (updatedAveragePrices.get(Constants.OPEN_PRICE_KEY) + open));
        updatedAveragePrices.put(Constants.CLOSE_PRICE_KEY, (updatedAveragePrices.get(Constants.CLOSE_PRICE_KEY) + close));
        return updatedAveragePrices;
    }

    private Map<String, Map<String, Double>> calculateAverages(Map<String, Map<String, Double>> totalCount) {
        Map<String, Map<String, Double>> averagePrices = new HashMap<>(totalCount);
        for (String month : averagePrices.keySet()) {
            Map<String, Double> monthPrices = averagePrices.get(month);
            Double totalPricesCounted = monthPrices.get(Constants.COUNT_KEY);
            monthPrices.put(Constants.OPEN_PRICE_KEY, monthPrices.get(Constants.OPEN_PRICE_KEY) / totalPricesCounted);
            monthPrices.put(Constants.CLOSE_PRICE_KEY, monthPrices.get(Constants.CLOSE_PRICE_KEY) / totalPricesCounted);
            averagePrices.put(month, monthPrices);
        }
        return averagePrices;
    }
}
