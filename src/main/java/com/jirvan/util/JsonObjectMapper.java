package com.jirvan.util;

import com.jirvan.dates.*;
import org.codehaus.jackson.map.*;

public class JsonObjectMapper extends ObjectMapper {

    public JsonObjectMapper() {
        this.registerModule(Dates.getSerializerDeserializerModule());
    }

}
