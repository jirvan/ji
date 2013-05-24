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

package com.jirvan.html;

/** This needs to have functionality added for minifying everything into one file for production use **/
public class JsAndCss {

    private String contextPath;

    private String html;

    public JsAndCss(String contextPath) {
        this.contextPath = contextPath;
    }

    public JsAndCss setJsPaths(String... jsPaths) {
        if (jsPaths != null && jsPaths.length > 0) {
            StringBuilder stringBuilder = html != null ? new StringBuilder(html) : new StringBuilder();
            for (String jsPath : jsPaths) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n");
                stringBuilder.append(String.format("    <script src=\"%s/%s\"></script>", contextPath, jsPath));
            }
            html = stringBuilder.toString();
        }
        return this;
    }

    public JsAndCss setCssPaths(String... cssPaths) {
        if (cssPaths != null && cssPaths.length > 0) {
            StringBuilder stringBuilder = html != null ? new StringBuilder(html) : new StringBuilder();
            for (String cssPath : cssPaths) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n");
                stringBuilder.append(String.format("    <link rel=\"stylesheet\" href=\"%s/%s\" type=\"text/css\"/>", contextPath, cssPath));
            }
            html = stringBuilder.toString();
        }
        return this;
    }

    public JsAndCss setIeOnlyCssPaths(String... ieOnlyCssPaths) {
        if (ieOnlyCssPaths != null && ieOnlyCssPaths.length > 0) {
            StringBuilder stringBuilder = html != null ? new StringBuilder(html) : new StringBuilder();
            stringBuilder.append("\n    <!--[if IE]>");
            for (String ieOnlyCssPath : ieOnlyCssPaths) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n");
                stringBuilder.append(String.format("    <link rel=\"stylesheet\" href=\"%s/%s\" type=\"text/css\"/>", contextPath, ieOnlyCssPath));
            }
            stringBuilder.append("\n    <![endif]-->");
            html = stringBuilder.toString();
        }
        return this;
    }

    public String getHtml() {
        return html == null ? "" : html;
    }

}