package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.vo.QRCodeVO;

import java.util.Map;

public interface MyAlipayService {
    CommonResponse<QRCodeVO> getQRCode(Integer userId, Long orderNo);
    CommonResponse<Object> alipayCallback(Map<String,String> params);
    CommonResponse<Object> getPayStatus(Integer userId,Long orderNo);
}
