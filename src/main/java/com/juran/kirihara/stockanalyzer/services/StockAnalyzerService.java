package com.juran.kirihara.stockanalyzer.services;

import com.juran.kirihara.stockanalyzer.Constants;
import com.juran.kirihara.stockanalyzer.components.QuandlConnector;
import com.juran.kirihara.stockanalyzer.dto.AverageMonthlyPriceResponse;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.dto.WikiTableResponse;
import com.juran.kirihara.stockanalyzer.models.QuandleTableEntry;
import com.juran.kirihara.stockanalyzer.models.QuandleTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
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
        response.setQuandleTableModel(responseFromQuandl.getBody());
        if (responseFromQuandl.getBody().getError() != null &&
                !responseFromQuandl.getBody().getError().isEmpty()) {
            String errorMessage = "Error in the api request call to quandl, check inner error";
            logger.error(errorMessage);
            response.setError(errorMessage);
        }
        return response;
    }

    public AverageMonthlyPriceResponse getAverageMonthlyPrice(QuandlRequest request) {
        QuandleTableModel quandleTableModel = getPrice(request).getQuandleTableModel();
        /*looks at all dates and places the values for open and close prices into the same bucket
            as yyyy-MM as key for the outer map.
            For maintenance the inside pair is {"open": double, "close": double, "count": double}
            count is the number of entries that fall into the same month
        */
        Map<String, Map<String, Double>> bucketForMonths = new HashMap<>();

        if (quandleTableModel != null) {
            for (QuandleTableEntry entry : quandleTableModel.getEntries()) {
                String monthKey = fmt.format(entry.getDate());
                if (!bucketForMonths.containsKey(monthKey)) {
                    Map<String, Double> bucketForPrices = new HashMap<>();
                    bucketForPrices.put(Constants.OPEN_PRICE_KEY, entry.getOpen());
                    bucketForPrices.put(Constants.CLOSE_PRICE_KEY, entry.getClose());
                    bucketForPrices.put(Constants.COUNT_KEY, (double) 1);
                    bucketForMonths.put(monthKey, bucketForPrices);
                } else {
                    Map<String, Double> updateAveragePrices = updateAverage(bucketForMonths.get(monthKey), entry.getOpen(), entry.getClose());
                    bucketForMonths.put(monthKey, updateAveragePrices);
                }

            }
        }

        return new AverageMonthlyPriceResponse(request.getTicker(), bucketForMonths);
    }

    private Map<String, Double> updateAverage(Map<String, Double> originalPrices, Double open, Double close) {
        Map<String, Double> updatedAveragePrices = new HashMap<>(originalPrices);
        double updatedCount = updatedAveragePrices.get(Constants.COUNT_KEY) + 1;
        updatedAveragePrices.put(Constants.COUNT_KEY, updatedCount);
        updatedAveragePrices.put(Constants.OPEN_PRICE_KEY, (updatedAveragePrices.get(Constants.OPEN_PRICE_KEY) + open) / updatedCount);
        updatedAveragePrices.put(Constants.CLOSE_PRICE_KEY, (updatedAveragePrices.get(Constants.CLOSE_PRICE_KEY) + close) / updatedCount);
        return updatedAveragePrices;
    }
}
