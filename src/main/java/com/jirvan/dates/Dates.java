/*

Copyright (c) 2012, Jirvan Pty Ltd
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

package com.jirvan.dates;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.module.*;
import org.codehaus.jackson.node.*;

import java.io.*;

public class Dates {

    public static String formatElapsedTime(int minutes) {
        int hours = minutes / 60;
        int minuteInHour = minutes - (hours * 60);
        return String.format("%d:%02d", hours, minuteInHour);
    }

    public static String formatTimeOfDay(int minutes) {
        int hours = minutes / 60;
        int minuteInHour = minutes - (hours * 60);
        return String.format("%02d:%02d", hours, minuteInHour);
    }

    public static Integer hourPart(Integer totalMinutes) {
        return totalMinutes == null
               ? null
               : totalMinutes / 60;
    }

    public static Integer minutePart(Integer totalMinutes) {
        return totalMinutes == null
               ? null
               : totalMinutes - ((totalMinutes / 60) * 60);
    }

    public static SimpleModule getSerializerDeserializerModule() {
        SimpleModule module = new SimpleModule("JiDatesSerializerModule", new Version(1, 0, 0, null));

        module.addSerializer(Month.class, new JsonSerializer<Month>() {
            public void serialize(Month value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Month.class, new JsonDeserializer<Month>() {
            public Month deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Month.fromString((jsonNode.getTextValue()));
            }
        });

        module.addSerializer(Day.class, new JsonSerializer<Day>() {
            public void serialize(Day value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toJavascriptString());
            }
        });
        module.addDeserializer(Day.class, new JsonDeserializer<Day>() {
            public Day deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Day.fromString((jsonNode.getTextValue()));
            }
        });

        module.addSerializer(Hour.class, new JsonSerializer<Hour>() {
            public void serialize(Hour value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Hour.class, new JsonDeserializer<Hour>() {
            public Hour deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Hour.fromString((jsonNode.getTextValue()));
            }
        });

        module.addSerializer(Minute.class, new JsonSerializer<Minute>() {
            public void serialize(Minute value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });

        return module;
    }

}
