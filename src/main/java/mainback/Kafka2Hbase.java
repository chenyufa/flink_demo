package mainback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import entity.OrderInfo;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;
import sink.SinkToHbase;

import java.util.Properties;

/**
 * @ Date 2019/12/25 15:03
 * @ Created by CYF
 * @ Description
 */
public class Kafka2Hbase {


    public static void main(String[] args) {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.1.183:24000,192.168.1.184:24000,192.168.1.185:24000,192.168.1.186:24000,192.168.1.187:24000,192.168.1.188:24000,192.168.1.189:24000");
        properties.put("zookeeper.connect", "192.168.1.199:41810,192.168.1.200:41810,192.168.1.201:41810,192.168.1.202:41810,192.168.1.203:41810/kafka");
        properties.put("group.id", "yewu-data-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("auto.offset.reset", "latest");

        /**
         * 定义source
         */
        DataStream<String> source = env.addSource(new FlinkKafkaConsumer08<>("yewu_message_queues",new SimpleStringSchema(),properties));

        /**
         * 对数据进行过滤
         */
        DataStream<String> filterSource = source.filter(dataStr -> {
            boolean flag = true;
            String[] dataArray = dataStr.split("&");
            if(dataArray.length <=2 || !"action=lieyouOrder".equals(dataArray[0])){
                flag = false;
            }else{
                String jsonStr = dataArray[2];
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                Object obj = jsonObject.get("orderId");
                if(obj == null){
                    flag = false;
                }
            }
            return flag;
        });

        /**
         * 对数据进行转换
         * String -> OrderInfo
         */
        SingleOutputStreamOperator<OrderInfo> dataStreamSource = filterSource.map(string -> {
            String jsonDate = "{}";
            String[] dataArray = string.split("&");
            if(dataArray.length >=3){
                jsonDate = dataArray[2];
            }
            return  JSON.parseObject(jsonDate, OrderInfo.class);
        });


        /**
         * sink 到 Hbase
         */
        String bootList = "192.168.1.199,192.168.1.200,192.168.1.201,192.168.1.202,192.168.1.203";
        dataStreamSource.addSink(new SinkToHbase(bootList)).name("HBse sink");

        /**
         * 执行
         */
        try {
            env.execute("execute app order data to Hbase Demo");
        } catch (Exception e) {
            e.printStackTrace();
        }




    }


}
