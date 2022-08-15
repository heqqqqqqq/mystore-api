package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.vo.CartVO;

public interface CartService {
    CommonResponse<CartVO> addCart(Integer userId, Integer productId, Integer quantity);
    CommonResponse<CartVO> updateCart(Integer userId,Integer productId,Integer quantity);
    CommonResponse<CartVO> deleteCart(Integer userId,String productIds);
    CommonResponse<CartVO> getCartList(Integer userId);
    CommonResponse<CartVO> updateAllCheckStatus(Integer userId,Integer checkStatus);
    CommonResponse<CartVO> updateCheckStatus(Integer userId,Integer productId,Integer checkStatus);
    CommonResponse<Integer> getCartCount(Integer userId);
}
