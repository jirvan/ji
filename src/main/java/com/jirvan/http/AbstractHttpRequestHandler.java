/*

Copyright (c) 2008,2009,2010,2011,2012,2013 Jirvan Pty Ltd
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

import com.jirvan.dates.*;
import org.springframework.web.*;

import javax.servlet.http.*;
import java.io.*;
import java.math.*;

public abstract class AbstractHttpRequestHandler implements HttpRequestHandler {

    protected String getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("Couldn't get current user - HttpServletRequest is null");
        }
        String remoteUser = request.getRemoteUser();
        if (remoteUser == null) {
            throw new RuntimeException("Couldn't get current user - request.getRemoteUser( returned null");
        }
        return remoteUser;
    }

    protected File getResourceDirectory(HttpServletRequest request, String resourceDirectoryPath) {
        String directoryPath = optionalContextInitParameter_String(request, resourceDirectoryPath);
        if (directoryPath == null) {
            throw new RuntimeException("Could not get resource directory, context init parameter \"" + resourceDirectoryPath + "\" has not been set)");
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new RuntimeException("Resource directory \"" + directoryPath + "\" does not exist.\n" +
                                       "(Servlet context init parameter \"" + resourceDirectoryPath + "\"");
        } else if (!directory.isDirectory()) {
            throw new RuntimeException("Resource directory \"" + directoryPath + "\" exists but is not a directory.\n" +
                                       "(Servlet context init parameter \"" + resourceDirectoryPath + "\"");
        }
        return directory;
    }

    public final String mandatoryContextInitParameter_String(HttpServletRequest request, String parameterName) {
        String value = optionalContextInitParameter_String(request, parameterName);
        if (value != null) {
            return value;
        } else {
            throw new RuntimeException("Servlet context init parameter \"" + parameterName + "\" has not been set");
        }
    }

    public final String optionalContextInitParameter_String(HttpServletRequest request, String parameterName) {
        return request.getSession().getServletContext().getInitParameter(parameterName);
    }


    protected static String extractOptionalParameter_String(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return stringValue;
    }

    protected static String extractMandatoryParameter_String(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return stringValue;
    }

    protected static Integer extractOptionalParameter_Integer(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new Integer(stringValue);
    }

    protected static Integer extractMandatoryParameter_Integer(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new Integer(stringValue);
    }

    protected static Long extractOptionalParameter_Long(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new Long(stringValue);
    }

    protected static Long extractMandatoryParameter_Long(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new Long(stringValue);
    }

    protected static BigInteger extractOptionalParameter_BigInteger(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new BigInteger(stringValue);
    }

    protected static BigInteger extractMandatoryParameter_BigInteger(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new BigInteger(stringValue);
    }

    protected static Float extractOptionalParameter_Float(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new Float(stringValue);
    }

    protected static Float extractMandatoryParameter_Float(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new Float(stringValue);
    }

    protected static Double extractOptionalParameter_Double(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new Double(stringValue);
    }

    protected static Double extractMandatoryParameter_Double(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new Double(stringValue);
    }

    protected static Day extractOptionalParameter_Day(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return Day.fromString(stringValue);
    }

    protected static Day extractMandatoryParameter_Day(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return Day.fromString(stringValue);
    }

    protected static BigDecimal extractOptionalParameter_BigDecimal(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new BigDecimal(stringValue);
    }

    protected static BigDecimal extractMandatoryParameter_BigDecimal(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return new BigDecimal(stringValue);
    }

    protected static int extractMandatoryParameter_int(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return Integer.parseInt(stringValue);
    }

    protected static long extractMandatoryParameter_long(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return Long.parseLong(stringValue);
    }

    protected static float extractMandatoryParameter_float(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return Float.parseFloat(stringValue);
    }

    protected static double extractMandatoryParameter_double(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return Double.parseDouble(stringValue);
    }

    protected static Boolean extractOptionalParameter_Boolean(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return null;
        }
        return new Boolean("y".equalsIgnoreCase(stringValue)
                           || "yes".equalsIgnoreCase(stringValue)
                           || "true".equalsIgnoreCase(stringValue)
                           || "t".equalsIgnoreCase(stringValue));
    }

    protected static boolean extractOptionalParameter_boolean(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return false;
        }
        return "y".equalsIgnoreCase(stringValue)
               || "yes".equalsIgnoreCase(stringValue)
               || "true".equalsIgnoreCase(stringValue)
               || "t".equalsIgnoreCase(stringValue);
    }

    protected static boolean extractMandatoryParameter_boolean(HttpServletRequest request, String parameterName) throws ServletMandatoryParameterException {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            throw new ServletMandatoryParameterException("\"" + parameterName + "\" parameter must be provided");
        }
        return "y".equalsIgnoreCase(stringValue)
               || "yes".equalsIgnoreCase(stringValue)
               || "true".equalsIgnoreCase(stringValue)
               || "t".equalsIgnoreCase(stringValue);
    }

    protected static boolean extractParameter_boolean(HttpServletRequest request, String parameterName) {
        String stringValue = request.getParameter(parameterName);
        if (stringValue == null || stringValue.length() == 0) {
            return false;
        }
        return "y".equalsIgnoreCase(stringValue)
               || "yes".equalsIgnoreCase(stringValue)
               || "true".equalsIgnoreCase(stringValue)
               || "t".equalsIgnoreCase(stringValue);
    }

}
