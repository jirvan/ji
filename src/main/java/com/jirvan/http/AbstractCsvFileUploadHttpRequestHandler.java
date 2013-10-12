/*

Copyright (c) 2008,2009 Jirvan Pty Ltd
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

package com.jirvan.http;

import java.text.*;
import java.util.*;

public abstract class AbstractCsvFileUploadHttpRequestHandler extends AbstractFileUploadHttpRequestHandler {

    protected void checkColumnNames(String[] columnNames, String[] expectedColumnNames) {
        if (columnNames == null || columnNames.length == 0) {
            throw new RuntimeException("First row is empty (expected column names");
        }
        for (int i = 0; i < expectedColumnNames.length; i++) {
            checkColumnHeader(columnNames, expectedColumnNames[i], i);

        }
    }

    protected void checkColumnHeader(String[] columnNames, String expectedColumnName, int columnIndex) {
        if (columnNames.length < columnIndex + 1 || !expectedColumnName.equals(columnNames[columnIndex])) {
            throw new RuntimeException(String.format("Column \"%s\" is missing (it should be column %d)", expectedColumnName, columnIndex + 1));
        }
    }

    protected static String getMandatoryCell_String(String[] columnNames, String[] line, int lineNumber, int columnIndex) {
        try {
            if (line.length < columnIndex + 1 || line[columnIndex] == null || "".equals(line[columnIndex])) {
                throw new RuntimeException(String.format("Exception processing line %d: No value for column \"%s\"", lineNumber, columnNames[columnIndex]));
            } else {
                return line[columnIndex];
            }
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error processing %s in line %d: %s", columnNames[columnIndex], lineNumber, t.getMessage()));
        }
    }

    protected static int getMandatoryCell_int(String[] columnNames, String[] line, int lineNumber, int columnIndex) {
        try {
            String stringValue = getMandatoryCell_String(columnNames, line, lineNumber, columnIndex);
            return Integer.valueOf(stringValue.replaceAll(",", ""));
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error processing %s in line %d: %s", columnNames[columnIndex], lineNumber, t.getMessage()));
        }
    }

    protected static long getMandatoryCell_long(String[] columnNames, String[] line, int lineNumber, int columnIndex) {
        try {
            String stringValue = getMandatoryCell_String(columnNames, line, lineNumber, columnIndex);
            return Long.valueOf(stringValue.replaceAll(",", ""));
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error processing %s in line %d: %s", columnNames[columnIndex], lineNumber, t.getMessage()));
        }
    }

    protected static double getMandatoryCell_double(String[] columnNames, String[] line, int lineNumber, int columnIndex) {
        try {
            String stringValue = getMandatoryCell_String(columnNames, line, lineNumber, columnIndex);
            return Double.valueOf(stringValue.replaceAll(",", ""));
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error processing %s in line %d: %s", columnNames[columnIndex], lineNumber, t.getMessage()));
        }
    }

    protected static Date getMandatoryCell_Date(DateFormat dateFormat, String[] columnNames, String[] line, int lineNumber, int columnIndex) {
        try {
            String stringValue = getMandatoryCell_String(columnNames, line, lineNumber, columnIndex);
            try {
                return dateFormat.parse(stringValue);
            } catch (ParseException e) {
                throw new RuntimeException(String.format("Invalid date/time value \"%s\"", stringValue), e);
            }
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error processing %s in line %d: %s", columnNames[columnIndex], lineNumber, t.getMessage()));
        }
    }

}