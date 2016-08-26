package co.com.prototype.pokemap.Utils;

import java.util.HashMap;

/**
 * Created by carlviar on 2016/08/26.
 */
public class ApiEndPointsBodyGenerator {

    public static final String KEY_AUTH_CODE = "auth_code";

    public static HashMap<String, String> getBodyForRefresh(String authCode){
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_AUTH_CODE, authCode);

        return map;
    }

}
