package org.example.sdj.min.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@ReadingConverter
public class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {


    @Override
    public Date convert(LocalDateTime source) {

        return source != null ? Date.from(source.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
}
