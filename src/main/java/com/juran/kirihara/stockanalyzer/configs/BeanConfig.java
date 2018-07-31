package com.juran.kirihara.stockanalyzer.configs;

import com.juran.kirihara.stockanalyzer.components.QuandlConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Value("${quandl.apiurl.wiki.table}")
    private String url;

    @Value("${quandl.apitoken}")
    private String apiToken;

    @Bean
    public QuandlConnector getNewQuandlConnector() {
        return new QuandlConnector(url, apiToken);
    }
}
