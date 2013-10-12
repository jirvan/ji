/*

Copyright (c) 2011, Jirvan Pty Ltd
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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public abstract class AbstractJsonHttpRequestHandler extends AbstractHttpRequestHandler {

    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String resultString = handleRequest(request);

            // Write response
            response.setHeader("Pragma", "");
            response.setHeader("Cache-Control", "");
            response.setHeader("Content-type", getContentType());
            if (getDownloadFilename() != null) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + getDownloadFilename() + "\";");
            }
            response.setHeader("Pragma", "");
            response.getOutputStream().write(resultString.getBytes());

        } catch (Throwable t) {
            throw new ServletException(t);
        }

    }

    protected String getContentType() {return DEFAULT_CONTENT_TYPE;}

    protected String getDownloadFilename() {return null;}

    protected abstract String handleRequest(HttpServletRequest request) throws ServletException;

    public static class ErrorResponse {

        private String errorMessage;
        private String stackTrace;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public ErrorResponse(Throwable cause) {
            this(cause.getMessage(), cause);
        }

        public ErrorResponse(String errorMessage, Throwable cause) {
            this.errorMessage = errorMessage;
            try {
                StringWriter stringWriter = new StringWriter();
                try {
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    try {
                        cause.printStackTrace(printWriter);
                        this.stackTrace = stringWriter.toString();
                    } finally {
                        printWriter.close();
                    }
                } finally {
                    stringWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }
    }

}
