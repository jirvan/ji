package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Day_TestCase extends TestCase {

    public void test_constructor() {
        Day day = new Day(1992, 1, 26);
        Assert.assertEquals("Unexpected year", 1992, day.getYear());
        Assert.assertEquals("Unexpected month", 1, day.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, day.getDayInMonth());
    }

    public void test_toString() {
        Day dateToDay = new Day(1992, 1, 26);
        Assert.assertEquals("Unexpected date string", "1992-01-26", dateToDay.toString());
        dateToDay = new Day(56, 7, 6);
        Assert.assertEquals("Unexpected date string", "0056-07-06", dateToDay.toString());
    }

    public void test_toFilenameSafeString() {
        Day day = new Day(1992, 1, 26);
        Assert.assertEquals("Unexpected day string", "19920126", day.toFilenameSafeString());
        day = new Day(56, 7, 6);
        Assert.assertEquals("Unexpected day string", "00560706", day.toFilenameSafeString());
    }

    public void test_fromString() {

        Day day = Day.fromString(null);
        Assert.assertNull("Expected Day to be null", day);

        day = Day.fromString("1992-01-26");
        Assert.assertEquals("Unexpected year", 1992, day.getYear());
        Assert.assertEquals("Unexpected month", 1, day.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, day.getDayInMonth());

        day = Day.fromString("0056-07-06");
        Assert.assertEquals("Unexpected year", 56, day.getYear());
        Assert.assertEquals("Unexpected month", 7, day.getMonthInYear());
        Assert.assertEquals("Unexpected day", 6, day.getDayInMonth());

        try {
            day = Day.fromString("56-07-06");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
