/*

Copyright (c) 2012,2013 Jirvan Pty Ltd
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

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;

import java.io.*;
import java.sql.*;
import java.util.Date;
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
public class Day implements Cloneable, UserType, Serializable {

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

    @Override public Day clone() {
        return new Day(year, monthInYear, dayInMonth);
    }

    private Day(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    private Day(Date date) {
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


    public static Day from(GregorianCalendar calendar) {
        return calendar == null ? null : new Day(calendar);
    }

    public static Day from(Date date) {
        return date == null ? null : new Day(date);
    }

    public static Day from(Date date, TimeZone timeZone) {
        return date == null ? null : new Day(date, timeZone);
    }

    public static Day today() {
        return new Day();
    }

    public int getYear() {
        return year;
    }

    public Day setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonthInYear() {
        return monthInYear;
    }

    public Day setMonthInYear(int monthInYear) {
        this.monthInYear = monthInYear;
        return this;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public Day setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
        return this;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Timestamp getTimestamp() {
        return new Timestamp(getCalendar().getTimeInMillis());
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

    public DayOfWeek getDayOfWeek() {
        int dayOfWeek = getCalendar().get(GregorianCalendar.DAY_OF_WEEK);
        if (dayOfWeek == GregorianCalendar.MONDAY) {
            return DayOfWeek.Monday;
        } else if (dayOfWeek == GregorianCalendar.TUESDAY) {
            return DayOfWeek.Tuesday;
        } else if (dayOfWeek == GregorianCalendar.WEDNESDAY) {
            return DayOfWeek.Wednesday;
        } else if (dayOfWeek == GregorianCalendar.THURSDAY) {
            return DayOfWeek.Thursday;
        } else if (dayOfWeek == GregorianCalendar.FRIDAY) {
            return DayOfWeek.Friday;
        } else if (dayOfWeek == GregorianCalendar.SATURDAY) {
            return DayOfWeek.Saturday;
        } else if (dayOfWeek == GregorianCalendar.SUNDAY) {
            return DayOfWeek.Sunday;
        } else {
            throw new RuntimeException(String.format("Unexpected error: Unrecognized GregorianCalendar.DAY_OF_WEEK %d", dayOfWeek));
        }
    }

    public boolean isOnAWeekend() {
        DayOfWeek dayOfWeek = getDayOfWeek();
        return dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
    }

    public boolean isAWeekday() {
        DayOfWeek dayOfWeek = getDayOfWeek();
        return dayOfWeek != DayOfWeek.Saturday && dayOfWeek != DayOfWeek.Sunday;
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

    public Day receded(int days) {
        return advanced(-days);
    }

    public Day advancedMonths(int months) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.MONTH, months);
        return new Day(calendar);
    }

    public Day recededMonths(int months) {
        return advancedMonths(-months);
    }

    public Day advancedWeeks(int weeks) {
        return advanced(weeks * 7);
    }

    public Day recededWeeks(int weeks) {
        return advancedWeeks(-weeks);
    }

    public Day advanceToNextWeekday() {
        Day day = new Day(year, monthInYear, dayInMonth);
        while (day.isOnAWeekend()) {
            day = day.advanced(1);
        }
        return day;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Day
               && ((Day) obj).getYear() == year
               && ((Day) obj).getMonthInYear() == monthInYear
               && ((Day) obj).getDayInMonth() == dayInMonth;
    }

    public boolean after(Day anotherDay) {
        if (anotherDay == null) {
            throw new NullPointerException("anotherDay cannot be null");
        } else {
            if (year > anotherDay.getYear()) {
                return true;
            } else if (year < anotherDay.getYear()) {
                return false;
            } else {
                if (monthInYear > anotherDay.getMonthInYear()) {
                    return true;
                } else if (monthInYear < anotherDay.getMonthInYear()) {
                    return false;
                } else {
                    if (dayInMonth > anotherDay.getDayInMonth()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public boolean before(Day anotherDay) {
        if (anotherDay == null) {
            throw new NullPointerException("anotherDay cannot be null");
        } else {
            if (year < anotherDay.getYear()) {
                return true;
            } else if (year > anotherDay.getYear()) {
                return false;
            } else {
                if (monthInYear < anotherDay.getMonthInYear()) {
                    return true;
                } else if (monthInYear > anotherDay.getMonthInYear()) {
                    return false;
                } else {
                    if (dayInMonth < anotherDay.getDayInMonth()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public int daysSince(Day anotherDay) {
        return -daysUntil(anotherDay);
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

    public static Date toDate(Day day) {
        return day == null ? null : day.getDate();
    }

    public String toString() {
        return String.format("%04d-%02d-%02d", year, monthInYear, dayInMonth);
    }

    public String toFilenameSafeString() {
        return String.format("%04d%02d%02d", year, monthInYear, dayInMonth);
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

    //************  Hibernate UserType implementation ************
    public int[] sqlTypes() {
        return new int[]{Types.DATE};
    }

    public Class returnedClass() {
        return Day.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && x.equals(y));
    }

    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Date date = rs.getTimestamp(names[0]);
        if (rs.wasNull()) {
            return null;
        } else {
            return new Day(date);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.DATE);
        } else {
            verifyType(value);
            st.setTimestamp(index, ((Day) value).getTimestamp());
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        verifyType(value);
        return clone();
    }

    public boolean isMutable() {
        return true;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        verifyType(value);
        return value == null ? null : ((Day)value).clone();
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        verifyType(cached);
        return cached == null ? null : ((Day)cached).clone();
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        verifyType(original);
        return original == null ? null : ((Day)original).clone();
    }

    private void verifyType(Object value) {
        if (value != null && !(value instanceof Day)) {
            throw new UnsupportedOperationException(String.format("expected an object of type %s", this.getClass().getName()));
        }
    }

}
