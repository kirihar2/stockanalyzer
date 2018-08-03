This spring boot webservice calls the Quandl API to get daily stock data for a given list of stock names and a time frame.
All data retrieved from Quandl is from the WIKI/PRICES table, however with minor configuration/code changes one can easily extend
the same functionality across different tables.

Most of the request and response bodies are explained in swagger-ui.
There are a few ways to run the application, however, the preferred way is to run the jar file in the target/stock-analyzer-1.0.0-SNAPSHOT.jar,
go to your command line/command prompt and run the following command. (Assuming that you are in the same directory as the jar file above, whether
copying to a different directory or etc.)

    java -jar stock-analyzer-1.0.0-SNAPSHOT.jar

http://localhost:8080/api/v1/swagger-ui.html

Below highlights the main functionality of this service:

    - Retrieve the all data for a given stocks and the respective time frame.
    - Calculate the average monthly open and close price for the given stocks within the time frame.
    - Implement a simple trading strategy to make a profit for the given stocks within the time frame, given
       that we know only the prices at open, close, high, and low. We then consider the following:

            Since the data does not show the time series data for the stock, it is not possible to say that the
            low necessarily happens before the stock price high. Therefore, we must consider the possibilities
            of the four data points that we have, keeping in mind that we know only the values at the price points,
            and not the order in which they occur.

            Diagrams to explain possibilities:

                case 1: low before high :

                          In this case, the maximum profit would be made buying at low and selling at high. However,
                          a profit would also be made buying at open and selling at high, as well as by buying at low
                          and selling at close.

                   open      low       high        close
                  <--------------------------------->

                case 2: high before low :

                           In this case, a (maximum) profit would be made by buying at open and selling at high, then
                           buying at low and selling at close.

                   open      high       low        close
                  <--------------------------------->

                           With the above two cases, we can give the best estimate by buying at open then selling at high,
                  then buying at low and selling at close. That way, regardless of the order of occurrence, we would be
                  making a profit.

                case 3: high at open, low at close

                           If the open price is the same value as the high price, and the close price is the same as the
                           low price, then the possible profit with these given data points would be 0. (It is possible
                           for a higher low and a smaller high value to occur on a smaller time interval within the given
                           interval, but we do not have enough data to say for certain.) Thus, the recommendation would be
                           to not trade.

                     high                            low
                    open                           close
                  <--------------------------------->

                case 4: low at open, high at close

                           If this is the case, we absolutely want to buy at open/low and sell at high/close. The maximum
                           profit would then be achieved.

                     low                            high
                    open                           close
                  <--------------------------------->


                  
    - Calculate the busy trading days for each stock within the time frame. A day of trading is considered a busy if
    the trade volume for that day is greater than 10% of the average trade volume for that time frame.

