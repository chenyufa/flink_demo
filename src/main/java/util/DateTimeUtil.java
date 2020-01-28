package util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ Date 2019/12/23 12:10
 * @ Created by CYF
 * @ Description 时间工具
 */

public class DateTimeUtil {

    public static String stampToDate(String time,String format){
        String res = "";
        if(format == null || "".equals(format)){
            format = "yyyyMMdd";
        }
        if(time != null && !"".equals(time)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            long lt = new Long(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        }
        return res;
    }


}
