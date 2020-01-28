package util;

import com.google.gson.Gson;

import java.nio.charset.Charset;

/**
 * Created by CYF
 * on 2019/9/29.
 */
public class GsonUtil {

    private final static Gson gson = new Gson();

    public static <T> T fromJson(String value, Class<T> type) {
        return gson.fromJson(value, type);
    }

    public static <T> T fromJson(Object value, Class<T> type) {
        String vaStr="{}";
        if(value != null){
            vaStr = value.toString();
        }
        return gson.fromJson(vaStr, type);
    }

    public static String toJson(Object value) {
        return gson.toJson(value);
    }

    public static byte[] toJSONBytes(Object value) {
        return gson.toJson(value).getBytes(Charset.forName("UTF-8"));
    }
}
