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

import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Base class for implementing command line processing
 *
 * @see @CommandLine
 */
public class CommandLineProcessor {

    private List<String> unprocessedArgs;
    private String usage;

    public CommandLineProcessor(String[] args, String usage) {
        this.unprocessedArgs = Arrays.asList(args);
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
        return Long.parseLong(nextArg(true));
    }

    protected BigDecimal nextArg_BigDecimal() throws UsageException {
        return new BigDecimal(nextArg());
    }

    protected BigDecimal nextArgOptional_BigDecimal() throws UsageException {
        return new BigDecimal(nextArg(true));
    }

    protected File nextArg_File() throws UsageException {
        return new File(nextArg());
    }

    protected File nextArgOptional_File() throws UsageException {
        return new File(nextArg(true));
    }

    protected Day nextArg_Day() throws UsageException {
        return Day.fromString(nextArg());
    }

    protected Day nextArgOptional_Day() throws UsageException {
        return Day.fromString(nextArg(true));
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
