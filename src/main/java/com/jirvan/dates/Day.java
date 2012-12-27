/*

Copyright (c) 2012, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.dates;

//import com.google.gson.*;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing an fixed
 * "granularity" of a day.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
public class Day implements JsonSerializable {

    private int year;
    private int monthInYear;
    private int dayInMonth;

    public Day() {
        this(new Date());
    }

    public Day(int year, int monthInYear, int dayInMonth) {
        this.year = year;
        this.monthInYear = monthInYear;
        this.dayInMonth = dayInMonth;
    }

    public Day(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public Day(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public Day(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public static Day today() {
        return new Day();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthInYear() {
        return monthInYear;
    }

    public void setMonthInYear(int monthInYear) {
        this.monthInYear = monthInYear;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public GregorianCalendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month getMonth() {
        return new Month(year, monthInYear);
    }

    public Day next() {
        return advanced(1);
    }

    public Day previous() {
        return advanced(-1);
    }

    public Day advanced(int days) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.DATE, days);
        return new Day(calendar);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Day
               && ((Day) obj).getYear() == year
               && ((Day) obj).getMonthInYear() == monthInYear
               && ((Day) obj).getDayInMonth() == dayInMonth;
    }

    public int daysSince(Day anotherDay) {
        return - daysUntil(anotherDay);
    }

    public int daysUntil(Day anotherDay) {
        GregorianCalendar thisCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        thisCalendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        thisCalendar.set(GregorianCalendar.MILLISECOND, 0);
        GregorianCalendar theOtherCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        theOtherCalendar.set(anotherDay.year, anotherDay.monthInYear - 1, anotherDay.dayInMonth, 0, 0, 0);
        theOtherCalendar.set(GregorianCalendar.MILLISECOND, 0);
        long milliseconds = theOtherCalendar.getTimeInMillis() - thisCalendar.getTimeInMillis();
        double fractionalDays = (double) milliseconds / ((double) (24 * 60 * 60 * 1000));
        long longDays = Math.round(fractionalDays);
        if (longDays > Integer.MAX_VALUE) {
            throw new RuntimeException("Number of days too big to represent as an int");
        } else {
            return (int) longDays;
        }

    }

    public String toString() {
        return String.format("%04d-%02d-%02d", year, monthInYear, dayInMonth);
    }

    public static Day fromString(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            Matcher m = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)$").matcher(dateString);
            if (!m.matches()) {
                throw new RuntimeException("Day date string must be of form \"YYYY-MM-DD\" (e.g. 2012-05-01)");
            }
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            return new Day(year, month, day);
        }
    }

    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(this.toString());
    }

}
