package sink;

import entity.OrderInfo;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import util.HbaseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Date 2019/12/26 13:47
 * @ Created by CYF
 * @ Description
 */
public class SinkToHbase extends RichSinkFunction<OrderInfo> {

    private static final Logger logger=Logger.getLogger(SinkToHbase.class);

    private Connection connection=null;
    private Table table=null;
    private ArrayList<Put> putArrayList;
    private BufferedMutatorParams params;
    private BufferedMutator mutator;

    private String cf="c";
    private String tableName="cdb_hunter_order_fk";
    private String bootList;

    public SinkToHbase(){

    }
    public SinkToHbase(String bootList){
        this.bootList=bootList;
    }

    //  只调用一次
    @Override
    public void open(Configuration configuration) throws Exception {
        logger.debug("********call open()*********");
        super.open(configuration);
        HbaseUtil.setConfig(bootList);
        connection=HbaseUtil.connection;
        params=new BufferedMutatorParams(TableName.valueOf(tableName));
        params.writeBufferSize(1024*1024);
        mutator=connection.getBufferedMutator(params);
        putArrayList=new ArrayList<>();
    }

    //  每条消息都调用<i>invoke</i>
    @Override
    public void invoke(OrderInfo event,Context ctx) throws IOException {

        Put put=new Put(Bytes.toBytes(String.valueOf(event.orderId)));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("orderId"),Bytes.toBytes(event.orderId));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("payBid"),Bytes.toBytes(event.payBid));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("serviceBid"),Bytes.toBytes(event.serviceBid));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("price"),Bytes.toBytes(event.price));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("payMoney"),Bytes.toBytes(event.payMoney));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("groupId"),Bytes.toBytes(event.groupId));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("orderStatus"),Bytes.toBytes(event.orderStatus));
        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("orderTime"),Bytes.toBytes(event.orderTime));
        putArrayList.add(put);

        mutator.mutate(putArrayList);
        if(putArrayList.size() >= 50){
            logger.info(" flush data to HBase ...");
            mutator.flush();
            putArrayList.clear();
        }
    }

    @Override
    public void close() throws Exception {
        logger.debug("******call close()*********");
        super.close();
        if(table!=null){
            table.close();
        }
        if(connection!=null){
            connection.close();
        }
    }
}
