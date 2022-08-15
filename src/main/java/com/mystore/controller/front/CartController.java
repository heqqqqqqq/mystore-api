package com.mystore.controller.front;

import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.User;
import com.mystore.service.CartService;
import com.mystore.vo.CartVO;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private CartService cartService;

    //加入购物车，返回成功与否，HttpSession判断当前是否登录
    @PostMapping("add_cart")
    public CommonResponse<CartVO> addCart(
            HttpSession session,
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId,
            @RequestParam @Range(min=1,message = "商品数量不能<=0") Integer quantity){

        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser==null){//用户未登录
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.addCart(loginUser.getId(),productId,quantity);
    }

    @PostMapping("update_cart")
    public CommonResponse<CartVO> updateCart(
            HttpSession session,
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId,
            @RequestParam @Range(min=1,message = "商品数量不能<=0") Integer quantity){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateCart(loginUser.getId(),productId,quantity);
    }

    @PostMapping("delete_cart")
    public CommonResponse<CartVO> deleteCartItems(
            HttpSession session,
            @RequestParam @NotNull(message = "商品ID不能为空") String productIds
    ){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.deleteCart(loginUser.getId(),productIds);
    }

    @GetMapping("get_cart_list")
    public CommonResponse<CartVO> getCartList(
            HttpSession session
    ){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.getCartList(loginUser.getId());
    }

    @GetMapping("set_all_checked")
    public CommonResponse<CartVO> setAllChecked(
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateAllCheckStatus(loginUser.getId(),CONSTANT.CART.CHECKED);
    }

    @GetMapping("set_all_unchecked")
    public CommonResponse<CartVO> setAllUnchecked(
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateAllCheckStatus(loginUser.getId(),CONSTANT.CART.UNCHECKED);
    }

    @GetMapping("set_cart_item_checked")
    public CommonResponse<CartVO> setCartItemChecked(
            HttpSession session,
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateCheckStatus(loginUser.getId(),productId,CONSTANT.CART.CHECKED);
    }

    @GetMapping("set_cart_item_unchecked")
    public CommonResponse<CartVO> setCartItemUnchecked(
            HttpSession session,
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.updateCheckStatus(loginUser.getId(),productId,CONSTANT.CART.UNCHECKED);
    }

    @GetMapping("get_cart_count")
    public CommonResponse<Integer> getartCount(
            HttpSession session
    ){
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return cartService.getCartCount(loginUser.getId());
    }
}
