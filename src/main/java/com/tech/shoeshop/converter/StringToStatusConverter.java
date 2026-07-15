package com.tech.shoeshop.converter;

import com.tech.shoeshop.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StringToStatusConverter implements Converter<String, Status> {

    @Override
    public Status convert(String source) {

        if(source == null || source.isBlank()){
            return null;
        }

        return Arrays.stream(Status.values())
                .filter(status -> status.name().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + source));
    }
}
