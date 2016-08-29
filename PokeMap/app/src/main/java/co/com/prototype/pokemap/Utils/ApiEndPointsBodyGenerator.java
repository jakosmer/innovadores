package co.com.prototype.pokemap.Utils;

import android.os.SystemClock;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by carlviar on 2016/08/26.
 */
public class ApiEndPointsBodyGenerator {

    public static final String KEY_AUTH_CODE = "auth_code";
    public static final String KEY_AUTH_DATE = "auth_date";

    private static HashMap<String, String> init(){
        HashMap<String, String> map = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));

        map.put(KEY_AUTH_DATE, String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
        return map;
    }

    public static HashMap<String, String> getBodyForRefresh(String authCode){
        HashMap<String, String> map = init();
        map.put(KEY_AUTH_CODE, authCode);

        return map;
    }

}
