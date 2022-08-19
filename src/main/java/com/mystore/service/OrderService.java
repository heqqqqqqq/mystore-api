package com.mystore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Cart;
import com.mystore.vo.OrderCartItemVO;
import com.mystore.vo.OrderVO;

import java.util.List;

public interface OrderService {
    CommonResponse<OrderVO> create(Integer userId, Integer addressId);
    CommonResponse<OrderCartItemVO> getCheckedCartItemList(Integer userId);
    CommonResponse<OrderVO> getOrderDetail(Integer userId,Long orderNo);
    CommonResponse<Page<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize);
    CommonResponse<String> cancel(Integer userId,Long orderNo);
}
