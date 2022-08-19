package com.mystore.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.OrderItem;
import com.mystore.domain.User;
import com.mystore.service.OrderService;
import com.mystore.vo.OrderCartItemVO;
import com.mystore.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    @PostMapping("create")
    public CommonResponse<OrderVO> create(
            @RequestParam @NotNull(message = "地址ID不能为空") Integer addressId,
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.create(loginUser.getId(),addressId);
    }

    //获取购物车中选中的商品列表
    @GetMapping("cart_item_list")
    public CommonResponse<OrderCartItemVO> getCheckedCartItem(HttpSession session){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.getCheckedCartItemList(loginUser.getId());
    }

    //获取订单详情
    @GetMapping("get_order_detail")
    public CommonResponse<OrderVO> getOrderDetail(
            HttpSession session,
            @RequestParam @NotNull(message = "订单编号不能为空") Long orderNo
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.getOrderDetail(loginUser.getId(), orderNo);
    }

    //获取订单列表
    @GetMapping("get_order_list")
    public CommonResponse<Page<OrderVO>> getOrderList(
            HttpSession session,
            @RequestParam(defaultValue = "1") int pageSize,
            @RequestParam(defaultValue = "2") int pageNum
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.getOrderList(loginUser.getId(),pageNum,pageSize);
    }

    //取消订单
    @PostMapping("cancel")
    public CommonResponse<String> cancel(
            HttpSession session,
            @RequestParam @NotNull(message = "订单编号不能为空") Long orderNo
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return orderService.cancel(loginUser.getId(),orderNo);
    }
}
