package org.example.sdj.mutidb;


import org.example.sdj.mutidb.converter.IntegerToBooleanConverter;
import org.example.sdj.mutidb.converter.LocalDateTimeToDateConverter;
import org.example.sdj.mutidb.converter.LocalDateToDateConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.util.ArrayList;
import java.util.List;

public class JdbcCustomConversionsUtil {

    public static JdbcCustomConversions jdbcCustomConversions() {
        List<Converter> converters = new ArrayList<>();
        converters.add(new IntegerToBooleanConverter());
        converters.add(new LocalDateToDateConverter());
        converters.add(new LocalDateTimeToDateConverter());
        JdbcCustomConversions conversions =  new JdbcCustomConversions(converters);
        return conversions;
    }
}
