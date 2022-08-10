package com.mystore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Product;
import com.mystore.vo.ProductDetailVO;
import com.mystore.vo.ProductListVO;

import java.util.List;

public interface ProductService {

    //获取商品详情
    CommonResponse<ProductDetailVO> getProductDetail(Integer productId);

    //获取商品列表
    /*
    categoryId:给出该类别下的所有商品  keyWord:给出符合关键字的所有商品
    orderBy:给出按照指定顺序排列的所有商品，如销量、价格等
    pageNum:第几页  pageSize:一页有多少个商品
     */
    CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyWord, String orderBy, int pageNum, int pageSize);
}
