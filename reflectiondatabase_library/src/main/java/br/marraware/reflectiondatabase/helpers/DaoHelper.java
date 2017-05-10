package br.marraware.reflectiondatabase.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by joao_gabriel on 04/05/17.
 */

public class DaoHelper {

    public static final String DATE_FORMATE_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat DATE_FORMATE = new SimpleDateFormat(DATE_FORMATE_STRING, Locale.getDefault());

    public static String dateToString(Date date) {
        if(date == null)
            return null;
        return DATE_FORMATE.format(date);
    }
    public static Date stringToDate(String dataStr){
        if(dataStr == null)
            return null;
        try {
            return DATE_FORMATE.parse(dataStr);
        }catch (ParseException e){}
        return null;
    }
}
