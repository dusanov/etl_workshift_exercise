package me.dusanov.etl.workshift.etljobapp.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@ReadingConverter
public class StringToESTDate implements Converter<byte[], EST_TZ_Date> {

    private static final Logger log = LoggerFactory.getLogger(StringToESTDate.class);

    @Override
    public EST_TZ_Date convert(byte @NotNull []attribute) {
        EST_TZ_Date date = null;
        try {
            date = new EST_TZ_Date(new String(attribute));
        } catch (ParseException e) {
            log.error(String.format("Error converting string %s to date. Error msg:\n%s\n",new String(attribute),e.getMessage()));
        }
        return date;
    }
}
