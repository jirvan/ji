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
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

import java.io.*;

public class Json {

    private static final ObjectMapper OBJECT_MAPPER = setUpObjectMapper();
    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer()
                                                                   .withDefaultPrettyPrinter();

    public static String toJsonString(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object).replaceAll("\\r", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String jsonString, Class<T> valueType) {
        try {

            return OBJECT_MAPPER.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType) {
        return fromJsonResourceFile(anchorObject.getClass(), filename, valueType);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType) {
        try {

            String jsonString = Io.getResourceFileString(anchorClass, filename);
            return OBJECT_MAPPER.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper setUpObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(Dates.getSerializerModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return objectMapper;
    }

}
