package com.mystore.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Order;
import com.mystore.domain.OrderItem;
import com.mystore.domain.PayInfo;
import com.mystore.persistence.OrderItemMapper;
import com.mystore.persistence.OrderMapper;
import com.mystore.persistence.PayInfoMapper;
import com.mystore.service.MyAlipayService;
import com.mystore.utils.BigDecimalUtil;
import com.mystore.utils.DateTimeFormatUtil;
import com.mystore.vo.QRCodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service("myAlipayService")
@Slf4j
public class MyAlipayServiceImpl implements MyAlipayService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public CommonResponse<QRCodeVO> getQRCode(Integer userId,Long orderNo){
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("order_no",orderNo);
        Order order=orderMapper.selectOne(queryWrapper);
        if (order==null){
            return CommonResponse.createForError("订单不存在");
        }

        QueryWrapper<OrderItem> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("user_id",userId).eq("order_no",orderNo);
        List<OrderItem> orderItemList=orderItemMapper.selectList(queryWrapper1);

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderNo.toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "Mystore商城 订单号："+orderNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPaymentPrice().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共"+order.getPaymentPrice()+"元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        //todo:orderItemList->goodsDetailList

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        for (OrderItem orderItem:orderItemList){
            GoodsDetail goods=GoodsDetail.newInstance(orderItem.getOrderNo().toString(),orderItem.getProductName(), BigDecimalUtil.divide(orderItem.getTotalPrice().doubleValue(),orderItem.getQuantity().doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

//        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
//        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);
//
//        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("https://575098d8d3.zicp.fun/pay/callback")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");
        AlipayTradeService tradeService=new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                String qrCodeBase64=getQRImageByBase64(response.getQrCode());//转换为图片

                QRCodeVO qrCodeVO=new QRCodeVO();
                qrCodeVO.setOrderNo(orderNo);
                qrCodeVO.setQrCodeBase64(qrCodeBase64);

                return CommonResponse.createForSuccess(qrCodeVO);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return CommonResponse.createForError("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return CommonResponse.createForError("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return CommonResponse.createForError("不支持的交易状态，交易返回异常!!!");
        }
    }

    //回调
    @Override
    public CommonResponse<Object> alipayCallback(Map<String,String> params){
        Long orderNo=Long.parseLong(params.get("out_trade_no"));
        String tradeNo=params.get("trade_no");
        String tradeStatus=params.get("trade_status");

        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        Order order=orderMapper.selectOne(queryWrapper);

        //重复回调(已付款/已发货/交易成功/交易关闭)
        if (order.getStatus()>= CONSTANT.OrderStatus.PAID.getCode()){
            return CommonResponse.createForSuccess();
        }

        //如果交易成功则修改订单状态
        if (tradeStatus.equals(CONSTANT.AlipayTradeStatus.TRADE_SUCCESS)){
            order.setStatus(CONSTANT.OrderStatus.PAID.getCode());
            order.setPaymentTime(DateTimeFormatUtil.parseGMT(params.get("gmt_payment")));
            orderMapper.updateById(order);
        }

        PayInfo payInfo=new PayInfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setUserId(order.getUserId());
        payInfo.setPaymentType(CONSTANT.PayType.ALIPAY.getCode());
        payInfo.setTradeStatus(tradeStatus);
        payInfo.setTradeNo(tradeNo);
        payInfo.setCreateTime(LocalDateTime.now());
        payInfo.setUpdateTime(LocalDateTime.now());
        payInfoMapper.insert(payInfo);

        return CommonResponse.createForSuccess();
    }

    //查询订单状态
    @Override
    public CommonResponse<Object> getPayStatus(Integer userId,Long orderNo){
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("order_no",orderNo);
        Order order=orderMapper.selectOne(queryWrapper);

        if (order.getStatus()==CONSTANT.OrderStatus.PAID.getCode()){
            return CommonResponse.createForSuccess();
        }

        return CommonResponse.createForError();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    //将字符串转化为Base64编码的图片(response中的一个字符串)
    private String getQRImageByBase64(String qrImageCode){
        final int BLACK = 0xFF000000;
        final int WHITE = 0xFFFFFFFF;

        String base64Image="";

        try {
            //生成图片
            Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrImageCode, BarcodeFormat.QR_CODE, 256, 256, hints);
            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 256; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
                }
            }

            //将图片变为字节流，再转换为Base64编码
            ByteArrayOutputStream os=new ByteArrayOutputStream();
            ImageIO.write(image,"png",os);
            base64Image=new String(new Base64().encode(os.toByteArray()));

            return base64Image;
        }catch (Exception e){
            log.error("创建二维码失败",e);
            return null;
        }
    }
}
