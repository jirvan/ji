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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing a fixed
 * "granularity" of a minute.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
public class Minute {

    private int year;
    private int monthInYear;
    private int dayInMonth;
    private int hourInDay;
    private int minuteInHour;

    public Minute() {
        this(new Date());
    }

    public Minute(int year, int monthInYear, int dayInMonth, int hourInDay, int minuteInHour) {
        this.year = year;
        this.monthInYear = monthInYear;
        this.dayInMonth = dayInMonth;
        this.hourInDay = hourInDay;
        this.minuteInHour = minuteInHour;
    }

    private Minute(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
    }

    private Minute(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
    }

    public Minute(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
    }

    public static Minute now() {
        return new Minute();
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

    public int getMinuteInHour() {
        return minuteInHour;
    }

    public int getMinuteInDay() {
        return (hourInDay * 60) + minuteInHour;
    }

    public void setMinuteInHour(int minuteInHour) {
        this.minuteInHour = minuteInHour;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Calendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, minuteInHour, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month getMonth() {
        return new Month(year, monthInYear);
    }

    public Day getDay() {
        return new Day(year, monthInYear, dayInMonth);
    }

    public Hour getHour() {
        return new Hour(year, monthInYear, dayInMonth, hourInDay);
    }

    public Minute next() {
        return advanced(1);
    }

    public Minute previous() {
        return advanced(-1);
    }

    public Minute advanced(int minutes) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, minuteInHour, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.MINUTE, minutes);
        return new Minute(calendar);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Minute
               && ((Minute) obj).getYear() == year
               && ((Minute) obj).getMonthInYear() == monthInYear
               && ((Minute) obj).getDayInMonth() == dayInMonth
               && ((Minute) obj).getHourInDay() == hourInDay
               && ((Minute) obj).getMinuteInHour() == minuteInHour;
    }

    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour);
    }

    public String toFilenameSafeString() {
        return String.format("%04d%02d%02d-%02d%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour);
    }

    public String toFilenameSafeString2() {
        return String.format("%04d-%02d-%02d_%02d%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour);
    }

    public String toISO8601String() {
        return String.format("%04d-%02d-%02dT%02d:%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour);
    }

    public static Minute fromString(String string) {
        if (string == null) {
            return null;
        } else {
            Matcher m = Pattern.compile("^(\\d\\d\\d\\d)[-\\.](\\d\\d)[-\\.](\\d\\d)[ T](\\d\\d):(\\d\\d)$").matcher(string);
            if (!m.matches()) {
                throw new MinuteTimestampFormatException(string);
            }
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int minute = Integer.parseInt(m.group(5));
            return new Minute(year, month, day, hour, minute);
        }
    }

    public static Minute from(Date date) {
        if (date == null) {
            return null;
        } else {
            return new Minute(date);
        }
    }

    public static Date toDate(Minute minute) {
        if (minute == null) {
            return null;
        } else {
            return minute.getDate();
        }
    }

    public static Minute from(GregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        } else {
            return new Minute(calendar);
        }
    }

    public static Calendar toCalendar(Minute minute) {
        if (minute == null) {
            return null;
        } else {
            return minute.getCalendar();
        }
    }

    public static String formatDuration(Second from, Second to) {
        if (from == null || to == null) {
            return "";
        } else {
            return formatDuration((to.getDate().getTime() - from.getDate().getTime()) / (60 * 1000));
        }
    }

    public static String formatDuration(Long minutes) {
        if (minutes == null) {
            return "";
        } else {
            long totalMinutes = Math.abs(minutes);
            long hoursComponent = totalMinutes / 60;
            long minutesComponent = totalMinutes - (hoursComponent * 60);
            return String.format("%s%02d:%02d", minutes < 0 ? "-" : "", hoursComponent, minutesComponent);
        }
    }

}
