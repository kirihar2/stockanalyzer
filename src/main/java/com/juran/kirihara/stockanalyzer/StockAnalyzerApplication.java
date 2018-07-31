package com.juran.kirihara.stockanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class StockAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalyzerApplication.class, args);
    }
}
