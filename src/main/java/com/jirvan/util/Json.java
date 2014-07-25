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
import com.jirvan.json.*;
import com.jirvan.util.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.module.*;
import org.codehaus.jackson.node.*;

import java.io.*;

public class Json {

    private static final ObjectMapper OBJECT_MAPPER = setUpObjectMapper(false);
    private static final ObjectMapper OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES = setUpObjectMapper(true);
    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer()
                                                                   .withPrettyPrinter(new JsonPrettyPrinter());

    public static <T> T treeToValue(JsonNode n, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return OBJECT_MAPPER.readValue(n, valueType);
    }

    public static JsonNode readTree(String content) throws IOException, JsonProcessingException {
        return OBJECT_MAPPER.readTree(content);
    }

    public static String toJsonString(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object).replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String jsonString, Class<T> valueType) {
        return fromJsonString(jsonString, valueType, false);
    }

    public static <T> T fromJsonString(String jsonString, Class<T> valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.readValue(jsonString, valueType)
                   : OBJECT_MAPPER.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType) {
        return fromJsonResourceFile(anchorClass, filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getResourceFileString(anchorClass, filename), valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonFile(File file, Class<T> valueType) {
        return fromJsonString(Io.getFileString(file), valueType, false);
    }

    public static <T> T fromJsonFile(File file, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getFileString(file), valueType, ignoreUnknownProperties);
    }

    private static ObjectMapper setUpObjectMapper(boolean ignoreUnknownProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(Dates.getSerializerDeserializerModule());
        objectMapper.registerModule(getJsonShapeShifterSerializerModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        if (ignoreUnknownProperties) objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private static SimpleModule getJsonShapeShifterSerializerModule() {
        SimpleModule module = new SimpleModule("JsonShapeShifterSerializerModule", new Version(1, 0, 0, null));

        module.addDeserializer(JsonShapeShifter.class, new JsonDeserializer<JsonShapeShifter>() {
            public JsonShapeShifter deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

                ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

                // Get and tree node, and remove type field
                JsonNode jsonNode = mapper.readTree(jsonParser);
                if (!(jsonNode instanceof ObjectNode)) {
                    throw new RuntimeException("Expected JsonShapeShifter nodes to be ObjectNodes");
                }
                JsonNode typeNode = ((ObjectNode) jsonNode).remove("type");
                if (typeNode == null) {
                    throw new RuntimeException("Expected JsonShapeShifter node to have a type field");
                }
                if (!(typeNode instanceof TextNode)) {
                    throw new RuntimeException("Expected type to be a text node");
                }

                // Determine the object class based on the type field
                String className = typeNode.asText();
                Class configClass = null;
                try {
                    configClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(String.format("JsonShapeShifter type \"%s\" is not a recognized class", className));
                }

                // Extract and return the object
                return (JsonShapeShifter) mapper.treeToValue(jsonNode, configClass);
            }
        });

        return module;
    }

}
