package me.dusanov.etl.workshift.etljobapp.model.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;
import java.util.TimeZone;

//Dont use this :)
@Deprecated
@Converter
public class TimestampConverter implements AttributeConverter<Long, Date> {
    @Override
    public Date convertToDatabaseColumn(Long attribute) {
        return new Date(attribute + TimeZone.getTimeZone("EST").getRawOffset());
    }

    @Override
    public Long convertToEntityAttribute(Date dbData) {
        return dbData.toInstant().getEpochSecond() * 1000 - TimeZone.getTimeZone("EST").getRawOffset();
    }
}
