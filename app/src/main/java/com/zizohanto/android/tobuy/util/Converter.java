package com.zizohanto.android.tobuy.util;

import androidx.databinding.InverseMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {
    @InverseMethod("toDouble")
    public static String doubleToString(Double value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static Double toDouble(String value) {
        if (value.equals("")) return 0.0;
        return Double.valueOf(value);
    }

    @InverseMethod("toDate")
    public static String dateToString(Date value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static Date toDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = format.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @InverseMethod("toInteger")
    public static String integerToString(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static Integer toInteger(String value) {
        if (value.equals("")) return 0;
        return Integer.valueOf(value);
    }
}
