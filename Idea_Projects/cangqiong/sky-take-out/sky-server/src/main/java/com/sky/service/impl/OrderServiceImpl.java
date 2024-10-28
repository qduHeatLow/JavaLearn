package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * 提交订单
     * @param ordersDTO
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO submit(OrdersSubmitDTO ordersDTO) {
//        1. 处理各种业务异常
        //1.1 地址簿中当前地址信息是否为空
        Long addressBookId = ordersDTO.getAddressBookId();
        AddressBook address = addressBookMapper.getById(addressBookId);
        if(address == null){
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //1.2 购物车是否有数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .id(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

//        2. 向订单表插入 1 条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        //订单号使用当前系统时间戳
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(userId);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setUserName(userMapper.getUserNameByUserId(userId));

        orders.setPhone(address.getPhone());
        orders.setAddress(address.getProvinceName()+address.getCityName()+address.getDistrictName()+address.getDetail());
        orders.setConsignee(address.getConsignee());

        orderMapper.insert(orders);

//        3. 向订单明细表插入 n 条数据 通过购物车封装数据
        List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
        shoppingCartList.forEach(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });
        orderDetailMapper.insertBatch(orderDetailList);

//        4. 清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);
//        5. 封装 VO 给前端
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

        return orderSubmitVO;
    }

    /**
     * 历史订单查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult page(int pageNum, int pageSize, Integer status) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        PageHelper.startPage(pageNum,pageSize);
        Page<Orders> page = orderMapper.page(ordersPageQueryDTO);
        //将分页查询的订单结果封装到分页VO（即List）中
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders order : page) {
            //将分页查询的订单结果封装到各个VO中
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);

            Long orderId = order.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }
        return new PageResult(page.getTotal(),orderVOList);
    }

    /**
     * 查看订单详情
     * @param id
     * @return OrderVO 包含订单明细的订单
     */
    @Override
    public OrderVO getDeatil(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancel(Long id) {
        //status = 6 是已取消
        Orders orders = orderMapper.getById(id);
        orders.setStatus(6);
        orderMapper.update(orders);
    }

    /**
     * 再来一单 再来一单就是将原订单中的商品重新加入到购物车中
     * @param id
     */
    @Override
    public void repetition(Long id) {
        //查询历史订单
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //新购物车
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orderDetailList
     * @return
     */
    private String getOrderDishesStr(List<OrderDetail> orderDetailList) {

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult adminConditionSearchPage(OrdersPageQueryDTO ordersPageQueryDTO) {
        //查询订单
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = orderMapper.page(ordersPageQueryDTO);

        //将订单Orders的Page封装为OrderVOList（为其中加入订单详情信息OrderDetail）
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : ordersPage) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            //为其中加入订单详情信息OrderDetail
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            String orderDishes = getOrderDishesStr(orderDetailList);
            orderVO.setOrderDishes(orderDishes);

            orderVOList.add(orderVO);
        }

        return new PageResult(ordersPage.getTotal(),orderVOList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.getStatistics();
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        if(orders.getStatus() != Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param rejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO rejectionDTO) {
        Orders orders = orderMapper.getById(rejectionDTO.getId());
        if(orders.getStatus()!=Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(rejectionDTO.getRejectionReason());
        orderMapper.update(orders);
    }

    /**
     * 管理端取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        if(orders.getStatus()!=Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders.getStatus()!=Orders.CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders.getStatus()!=Orders.DELIVERY_IN_PROGRESS){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    /**
     * 付款
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        PaySuccess(ordersPaymentDTO.getOrderNumber());
        return null;
    }

    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        //来单提醒
        Map map = new HashMap();
        map.put("type",2); //1表示来单提醒 2表示客户催单
        map.put("orderId",id);
        map.put("content","订单号："+orders.getNumber());
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 支付成功处理业务 修改订单状态 来单提醒
     * @param orderNumber
     */
    public void PaySuccess(String orderNumber){
        //更新订单状态
        Long uersId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByNumberAndUserId(orderNumber,uersId);
        orders.setStatus(Orders.PAID);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(orders);

        //来单提醒
        Map map = new HashMap();
        map.put("type",1); //1表示来单提醒 2表示客户催单
        map.put("orderId",orders.getId());
        map.put("content","订单号："+orderNumber);
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }
}
