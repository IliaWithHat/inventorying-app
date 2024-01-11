package org.ilia.inventoryingapp.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StringToMapConverter implements Converter<String, Map<String, Object>> {

    @SneakyThrows
    @Override
    public Map<String, Object> convert(String json) {
        //TODO made normal converter
        if (json.isBlank())
            return null;
        return new ObjectMapper().readValue(json, Map.class);
    }
}
