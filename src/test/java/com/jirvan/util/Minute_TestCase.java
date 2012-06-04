package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Minute_TestCase extends TestCase {

    public void test_constructor() {
        Minute minute = new Minute(1992, 1, 26, 10, 30);
        Assert.assertEquals("Unexpected year", 1992, minute.getYear());
        Assert.assertEquals("Unexpected month", 1, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 10, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 30, minute.getMinuteInHour());
    }

    public void test_toString() {
        Minute minute = new Minute(1992, 1, 26, 10, 30);
        Assert.assertEquals("Unexpected date string", "1992-01-26 10:30", minute.toString());
        minute = new Minute(56, 7, 6, 9, 3);
        Assert.assertEquals("Unexpected date string", "0056-07-06 09:03", minute.toString());
    }

    public void test_fromString() {

        Minute minute = Minute.fromString(null);
        Assert.assertNull("Expected minute to be null", minute);

        minute = Minute.fromString("1992-01-26 23:34");
        Assert.assertEquals("Unexpected year", 1992, minute.getYear());
        Assert.assertEquals("Unexpected month", 1, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 23, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 34, minute.getMinuteInHour());

        minute = Minute.fromString("0056-07-06 06:02");
        Assert.assertEquals("Unexpected year", 56, minute.getYear());
        Assert.assertEquals("Unexpected month", 7, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 6, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 6, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 2, minute.getMinuteInHour());

        try {
            minute = Minute.fromString("0056-07-06 3:04");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
