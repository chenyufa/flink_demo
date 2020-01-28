package entity;

/**
 * @ Date 2019/12/26 13:51
 * @ Created by CYF
 * @ Description 订单信息实体
 */
public class OrderInfo {

    public int orderId;
    public long payBid;
    public long serviceBid;
    public int price;
    public int payMoney;
    public int groupId;
    public String orderStatus;
    public int orderTime;

    public long ranking=0;

    public static OrderInfo of(int orderId,long payBid,long serviceBid,
                               int price,int payMoney,int groupId,
                               String orderStatus,int orderTime){
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.orderId = orderId;
        orderInfo.payBid = payBid;
        orderInfo.serviceBid = serviceBid;
        orderInfo.price = price;
        orderInfo.payMoney = payMoney;
        orderInfo.groupId = groupId;
        orderInfo.orderStatus = orderStatus;
        orderInfo.orderTime = orderTime;
        return orderInfo;
    }

}
