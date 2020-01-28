package mainback;

import com.alibaba.fastjson.JSONObject;

/**
 * @ Date 2019/12/23 17:07
 * @ Created by CYF
 * @ Description TODO
 */
public class TestMain {

    public static void main(String[] args) {
        String dataStr = "{\"aplan_et\":\"cus\",\"login_result\":\"1\",\"aplan_ch\":\"\",\"aplan_uuid\":\"0-2246792532\",\"aplan_province\":\"广东\",\"aplan_country\":\"中国\",\"versionCode\":\"198\",\"aplan_ak\":\"68c701aeaed73a4ad8a995dab7b35f659db3\",\"ap_uuid\":\"0-2246792532\",\"appPackage\":\"lieyou\",\"aplan_ts\":\"1577091324000\",\"login_time\":\"786\",\"aplan_fa\":\"0\",\"aplan_hour\":\"16\",\"bid\":\"10012669537\",\"ap_ts\":\"1577091324\",\"aplan_plat\":\"ios\",\"aplan_eid\":\"aplan_im_login\",\"aplan_appver\":\"3.2.3\",\"aplan_sdkv\":\"1\",\"appver\":\"3.2.3\",\"aplan_city\":\"广州\",\"message\":\"已连接\",\"aplan_sid\":\"2E269AA2-E62B-45E9-953E-84B3B662628F\",\"aplan_ua\":\"unknown\",\"aplan_uid\":\"8F0AFF3E-BF1A-42C2-AC92-149B86807E92\",\"aplan_ip\":\"14.18.236.76\"}";
        boolean flag = true;
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        Object obj = jsonObject.get("aplan_login");
        if(obj == null){
            flag = false;
        }else if(!"1".equals(obj.toString().trim())){
            flag = false;
        }
        System.out.println(flag);
    }

}
