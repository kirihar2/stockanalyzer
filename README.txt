This spring boot webservice calls the Quandl API to get daily stock data for a given list of stock names and a time frame.
All data retrieved from Quandl is from the WIKI/PRICES table, however with minor configuration/code changes one can easily extend
the same functionality across different tables.

Most of the request and response bodies are explained in swagger-ui.
If you run the 'StockAnalyzerApplication.class' and open a browser and go to the following link, all controllers and their documentations
are there. The stock-analyzer-controller contains the main functionlity.

http://localhost:8080/api/v1/swagger-ui.html

Below highlights the main functionality of this service:

    - Retrieve the all data for a given stocks and the respective time frame.
    - Calculate the average monthly open and close price for the given stocks within the time frame.
    - Calculate the daily maximum profit for the given stocks within the time frame. For the calculations the following cases
    are considered.

         Since data does not show the time series data for the stock, it is not possible to say that the
         low happens before stock high price. The best (possible trade) is to compare the buy at open and sell at high
         and buy at low and sell at close assuming we could do multiple trades per stock/day.
         Diagram to explain possibilities:

             case 1: low before high :

                       In this case the true maximum would be buy at open and at low then sell both at high.
                        open      low       high        close
               <--------------------------------->

             case 2: high before low :

                           In this case the true maximum would be to buy at open and sell at high, then buy at low
                           and sell at close.
                        open      high       low        close
               <--------------------------------->

                        With the above, we can give the best estimate by using buying at open then selling it at high,
               then buying at low and sell in at close. The if the open is the same value as high and close is the same
               as low then the maximum profit would be 0 because the trend would be decreasing in price.

             case 3:
                  high                            low
                 open                           close
               <--------------------------------->

             case 4:
                 this could also impact the calculation, because it would double the profit that is possible.
                 condition would be if high-open = low-close then maximum would be high-open.
                  low                            high
                 open                           close
               <--------------------------------->


    - Calculate the busy trading days for each stock within the time frame. A day of trading is considered a busy if
    the trade volume for that day is greater than 10% of the average trade volume for that time frame.

