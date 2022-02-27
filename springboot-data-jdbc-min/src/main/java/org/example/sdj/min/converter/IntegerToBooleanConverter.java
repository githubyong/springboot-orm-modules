package org.example.sdj.min.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

  @Override
  public Boolean convert(Integer source) {
    return "1".equals(source+"");
  }
}