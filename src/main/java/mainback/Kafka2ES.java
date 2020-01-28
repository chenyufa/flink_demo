package mainback;

import com.alibaba.fastjson.JSONObject;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import util.DateTimeUtil;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @ Date 2019/12/20 17:07
 * @ Created by CYF
 * @ Description
 */
public class Kafka2ES {

    public static void main(String[] args) {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.1.183:24000,192.168.1.184:24000,192.168.1.185:24000,192.168.1.186:24000,192.168.1.187:24000,192.168.1.188:24000,192.168.1.189:24000");
        properties.put("zookeeper.connect", "192.168.1.199:41810,192.168.1.200:41810,192.168.1.201:41810,192.168.1.202:41810,192.168.1.203:41810/kafka");
        properties.put("group.id", "es-data-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("auto.offset.reset", "latest");

        /**
         * 定义source
         */
        DataStream<String> source = env.addSource(new FlinkKafkaConsumer08<>("nginx_log_aplan_backend",new SimpleStringSchema(),properties));

        /**
         * 对数据进行过滤
         */
        DataStream<String> filterSource = source.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String dataStr) throws Exception {
                boolean flag = true;
                JSONObject jsonObject = JSONObject.parseObject(dataStr);
                Object obj = jsonObject.get("aplan_login");
                if(obj == null){
                    flag = false;
                }else if(!"1".equals(obj.toString().trim())){
                    flag = false;
                }
                return flag;

            }
        });

        //lambdas表达式
        /*SingleOutputStreamOperator<String> filterSource = source.filter(dataStr -> {
            boolean flag = true;
            JSONObject jsonObject = JSONObject.parseObject(dataStr);
            Object obj = jsonObject.get("aplan_login");
            if(obj == null){
                flag = false;
            }else if(!"1".equals(obj.toString().trim())){
                flag = false;
            }
            return flag;
        });*/



        /**
         * 创建一个ElasticSearchSink对象
         */
        Map<String, String> config = new HashMap<>();
        config.put("cluster.name", "es");
        //该配置表示批量写入ES时的记录条数
        config.put("bulk.flush.max.actions", "100");

        List<InetSocketAddress> transportAddresses = new ArrayList<>();
        try {
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.191"), 9300));
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.193"), 9300));
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.198"), 9300));
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.191"), 9302));
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.193"), 9302));
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("192.168.1.198"), 9302));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        /*入每小时一条记录的索引*/
        filterSource.addSink(new ElasticsearchSink<>(config, transportAddresses, new ElasticsearchSinkFunction<String>() {
            public IndexRequest createIndexRequest(String dataStr) {
                JSONObject jsonObject = JSONObject.parseObject(dataStr);

                //将需要写入ES的字段依次添加到Map当中
                Map<String, String> json = new HashMap<>();

                String appKey = jsonObject.getString("aplan_ak");
                String bid = jsonObject.getString("bid");
                String aplan_ts = jsonObject.getString("aplan_ts");

                //设置记录ID appKey截取+bid+yyyyMMddHH
                StringBuffer aplan_uuid = new StringBuffer("");
                if(appKey != null && appKey.length() >=5){
                    aplan_uuid.append(appKey.substring(0,5));
                }
                if(bid != null){
                    aplan_uuid.append("_").append(bid);
                }
                String dtHour = DateTimeUtil.stampToDate(aplan_ts,"yyyyMMddHH");
                aplan_uuid.append("_").append(dtHour);

                //eid
                json.put("aplan_eid", "aplan_login");
                //用户信息
                json.put("bid", jsonObject.getString("bid"));
                json.put("isHunter", jsonObject.getString("isHunter"));
                json.put("gender", jsonObject.getString("gender"));
                json.put("nickname", jsonObject.getString("nickname"));
                json.put("createTime", jsonObject.getString("createTime"));
                //设备及包信息
                json.put("aplan_ak", appKey);
                json.put("aplan_plat", jsonObject.getString("aplan_plat"));
                json.put("aplan_ch", jsonObject.getString("aplan_ch"));
                json.put("versionCode", jsonObject.getString("versionCode"));
                //IP地理位置
                json.put("aplan_ip", jsonObject.getString("aplan_ip"));
                json.put("aplan_country", jsonObject.getString("aplan_country"));
                json.put("aplan_province", jsonObject.getString("aplan_province"));
                json.put("aplan_city", jsonObject.getString("aplan_city"));
                //时间
                json.put("aplan_ts", aplan_ts);
                json.put("aplan_hour", jsonObject.getString("aplan_hour"));
                json.put("aplan_uuid", aplan_uuid.toString());

                return Requests.indexRequest()
                        .index("dynamic-app-login-hour")
                        .type("event")
                        .id(aplan_uuid.toString())
                        .source(json);
            }


            @Override
            public void process(String dataStr, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(dataStr));
            }
        }));

        /*入每天一条记录的索引*/
        filterSource.addSink(new ElasticsearchSink<>(config, transportAddresses, new ElasticsearchSinkFunction<String>() {
            public IndexRequest createIndexRequest(String dataStr) {
                JSONObject jsonObject = JSONObject.parseObject(dataStr);

                //将需要写入ES的字段依次添加到Map当中
                Map<String, String> json = new HashMap<>();

                String appKey = jsonObject.getString("aplan_ak");
                String bid = jsonObject.getString("bid");
                String aplan_ts = jsonObject.getString("aplan_ts");

                //设置记录ID appKey截取+bid+yyyyMMdd
                StringBuffer aplan_uuid = new StringBuffer("");
                if(appKey != null && appKey.length() >=5){
                    aplan_uuid.append(appKey.substring(0,5));
                }
                if(bid != null){
                    aplan_uuid.append("_").append(bid);
                }
                String dt = DateTimeUtil.stampToDate(aplan_ts,"yyyyMMdd");
                aplan_uuid.append("_").append(dt);

                //eid
                json.put("aplan_eid", "aplan_login");
                //用户信息
                json.put("bid", jsonObject.getString("bid"));
                json.put("isHunter", jsonObject.getString("isHunter"));
                json.put("gender", jsonObject.getString("gender"));
                json.put("nickname", jsonObject.getString("nickname"));
                json.put("createTime", jsonObject.getString("createTime"));
                //设备及包信息
                json.put("aplan_ak", appKey);
                json.put("aplan_plat", jsonObject.getString("aplan_plat"));
                json.put("aplan_ch", jsonObject.getString("aplan_ch"));
                json.put("versionCode", jsonObject.getString("versionCode"));
                //IP地理位置
                json.put("aplan_ip", jsonObject.getString("aplan_ip"));
                json.put("aplan_country", jsonObject.getString("aplan_country"));
                json.put("aplan_province", jsonObject.getString("aplan_province"));
                json.put("aplan_city", jsonObject.getString("aplan_city"));
                //时间
                json.put("aplan_ts", aplan_ts);
                json.put("aplan_hour", jsonObject.getString("aplan_hour"));
                json.put("aplan_uuid", aplan_uuid.toString());

                return Requests.indexRequest()
                        .index("dynamic-app-login-day")
                        .type("event")
                        .id(aplan_uuid.toString())
                        .source(json);
            }


            @Override
            public void process(String dataStr, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(dataStr));
            }

        }));


        /**
         * 执行
         */
        try {
            env.execute("execute app login data to Elasticsearch Demo");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
