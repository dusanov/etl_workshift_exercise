package me.dusanov.etl.workshift.etljobapp.config;

import me.dusanov.etl.workshift.etljobapp.model.EST_TZ_Date;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ESTDateToStringRedisConverter implements Converter<EST_TZ_Date, byte[]> {

    @Override
    public byte[] convert(@NotNull EST_TZ_Date dbData) {
        return dbData.toString().getBytes();
    }

}
