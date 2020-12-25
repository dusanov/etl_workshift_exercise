package me.dusanov.etl.workshift.etljobapp.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ESTDateToString implements Converter<EST_TZ_Date, byte[]> {

    @Override
    public byte[] convert(@NotNull EST_TZ_Date dbData) {
        return dbData.toString().getBytes();
    }

}
