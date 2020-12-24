package me.dusanov.etl.workshift.etljobapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

@Component
@ReadingConverter
public class StringToDate implements Converter</*String*/byte[], Date> {

    private static final Logger log = LoggerFactory.getLogger(StringToDate.class);

    @Override
    public Date convert(/*String*/byte[] attribute) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date date = null;
        String stringDate = "";
        try {
            stringDate = Arrays.toString(attribute);
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            log.error(String.format("Error converting string %s to date. Error msg:\n%s\n",stringDate,e.getMessage()));
        }
        return date;
    }

}
