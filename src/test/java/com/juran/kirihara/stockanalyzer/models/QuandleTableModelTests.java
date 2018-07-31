package com.juran.kirihara.stockanalyzer.models;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class QuandleTableModelTests {

    @Test
    public void testListConstructorCreatesNewObject() {
        List<QuandleTableEntry> temp = new ArrayList<>();
        QuandleTableModel model = new QuandleTableModel(temp);
        Assert.assertNotSame(model.getEntries(), temp);

    }
}
