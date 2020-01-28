package util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @ Date 2019/12/26 13:58
 * @ Created by CYF
 * @ Description
 */
public class HbaseUtil {

    public static Configuration config=null;
    public static Connection connection=null;
    public static Admin admin=null;
    public static String tableName="table_name";

    private HbaseUtil(){}

    public static void setConfig(String bootLists){
        try {
            config= HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum",bootLists);
            config.set("hbase.zookeeper.property.clientPort","21810");
            config.set("hbase.client.pause","2000");
            config.set("hbase.client.retries.number","20");
            connection= ConnectionFactory.createConnection(config);
            admin=connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        try {
            if(connection!=null){
                connection.close();
            }
            if(admin!=null){
                admin.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createHTable(){
        TableName tName=TableName.valueOf(tableName);

        try {
            if(!admin.tableExists(tName)){
                HTableDescriptor table = new HTableDescriptor(tName);
                HColumnDescriptor cf = new HColumnDescriptor("c");
                table.addFamily(cf);

                //HBase预分区，按月分区
                byte[][] splitKey={
                        Bytes.toBytes("2019-09"),
                        Bytes.toBytes("2019-10")
                };

                admin.createTable(table);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
