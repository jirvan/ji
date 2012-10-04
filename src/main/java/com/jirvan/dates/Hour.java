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

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing an fixed
 * "granularity" of an hour.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
public class Hour {

    private int year;
    private int monthInYear;
    private int dayInMonth;
    private int hourInDay;

    public Hour() {
        this(new Date());
    }

    public Hour(int year, int monthInYear, int dayInMonth, int hourInDay) {
        this.year = year;
        this.monthInYear = monthInYear;
        this.dayInMonth = dayInMonth;
        this.hourInDay = hourInDay;
    }

    public Hour(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
    }

    public Hour(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
    }

    public Hour(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
    }

    public static Hour current() {
        return new Hour();
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

    public int getHourInDay() {
        return hourInDay;
    }

    public void setHourInDay(int hourInDay) {
        this.hourInDay = hourInDay;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Calendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month getMonth() {
        return new Month(year, monthInYear);
    }

    public Day getDay() {
        return new Day(year, monthInYear, dayInMonth);
    }

    public Hour next() {
        return advanced(1);
    }

    public Hour previous() {
        return advanced(-1);
    }

    public Hour advanced(int hours) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, hours);
        return new Hour(calendar);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Hour
               && ((Hour) obj).getYear() == year
               && ((Hour) obj).getMonthInYear() == monthInYear
               && ((Hour) obj).getDayInMonth() == dayInMonth
               && ((Hour) obj).getHourInDay() == hourInDay;
    }

    public String toString() {
        return String.format("%04d-%02d-%02d %02d", year, monthInYear, dayInMonth, hourInDay);
    }

    public String toISO8601String() {
        return String.format("%04d-%02d-%02dT%02d", year, monthInYear, dayInMonth, hourInDay);
    }

    public static Hour fromString(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            Matcher m = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)[ T](\\d\\d)$").matcher(dateString);
            if (!m.matches()) {
                throw new RuntimeException("Hour date string must be of form \"YYYY-MM-DD hh\" (e.g. 2012-05-01 09:30) or YYYY-MM-DDThh (e.g. 2012-05-01T09)");
            }
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            return new Hour(year, month, day, hour);
        }
    }

/*
    public static class Serializer implements JsonSerializer<Hour> {
        public JsonElement serialize(Hour hour, Type type, JsonSerializationContext jsonSerializationContext) {
            return hour == null
                   ? new JsonNull()
                   : new JsonPrimitive(hour.toString());
        }
    }
*/

}
