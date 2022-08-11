package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.vo.AliOSSCallbackResult;
import com.mystore.vo.AliOSSPolicy;

import javax.servlet.http.HttpServletRequest;

public interface OSSService {

    public CommonResponse<AliOSSPolicy> generatePolicy();

    public CommonResponse<AliOSSCallbackResult> callback(HttpServletRequest request);

}
