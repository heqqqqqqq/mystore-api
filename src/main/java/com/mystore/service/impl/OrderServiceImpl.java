package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.domain.*;
import com.mystore.persistence.*;
import com.mystore.service.AddressService;
import com.mystore.service.OrderService;
import com.mystore.utils.BigDecimalUtil;
import com.mystore.utils.DateTimeFormatUtil;
import com.mystore.utils.ImageServerConfig;
import com.mystore.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private AddressService addressService;
    @Resource
    private ImageServerConfig imageServerConfig;

    //创建订单
    @Override
    public CommonResponse<OrderVO> create(Integer userId,Integer addressId){

        //1.从购物车中获取用户已经选中的商品
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("checked", CONSTANT.CART.CHECKED);
        List<Cart> cartItemList=cartMapper.selectList(queryWrapper);//购物车中已选中的商品合集

        if (CollectionUtils.isEmpty(cartItemList)){
            return CommonResponse.createForError("购物车为空，不能创建订单");
        }

        //2.将购物车中的CartItem填入到OrderItem
        CommonResponse cartItemToOrderItemResult=cartItemToOrderItem(cartItemList);
        if (!cartItemToOrderItemResult.isSuccess()){
            return cartItemToOrderItemResult;
        }

        //3.计算订单总价
        List<OrderItem> orderItemList=(List<OrderItem>)cartItemToOrderItemResult.getData();
        BigDecimal paymentPrice=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            paymentPrice=BigDecimalUtil.add(paymentPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }

        //4.生成订单
        Order order=new Order();
        Long orderNo=generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setPaymentPrice(paymentPrice);
        order.setPaymentType(CONSTANT.PayType.ALIPAY.getCode());//支付宝支付
        order.setPostage(0);//包邮
        order.setStatus(CONSTANT.OrderStatus.UNPAID.getCode());//新建为未付款
        //支付时间、发货时间、交易完成时间和关闭订单时间未来再填入
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        //5.将新生成的订单插入到数据库
        int result=orderMapper.insert(order);
        if (result!=1){
            return CommonResponse.createForError("生成订单失败");
        }

        //6.将订单明细插入到OrderItem表中，批量插入
        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(orderNo);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }

        //7.减少商品库存
        for(OrderItem orderItem:orderItemList){
            Product product=productMapper.selectById(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateById(product);
        }

        //8.清空购物车
        for (Cart cartItem:cartItemList){
            cartMapper.deleteById(cartItem.getId());
        }

        //9.返回前端数据
        OrderVO orderVO=generateOrderVO(order,orderItemList);
        return CommonResponse.createForSuccess(orderVO);
    }

    @Override
    public CommonResponse<OrderCartItemVO> getCheckedCartItemList(Integer userId){
        OrderCartItemVO orderCartItemVO=new OrderCartItemVO();

        //1.从购物车中获取选中的商品
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("checked",CONSTANT.CART.CHECKED);
        List<Cart> cartItemList=cartMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(cartItemList)){
            return CommonResponse.createForError("购物车为空");
        }

        //2.将购物车中的cartItem填入到OrderItem中
        CommonResponse cartItemToOrderItemResult=cartItemToOrderItem(cartItemList);
        if (!cartItemToOrderItemResult.isSuccess()){
            return cartItemToOrderItemResult;
        }

        List<OrderItem> orderItemList=(List<OrderItem>)cartItemToOrderItemResult.getData();

        //3.计算整个订单总价和将OrderItem->OrderItemVO
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        BigDecimal paymentPrice=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            paymentPrice=BigDecimalUtil.add(paymentPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(orderItemToOrderItemVO(orderItem));
        }

        orderCartItemVO.setOrderItemVOList(orderItemVOList);
        orderCartItemVO.setPaymentPrice(paymentPrice);
        orderCartItemVO.setImageServer(imageServerConfig.getUrl());

        return CommonResponse.createForSuccess(orderCartItemVO);
    }

    @Override
    public CommonResponse<OrderVO> getOrderDetail(Integer userId,Long orderNo){
        OrderVO orderVO=new OrderVO();

        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        Order order=orderMapper.selectOne(queryWrapper);
        if (order==null){
            return CommonResponse.createForError("订单不存在");
        }

        QueryWrapper<OrderItem> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("user_id",userId).eq("order_no",orderNo);
        List<OrderItem> orderItemList=orderItemMapper.selectList(queryWrapper1);

        orderVO=generateOrderVO(order,orderItemList);

        return CommonResponse.createForSuccess(orderVO);
    }

    //获取订单列表
    @Override
    public CommonResponse<Page<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize){
        Page<Order> result=new Page<>();
        result.setCurrent(pageNum);//当前页
        result.setSize(pageSize);//每页几个

        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        result=orderMapper.selectPage(result,queryWrapper);

        List<Order> orderList=result.getRecords();
        List<OrderVO> orderVOList=Lists.newArrayList();

        for (Order order:orderList){
            QueryWrapper<OrderItem> queryWrapper1=new QueryWrapper<>();
            queryWrapper1.eq("user_id",userId);
            queryWrapper1.eq("order_no",order.getOrderNo());

            List<OrderItem> orderItemList=orderItemMapper.selectList(queryWrapper1);
            OrderVO orderVO=generateOrderVO(order,orderItemList);
            orderVOList.add(orderVO);
        }

        Page<OrderVO> newResult=new Page<>();
        newResult.setRecords(orderVOList);
        newResult.setSize(pageSize);
        newResult.setCurrent(pageNum);
        newResult.setTotal(result.getTotal());

        return CommonResponse.createForSuccess(newResult);
    }

    //取消订单
    @Override
    public CommonResponse<String> cancel(Integer userId,Long orderNo){
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("order_no",orderNo);
        Order order=orderMapper.selectOne(queryWrapper);

        if (order==null){
            return CommonResponse.createForError("订单不存在");
        } else if (order.getStatus()!=CONSTANT.OrderStatus.UNPAID.getCode()) {
            return CommonResponse.createForError("订单不是未支付状态，不能取消");
        }
        order.setStatus(CONSTANT.OrderStatus.CANCEL.getCode());
        int result=orderMapper.updateById(order);
        if (result!=1){
            return CommonResponse.createForError("取消订单失败");
        }
        return CommonResponse.createForSuccess();
    }

    private CommonResponse<Object> cartItemToOrderItem(List<Cart> cartItemList){
        List<OrderItem> orderItemList= Lists.newArrayList();

        for (Cart cartItem:cartItemList){
            //核查购物车中的商品是否能够生成订单，包括商品的在售状态和库存
            Product product=productMapper.selectById(cartItem.getProductId());
            if(product.getStatus()!=CONSTANT.ProductStatus.ON_SALE.getCode()){
                return CommonResponse.createForError("商品【"+product.getName()+"】不是在售状态");
            }
            if (product.getStock()<cartItem.getQuantity()){
                return CommonResponse.createForError("商品【"+product.getName()+"】库存不足");
            }

            //组装数据cartItem->orderItem
            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(cartItem.getUserId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            orderItem.setQuantity(cartItem.getQuantity());

            orderItemList.add(orderItem);
        }

        return CommonResponse.createForSuccess(orderItemList);
    }

    //生成订单号
    private Long generateOrderNo(){
        return System.currentTimeMillis()+new Random().nextInt(1000);
    }

    private OrderVO generateOrderVO(Order order,List<OrderItem> orderItemList){
        OrderVO orderVO=new OrderVO();

        orderVO.setId(order.getId());
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setUserId(order.getUserId());
        orderVO.setPaymentPrice(order.getPaymentPrice());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());

        orderVO.setPaymentTime(DateTimeFormatUtil.format(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeFormatUtil.format(order.getSendTime()));
        orderVO.setEndTime(DateTimeFormatUtil.format(order.getEndTime()));
        orderVO.setCloseTime(DateTimeFormatUtil.format(order.getCloseTime()));
        orderVO.setCreateTime(DateTimeFormatUtil.format(order.getCreateTime()));
        orderVO.setUpdateTime(DateTimeFormatUtil.format(order.getUpdateTime()));

        AddressVO addressVO=addressService.findById(orderVO.getUserId(), order.getAddressId()).getData();
        orderVO.setAddressVO(addressVO);

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            orderItemVOList.add(orderItemToOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVOList(orderItemVOList);

        orderVO.setImageServer(imageServerConfig.getUrl());

        return orderVO;
    }

    private OrderItemVO orderItemToOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setId(orderItem.getId());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentPrice(orderItem.getCurrentPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVO;
    }

}
