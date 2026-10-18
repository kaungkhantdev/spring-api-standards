package com.astronity.pos.common.config;

import com.astronity.pos.common.enums.DbEnum;
import java.util.Arrays;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ?status=open in a URL uses the same lowercase value as the JSON body
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToDbEnumConverterFactory());
    }

    private static final class StringToDbEnumConverterFactory implements ConverterFactory<String, DbEnum> {

        @Override
        public <T extends DbEnum> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> Arrays.stream(targetType.getEnumConstants())
                    .filter(constant -> constant.getValue().equals(source))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown value '" + source + "'"));
        }
    }
}
