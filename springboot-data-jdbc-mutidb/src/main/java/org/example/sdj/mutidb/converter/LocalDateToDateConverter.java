package org.example.sdj.mutidb.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@ReadingConverter
public class LocalDateToDateConverter implements Converter<LocalDate, Date> {


    @Override
    public Date convert(LocalDate source) {

        return source != null ? Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
}
