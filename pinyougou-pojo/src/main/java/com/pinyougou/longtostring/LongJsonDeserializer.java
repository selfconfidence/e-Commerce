package com.pinyougou.longtostring;

import java.io.IOException;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LongJsonDeserializer extends JsonDeserializer<Long>{
	 private static final Logger logger = (Logger) LoggerFactory.getLogger(LongJsonDeserializer.class);

	    @Override
	    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
	        String value = jsonParser.getText();
	        try {
	            return value == null ? null : Long.parseLong(value);
	        } catch (NumberFormatException e) {
	            ((org.slf4j.Logger) logger).error("解析长整形错误", e);
	            return null;
	        }
	    }
}
