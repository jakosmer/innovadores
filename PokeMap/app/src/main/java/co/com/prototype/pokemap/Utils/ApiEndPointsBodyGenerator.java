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
public class ApiEndPointsBodyGenerator{

    public static final String KEY_AUTH_CODE = "auth_code";
    public static final String KEY_AUTH_DATE = "auth_date";
    private HashMap<String, Object> map = null;

    private ApiEndPointsBodyGenerator(){
        map = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));

        map.put(KEY_AUTH_DATE, c.get(Calendar.HOUR_OF_DAY));
    }

    public static ApiEndPointsBodyGenerator builder(){
        return new ApiEndPointsBodyGenerator();
    }

    public ApiEndPointsBodyGenerator addAuthCode(String authCode){

        map.put(KEY_AUTH_CODE, authCode);

        return this;
    }

    public HashMap<String, Object> build(){
        return map;
    }

}
