package com.juran.kirihara.stockanalyzer.models;

import com.juran.kirihara.stockanalyzer.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

@RunWith(JUnit4.class)
public class QuandlTableEntryTests {
    private QuandlTableEntry entry = new QuandlTableEntry();

    @Test
    public void testDateParser() throws ParseException {
        String dateToParse = "2018-01-02";
        entry.setDate(dateToParse);
        Assert.assertEquals(dateToParse, Constants.formatWithDate.format(entry.getDate()));
    }


}
