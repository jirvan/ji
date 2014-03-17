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

import org.apache.log4j.*;

import java.io.*;

import static com.jirvan.util.Assertions.*;

class LogStreamer extends Thread {

    private InputStream inputStream;
    private String logLinePrefix;
    private Logger log;

    LogStreamer(InputStream inputStream, String logLinePrefix, Logger log) {
        this.inputStream = inputStream;
        this.logLinePrefix = logLinePrefix;
        this.log = log;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (logLinePrefix != null) {
                    log.info(logLinePrefix + line);
                } else {
                    log.info(line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

class StringBufferStreamer extends Thread {

    private InputStream inputStream;
    private StringBuffer stringBuffer;

    StringBufferStreamer(InputStream inputStream, StringBuffer stringBuffer) {
        this.inputStream = inputStream;
        this.stringBuffer = stringBuffer;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append('\n');
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

public class CommandLine {

    public static void execute(Logger log, String command) {
        execute(null, log, command);
    }

    public static void execute(String logLinePrefix, Logger log, String command) {
        assertNotNull(command, "command is null");
        try {

            log.info(String.format("%sExecuting \"%s\"", logLinePrefix == null ? "" : logLinePrefix, command));
            Process proc = Runtime.getRuntime().exec(command);
            new LogStreamer(proc.getErrorStream(), logLinePrefix, log).start();
            new LogStreamer(proc.getInputStream(), logLinePrefix, log).start();
            if (proc.waitFor() != 0) {
                throw new RuntimeException(String.format("Error executing \"%s\"", command));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static String execute(String command) {
        assertNotNull(command, "command is null");
        try {
            StringBuffer stderrStringBuffer = new StringBuffer();
            StringBuffer stdoutStringBuffer = new StringBuffer();
            Process proc = Runtime.getRuntime().exec(command);
            new StringBufferStreamer(proc.getErrorStream(), stderrStringBuffer).start();
            new StringBufferStreamer(proc.getInputStream(), stdoutStringBuffer).start();
            if (proc.waitFor() != 0) {
                throw new RuntimeException(String.format("Error executing \"%s\":\n%s", command, stderrStringBuffer.toString()));
            } else {
                return stdoutStringBuffer.toString();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}