package mainback;


/*import entity.Student;


import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.curator.org.apache.curator.shaded.com.google.common.collect.Lists;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import sink.SinkToMySQL;
import util.GsonUtil ;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import util.Splitter;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;*/

/**
 * Created by CYF
 * on 2019/9/29
 * 高版本kafka
 */
public class Kafka2Mysql {

    /*public static void main(String[] args) throws Exception{
        // ExecutionEnvironment
        wordCountMethod();

    }

    *//**
     * 本地读取计数 wordCount
     * @throws Exception
     *//*
    public static void wordCountMethod() throws Exception{

        final ExecutionEnvironment env2 = ExecutionEnvironment.getExecutionEnvironment();
        //env2.readTextFile("E:/word.txt");
        //创建流执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        //读取文本文件中的数据
        DataStreamSource<String> streamSource = env.readTextFile("E:/word.txt");

        //进行逻辑计算
        SingleOutputStreamOperator<Tuple2<String, Integer>> dataStream = streamSource
                .flatMap(new Splitter())
                .keyBy(0)
                .sum(1);
        dataStream.print();
        //设置程序名称
        env.execute("Window WordCount");


    }


    *//**
     * 流处理环境方法
     * StreamExecutionEnvironment.getExecutionEnvironment
     * @throws Exception
     *//*
    public static void streamMethod() throws Exception{

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.30.39:9092");
        props.put("zookeeper.connect", "192.168.30.39:2181");
        props.put("group.id", "metric-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");

        //这个 kafka topic 需要和生产者的 topic 一致
        SingleOutputStreamOperator<Student> student = env.addSource(new FlinkKafkaConsumer011<>(
                "student",
                new SimpleStringSchema(),
                props)).setParallelism(1)
                .map(string -> GsonUtil.fromJson(string, Student.class));

        student.timeWindowAll(Time.minutes(1)).apply(new AllWindowFunction<Student, List<Student>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<Student> values, Collector<List<Student>> out) throws Exception {
                ArrayList<Student> students = Lists.newArrayList(values);
                if (students.size() > 0) {
                    System.out.println("1 分钟内收集到 student 的数据条数是：" + students.size());
                    out.collect(students);
                }
            }
        }).addSink(new SinkToMySQL());

        env.execute("flink learning connectors kafka");
    }*/

}
