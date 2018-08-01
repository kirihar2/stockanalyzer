package com.juran.kirihara.stockanalyzer.models;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class QuandlTableModelTests {

    @Test
    public void testListConstructorCreatesNewObject() {
        List<QuandlTableEntry> temp = new ArrayList<>();
        QuandlTableModel model = new QuandlTableModel(temp);
        Assert.assertNotSame(model.getEntries(), temp);

    }
}
