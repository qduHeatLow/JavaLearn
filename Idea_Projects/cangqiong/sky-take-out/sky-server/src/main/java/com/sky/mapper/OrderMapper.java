package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单信息
     * @param orders
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into orders (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, estimated_delivery_time, delivery_status, pack_amount, tableware_number, tableware_status) VALUES " +
            "(#{number},#{status},#{userId},#{addressBookId},#{orderTime},#{checkoutTime},#{payMethod},#{payStatus},#{amount},#{remark},#{phone},#{address},#{userName},#{consignee},#{estimatedDeliveryTime},#{deliveryStatus},#{packAmount},#{tablewareNumber},#{tablewareStatus})")
    void insert(Orders orders);

    /**
     * 根据分页查询DTO 分页查询订单
     * @param orders
     * @return
     */
    //SELECT o.*,od.* FROM orders o left  JOIN order_detail od on o.id = od.order_id where estimated_delivery_time BETWEEN '2024-10-23 'AND'2024-10-24'
    Page<Orders> page(OrdersPageQueryDTO orders);

    /**
     * 根据Id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 更新订单 可用于更新status
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询confirmed3、deliveryInProgress4、toBeConfirmed2状态的订单数量
     * @return
     */
    @Select("SELECT" +
            "  COUNT(CASE WHEN status = 3 THEN 1 END) AS confirmed," +
            "  COUNT(CASE WHEN status = 4 THEN 1 END) AS deliveryInProgress," +
            "  COUNT(CASE WHEN status = 2 THEN 1 END) AS toBeConfirmed\n" +
            "FROM orders;")
    OrderStatisticsVO getStatistics();

    /**
     * 查询待支付、且下单时间超过15分钟的订单
     * @param pendingPayment
     * @param nowMinus15Minutes
     * @return
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time < #{nowMinus15Minutes}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime nowMinus15Minutes);

    /**
     * 根据订单号和用户Id查询订单
     * @param orderNumber
     * @param userId
     * @return
     */
    @Select("select * from orders where user_id = #{userId} and number = #{orderNumber}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 根据时间和状态查找营业额
     * @param map
     * @return
     */
    Double sumTurnoverByMap(Map map);

    /**
     * 根据时间和状态查订单数
     * @param map
     * @return
     */
    Integer getOrderCount(Map map);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
