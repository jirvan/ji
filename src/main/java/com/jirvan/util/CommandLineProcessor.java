/*

Copyright (c) 2014, Jirvan Pty Ltd
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
import com.jirvan.lang.*;

import javax.sql.*;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;

/**
 * Base class for implementing command line processing
 *
 * @see @CommandLine
 */
public class CommandLineProcessor {

    private ArrayList<String> unprocessedArgs;
    private String usage;

    public CommandLineProcessor(String[] args, String usage) {
        this.unprocessedArgs = Lists.createArrayList(args);
        this.usage = usage;
    }

    protected boolean extractBooleanOption(String... optionVariations) {
        for (String unprocessedArg : unprocessedArgs) {
            for (String optionVariation : optionVariations) {
                if (optionVariation.equals(unprocessedArg)) {
                    unprocessedArgs.remove(unprocessedArg);
                    return true;
                }
            }
        }
        return false;
    }

    protected File extractFileOption(String... optionVariations) throws UsageException {
        String stringValue = extractStringOption(optionVariations);
        return stringValue == null ? null : new File(stringValue);
    }

    protected Integer extractIntegerOption(String... optionVariations) throws UsageException {
        String stringValue = extractStringOption(optionVariations);
        return stringValue == null ? null : Integer.parseInt(stringValue);
    }

    protected Float extractFloatOption(String... optionVariations) throws UsageException {
        String stringValue = extractStringOption(optionVariations);
        return stringValue == null ? null : Float.parseFloat(stringValue);
    }

    protected BigDecimal extractBigDecimalOption(String... optionVariations) throws UsageException {
        String stringValue = extractStringOption(optionVariations);
        return stringValue == null ? null : new BigDecimal(stringValue);
    }

    protected String extractStringOption(String... optionVariations) throws UsageException {
        for (int i = 0; i < unprocessedArgs.size(); i++) {
            String unprocessedArg = unprocessedArgs.get(i);
            for (String optionVariation : optionVariations) {
                if (optionVariation.equals(unprocessedArg)) {
                    if (i + 1 > unprocessedArgs.size() - 1) {
                        throw new UsageException();
                    } else {
                        String value = unprocessedArgs.remove(i + 1);
                        unprocessedArgs.remove(i);
                        return value;
                    }
                }
            }
        }
        return null;
    }

    protected int numberOfRemainingArgs() {
        return unprocessedArgs.size();
    }

    private String nextArg(boolean optional) throws UsageException {
        if (unprocessedArgs.isEmpty()) {
            if (optional) {
                return null;
            } else {
                throw new UsageException();
            }
        } else {
            String nextArg = unprocessedArgs.remove(0);
            if (nextArg.startsWith("-")) {
                throw new UsageException();
            } else {
                return nextArg;
            }
        }
    }

    protected String nextArg() throws UsageException {
        return nextArg(false);
    }

    protected String nextArgOptional() throws UsageException {
        return nextArg(true);
    }

    protected Long nextArg_Long() throws UsageException {
        return Long.parseLong(nextArg());
    }

    protected Long nextArgOptional_Long() throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? null : Long.parseLong(stringValue);
    }

    protected Integer nextArgOptional_Integer() throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? null : Integer.parseInt(stringValue);
    }

    protected Integer nextArgOptional_Integer(int defaultValue) throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? defaultValue : Integer.parseInt(stringValue);
    }

    protected BigDecimal nextArg_BigDecimal() throws UsageException {
        return new BigDecimal(nextArg());
    }

    protected BigDecimal nextArgOptional_BigDecimal() throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? null : new BigDecimal(stringValue);
    }

    protected File nextArg_File() throws UsageException {
        return new File(nextArg());
    }

    protected File nextArgOptional_File() throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? null : new File(stringValue);
    }

    protected Day nextArg_Day() throws UsageException {
        return Day.fromString(nextArg());
    }

    protected DataSource nextArg_DataSource() throws UsageException {
        DataSource dataSource;
        String connectString = nextArg();
        Pattern databaseTypePattern = Pattern.compile("^([^:]+):.*$");
        Matcher m;
        if (connectString.toLowerCase().startsWith("postgresql:")) {
            dataSource = Jdbc.getPostgresDataSource(connectString.replaceFirst("postgresql:", ""));
        } else if (connectString.toLowerCase().startsWith("sqlserver:")) {
            dataSource = Jdbc.getSqlServerDataSource(connectString.replaceFirst("sqlserver:", ""));
        } else if ((m = databaseTypePattern.matcher(connectString)).matches()) {
            throw new MessageException(String.format("Unsupported database type \"%s\" (supported types are \"postgresql\", \"sqlserver\"", m.group(1)));
        } else {
            throw new MessageException(String.format("Invalid connect string \"%s\"", connectString));
        }
        return dataSource;
    }

    protected Day nextArgOptional_Day() throws UsageException {
        String stringValue = nextArgOptional();
        return stringValue == null ? null : Day.fromString(stringValue);
    }

    protected void verifyNoMoreArgs() throws UsageException {
        if (unprocessedArgs.size() > 0) throw new UsageException(usage);
    }

    public class UsageException extends Exception {

        public UsageException() {
            super(usage);
        }

        public UsageException(String message) {
            super(message);
        }

    }

}
