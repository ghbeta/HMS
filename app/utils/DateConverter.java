package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hao on 2016/1/2.
 */
public class DateConverter {
    public static Date fromString(String datepicker) throws ParseException {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        return df.parse(datepicker);

    }
}
