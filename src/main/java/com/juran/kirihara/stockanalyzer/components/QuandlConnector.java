package com.juran.kirihara.stockanalyzer.components;


import com.juran.kirihara.stockanalyzer.Constants;
import com.juran.kirihara.stockanalyzer.dto.QuandlRequest;
import com.juran.kirihara.stockanalyzer.models.QuandleTableModel;
import com.juran.kirihara.stockanalyzer.models.QuandleTableModelDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class QuandlConnector {
    private static Logger logger = LoggerFactory.getLogger(QuandleTableModelDeserializer.class);
    private String apiToken;
    private String wikiApiUrl;
    private RestTemplate quandlRestRequestBuilder;

    public QuandlConnector(String url, String apiToken) {
        quandlRestRequestBuilder = new RestTemplate();
        this.apiToken = apiToken;
        this.wikiApiUrl = url;
    }

    public ResponseEntity<QuandleTableModel> getWikiTableResponse(QuandlRequest request) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(this.wikiApiUrl);
            String tickers = String.join(",", request.getTickers());
            builder.queryParam("ticker", tickers);
            if (request.getStartDate() != null) {
                builder.queryParam("date.gte", Constants.formatWithDate.format(request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                builder.queryParam("date.lt", Constants.formatWithDate.format(request.getEndDate()));
            }
            builder.queryParam("api_key", this.apiToken);

            return this.quandlRestRequestBuilder.getForEntity(builder.toUriString(), QuandleTableModel.class);

        } catch (Exception e) {
            QuandleTableModel error = new QuandleTableModel();
            error.setError(e.getMessage());
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}
