package com.mystore.utils.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Product;
import com.mystore.service.ProductService;
import com.mystore.vo.ProductDetailVO;
import com.mystore.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/product/")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("get_product_detail")
    public CommonResponse<ProductDetailVO> getProductDetail(
            @RequestParam @NotNull(message = "商品ID不能为空") Integer productId){
        return productService.getProductDetail(productId);
    }

    @GetMapping("get_product_list")
    public CommonResponse<Page<ProductListVO>> getProductList(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyWord,
            @RequestParam(defaultValue = "")String orderBy,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "2") int pageSize){
        return productService.getProductList(categoryId,keyWord,orderBy,pageNum,pageSize);
    }
}
