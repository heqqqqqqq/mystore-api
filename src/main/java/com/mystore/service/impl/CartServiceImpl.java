package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.Cart;
import com.mystore.domain.Product;
import com.mystore.persistence.CartMapper;
import com.mystore.persistence.ProductMapper;
import com.mystore.service.CartService;
import com.mystore.utils.BigDecimalUtil;
import com.mystore.utils.ImageServerConfig;
import com.mystore.vo.CartItemVO;
import com.mystore.vo.CartVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<CartVO> addCart(Integer userId,Integer productId,Integer quantity){
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("product_id",productId).eq("user_id",userId);
        Cart cartItem=cartMapper.selectOne(queryWrapper);

        if(cartItem==null){//目前购物车中没有此产品
            cartItem=new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(quantity);
            cartItem.setChecked(CONSTANT.CART.CHECKED);//默认刚加入购物车会选中
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cartItem);
        }else{//目前购物车中有此产品
            UpdateWrapper<Cart> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("id",cartItem.getId());
            updateWrapper.set("quantity",quantity);
            updateWrapper.set("update_time",LocalDateTime.now());
            cartMapper.update(cartItem,updateWrapper);
        }
        CartVO cartVO=this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> updateCart(Integer userId,Integer productId,Integer quantity){
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("product_id",productId).eq("user_id",userId);
        Cart cartItem=cartMapper.selectOne(queryWrapper);

        if(cartItem!=null){
            UpdateWrapper<Cart> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("id",cartItem.getId());
            updateWrapper.set("quantity",quantity);
            updateWrapper.set("update_time",LocalDateTime.now());
            cartMapper.update(cartItem,updateWrapper);

            CartVO cartVO=this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        }else {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(),ResponseCode.ARGUMENT_ILLEGAL.getDescription());
        }
    }

    //productIds，同前端约定好格式为”1，2，3“
    @Override
    public CommonResponse<CartVO> deleteCart(Integer userId,String productIds){
        List<String> productIdStrings= Splitter.on(",").splitToList(productIds);//根据逗号分割成productId数组
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        for (String productIdString:productIdStrings){
            int productId=Integer.parseInt(productIdString);
            queryWrapper.eq("product_id",productId);
            queryWrapper.eq("user_id",userId);
            cartMapper.delete(queryWrapper);
            queryWrapper.clear();//注意清除记录
        }
        CartVO cartVO=this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> getCartList(Integer userId){
        CartVO cartVO=this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    //设置全选或全不选
    @Override
    public CommonResponse<CartVO> updateAllCheckStatus(Integer userId,Integer checkStatus){
        Cart cartItem=new Cart();
        UpdateWrapper<Cart> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("user_id",userId);
        updateWrapper.set("checked",checkStatus);
        updateWrapper.set("update_time",LocalDateTime.now());
        cartMapper.update(cartItem,updateWrapper);

        CartVO cartVO=this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    //设置购物车某项选中或不选中
    @Override
    public CommonResponse<CartVO> updateCheckStatus(Integer userId,Integer productId,Integer checkStatus){
        UpdateWrapper<Cart> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("product_id",productId);
        updateWrapper.eq("user_id",userId);
        updateWrapper.set("checked",checkStatus);
        updateWrapper.set("update_time",LocalDateTime.now());
        Cart cartItem=new Cart();
        cartMapper.update(cartItem,updateWrapper);

        CartVO cartVO=this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<Integer> getCartCount(Integer userId){
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);

        List<Cart> cartList=cartMapper.selectList(queryWrapper);
        int count=0;
        for(Cart cartItem:cartList){
            count+= cartItem.getQuantity();
        }
        return CommonResponse.createForSuccess(count);
    }

    //将数据库中查出的CartItem列表转换为CartVO
    private CartVO getCartVOAndCheckStock(Integer userId){
        CartVO cartVO=new CartVO();
        List<CartItemVO> cartItemVOList= Lists.newArrayList();

        //从数据库中查询userId的购物车信息
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Cart> cartItemList=cartMapper.selectList(queryWrapper);
        BigDecimal cartTotalPrice=new BigDecimal("0");//用字符串的形式来初始化
        boolean allSelected=true;

        if(CollectionUtils.isNotEmpty(cartItemList)){
            //遍历
            for(Cart cartItem:cartItemList){
                CartItemVO cartItemVO=new CartItemVO();
                cartItemVO.setId(cartItem.getId());
                cartItemVO.setUserId(cartItem.getUserId());
                //cartItemVO.setQuantity(cartItem.getQuantity());
                cartItemVO.setChecked(cartItem.getChecked());
                cartItemVO.setProductId(cartItem.getProductId());

                Product product=productMapper.selectById(cartItem.getProductId());
                if(product!=null){
                    cartItemVO.setProductName(product.getName());
                    cartItemVO.setProductSubtitle(product.getSubtitle());
                    cartItemVO.setProductPrice(product.getPrice());
                    cartItemVO.setProductStock(product.getStock());
                    cartItemVO.setProductMainImage(product.getMainImage());

                    //处理库存,如果库存不够，先加入购物车再说，然后把购物车内的商品数量改为库存最大值（一般商城的用法）
                    if(product.getStock()>= cartItem.getQuantity()){
                        cartItemVO.setQuantity(cartItem.getQuantity());
                        cartItemVO.setCheckStock(true);
                    }
                    else {//库存不足
                        cartItemVO.setQuantity(product.getStock());
                        //修改数据库
                        Cart updateStockCart=new Cart();
                        UpdateWrapper<Cart> updateWrapper=new UpdateWrapper<>();
                        updateWrapper.eq("id",cartItem.getId());
                        updateWrapper.set("quantity",product.getStock());
                        updateWrapper.set("update_time",LocalDateTime.now());
                        cartMapper.update(updateStockCart,updateWrapper);
                        cartItemVO.setCheckStock(false);
                    }
                    cartItemVO.setCartItemTotalPrice(
                            BigDecimalUtil.multiply(cartItemVO.getProductPrice().doubleValue(),
                                    cartItemVO.getQuantity().doubleValue()));

                }
                cartItemVOList.add(cartItemVO);

                if(cartItem.getChecked()==CONSTANT.CART.CHECKED){//如果被选中
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartItemVO.getCartItemTotalPrice().doubleValue());
                }else {
                    allSelected=false;//如果有一个没被选中就设为false
                }
            }
            cartVO.setCartTotalPrice(cartTotalPrice);
            cartVO.setCartItemVOList(cartItemVOList);
            cartVO.setAllSelected(allSelected);
            cartVO.setProductImageServer(imageServerConfig.getUrl());
        }
        return cartVO;
    }
}
