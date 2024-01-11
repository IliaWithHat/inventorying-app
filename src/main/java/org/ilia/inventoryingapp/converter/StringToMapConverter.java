package org.ilia.inventoryingapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StringToMapConverter implements Converter<String, Map<String, String>> {

    @Override
    public Map<String, String> convert(String str) {
        if (str.isBlank())
            return null;
        HashMap<String, String> map = new HashMap<>();
        for (String strings : str.split(";")) {
            String[] keyValue = strings.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }
}
