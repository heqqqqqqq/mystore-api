package com.mystore.controller.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.User;
import com.mystore.service.MyAlipayService;
import com.mystore.vo.QRCodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/pay/")
@Slf4j
public class AlipayController {

    @Autowired
    private MyAlipayService myAlipayService;

    //获取二维码
    @GetMapping("get_qrcode")
    public CommonResponse<QRCodeVO> getQRCode(
            @RequestParam @NotNull(message = "订单编号不能为空") Long orderNo,
            HttpSession session
    ){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return myAlipayService.getQRCode(loginUser.getId(),orderNo);
    }

    //支付宝发出请求后的回调
    @PostMapping("callback")
    public Object callback(HttpServletRequest request){

        Map<String,String[]> requestParams=request.getParameterMap();
        Map<String,String> params= Maps.newHashMap();

        //Map<String,String[]> -> Map<String,String>
        for (String paramName:requestParams.keySet()){
            String[] values=requestParams.get(paramName);
            String paramValue="";

            for (int i=0;i<values.length;i++){
                paramValue=(i< values.length-1)?(values[i]+","):(paramValue+values[i]);
            }
            params.put(paramName,paramValue);
        }

        log.info("alipay回调......\n" +
                "trade_no:{}\n" +
                "out_trade_no:{}" +
                "trade_status:{}",
                params.get("trade_no"),
                params.get("out_trade_no"),
                params.get("trade_status"));

        //验签，确保参数由支付宝发送，防止他人调用该接口
        params.remove("sign_type");//官方文档中表明不需要验证sign和sign_type两个参数
        try {
            boolean alipayRSACheck = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheck){
                return CommonResponse.createForError("非法请求");
            }
        }catch (AlipayApiException e){
            log.error("alipay验签异常.......",e);
        }

        //业务逻辑
        CommonResponse result=myAlipayService.alipayCallback(params);
        if (result.isSuccess()){
            return CONSTANT.AlipayCallbackResponse.RESPONSE_SUCCESS;
        }
        return CONSTANT.AlipayCallbackResponse.RESPONSE_FAILED;
    }

    @GetMapping("get_pay_status")
    public CommonResponse<Object> getPayStatus(
            @RequestParam @NotNull(message = "订单编号不能为空") Long orderNo,
            HttpSession session
    ){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return myAlipayService.getPayStatus(loginUser.getId(),orderNo);
    }
}
