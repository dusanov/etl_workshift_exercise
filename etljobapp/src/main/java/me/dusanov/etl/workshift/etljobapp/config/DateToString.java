package me.dusanov.etl.workshift.etljobapp.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
@WritingConverter
public class DateToString implements Converter<Date, /*String*/byte[]> {

    @Override
    public /*String*/byte[] convert(Date dbData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return sdf.format(dbData).getBytes();
    }

}
