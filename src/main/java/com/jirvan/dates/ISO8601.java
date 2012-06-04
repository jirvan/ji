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

import java.text.*;
import java.util.*;

public class ISO8601 {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(TIMESTAMP_PATTERN);
    public static final String TIMESTAMP_PATTERN2 = "yyyy-MM-dd HH:mm:ssZ";
    public static final DateFormat TIMESTAMP_FORMAT2 = new SimpleDateFormat(TIMESTAMP_PATTERN);

    public static final String TO_DAY_PATTERN = "yyyy-MM-dd";
    public static final DateFormat TO_DAY_DATEFORMAT = new SimpleDateFormat(TO_DAY_PATTERN);
    public static final String TO_MINUTE_PATTERN = "yyyy-MM-dd'T'HH:mm";
    public static final DateFormat TO_MINUTE_DATEFORMAT = new SimpleDateFormat(TO_MINUTE_PATTERN);
    public static final String TO_MINUTE_PATTERN2 = "yyyy-MM-dd HH:mm";
    public static final DateFormat TO_MINUTE_DATEFORMAT2 = new SimpleDateFormat(TO_MINUTE_PATTERN2);


    public static Date fromTimestampString(String timestampString) {
        if (timestampString == null) {
            return null;
        } else {
            try {
                return TIMESTAMP_FORMAT.parse(timestampString);
            } catch (ParseException e) {
                try {
                    return TIMESTAMP_FORMAT2.parse(timestampString);
                } catch (ParseException e2) {
                    throw new RuntimeException("Day date string must be of form \"YYYY-MM-DD hh:mm:ss\" (e.g. 2012-05-31 01:49:10Z) or  \"YYYY-MM-DDThh:mm:ss\" (e.g. 2012-05-31T01:49:10Z)", e2);
                }
            }
        }
    }

    public static Date fromDayString(String dayDateString) {
        if (dayDateString == null) {
            return null;
        } else {
            try {
                return TO_DAY_DATEFORMAT.parse(dayDateString);
            } catch (ParseException e) {
                throw new RuntimeException("Day date string must be of form \"YYYY-MM-DD\" (e.g. 2012-05-01)", e);
            }
        }
    }

}
