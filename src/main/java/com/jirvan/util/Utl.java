/*

Copyright (c) 2013, Jirvan Pty Ltd
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

package com.jirvan.util;

import com.jirvan.dates.*;

import java.io.*;
import java.math.*;
import java.util.*;

public class Utl {

    public static <T> ArrayList<T> newArrayList(T... items) {
        ArrayList<T> list = new ArrayList<T>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

    public static boolean areEqual(Integer value1, Integer value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Long value1, Long value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Float value1, Float value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Double value1, Double value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(BigInteger value1, BigInteger value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(String value1, String value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Date value1, Date value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Month value1, Month value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Day value1, Day value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Hour value1, Hour value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Minute value1, Minute value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Second value1, Second value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Millisecond value1, Millisecond value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static String getStackTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try {
            t.printStackTrace(printWriter);
        } finally {
            printWriter.close();
        }
        return stringWriter.toString();
    }

}
