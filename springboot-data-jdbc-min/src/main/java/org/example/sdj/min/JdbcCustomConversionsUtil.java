package org.example.sdj.min;


import org.example.sdj.min.converter.IntegerToBooleanConverter;
import org.example.sdj.min.converter.LocalDateTimeToDateConverter;
import org.example.sdj.min.converter.LocalDateToDateConverter;
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
