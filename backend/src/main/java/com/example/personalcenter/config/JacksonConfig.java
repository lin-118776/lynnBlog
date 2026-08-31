package com.example.personalcenter.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 配置：统一 LocalDateTime / LocalDate 的序列化与反序列化格式
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 序列化：LocalDateTime -> yyyy-MM-dd HH:mm:ss，LocalDate -> yyyy-MM-dd
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
            // 反序列化：兼容上述格式
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        };
    }
}