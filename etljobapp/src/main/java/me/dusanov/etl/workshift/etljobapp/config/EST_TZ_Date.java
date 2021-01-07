package me.dusanov.etl.workshift.etljobapp.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EST_TZ_Date {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static {sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));}

    private final Date date;

    public EST_TZ_Date(String date) throws ParseException {
        this.date = sdf.parse(date);
    }

    public EST_TZ_Date(Long timestamp){
        this.date = new Date(timestamp);
    }

    @Override
    public String toString() {
        return sdf.format(date);
    }

    public long getTime() {
        return this.date.getTime();
    }
}
