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

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class WebClientExtended extends WebClient {

    public WebClientExtended(String username, String password, boolean useInsecureSSL) {
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.addCredentials(username, password);
        this.setCredentialsProvider(credentialsProvider);
        if (useInsecureSSL) {
            this.getOptions().setUseInsecureSSL(useInsecureSSL);
        }
    }

    public String postRequestForJson(String urlPath, Object... namesAndValues) {
        try {

            // Build parameter list
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            String currentName = null;
            for (Object nameOrValue : namesAndValues) {
                if (currentName == null) {
                    if (!(nameOrValue instanceof String)) throw new RuntimeException("names must be of type String");
                    currentName = (String) nameOrValue;
                } else {
                    nameValuePairs.add(new NameValuePair(currentName, nameOrValue.toString()));
                    currentName = null;
                }
            }
            if (currentName != null) throw new RuntimeException("There must be an equal amount of names and values");


            // Perform request
            WebRequest webRequest = new WebRequest(new URL(String.format("%s/%s", "http://localhost:8080", urlPath)), HttpMethod.POST);
            webRequest.setRequestParameters(nameValuePairs);
            Page page = this.getPage(webRequest);
            if (!"application/json".equals(page.getWebResponse().getContentType())) {
                throw new RuntimeException(String.format("Unexpected content type \"%s\" (expected \"application/json\")", page.getWebResponse().getContentType()));
            }
            return page.getWebResponse().getContentAsString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
