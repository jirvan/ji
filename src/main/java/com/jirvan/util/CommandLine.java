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
    private Logger log;

    LogStreamer(InputStream inputStream, Logger log) {
        this.inputStream = inputStream;
        this.log = log;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

public class CommandLine {

    public static void execute(Logger log, String command) {
        execute(log, command, null);
    }

    public static void execute(Logger log, String command, String input) {
        assertNotNull(command, "command is null");
        try {

            log.info(String.format("Executing \"%s\"", command));
            Process proc = Runtime.getRuntime().exec(command);
            new LogStreamer(proc.getErrorStream(), log).start();
            new LogStreamer(proc.getInputStream(), log).start();
            Thread.sleep(3000l);
            if (input != null) {
                proc.getOutputStream().write(input.getBytes());
            }
            if (proc.waitFor() != 0) {
                throw new RuntimeException(String.format("Error executing \"%s\"", command));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}