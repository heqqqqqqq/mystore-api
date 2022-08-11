package com.mystore.controller.admin;

import com.mystore.common.CommonResponse;
import com.mystore.service.OSSService;
import com.mystore.vo.AliOSSCallbackResult;
import com.mystore.vo.AliOSSPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/alioss/")
public class AliOSSController {

    @Autowired
    private OSSService ossService;

    @GetMapping("get_policy")
    public CommonResponse<AliOSSPolicy> getPolicy(){
        return ossService.generatePolicy();
    }

    @PostMapping("callback")
    public CommonResponse<AliOSSCallbackResult> callback(HttpServletRequest request){
        return ossService.callback(request);
    }

}
