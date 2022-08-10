package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.common.ResponseCode;
import com.mystore.domain.Category;
import com.mystore.domain.Product;
import com.mystore.persistence.CategoryMapper;
import com.mystore.persistence.ProductMapper;
import com.mystore.service.CategoryService;
import com.mystore.service.ProductService;
import com.mystore.utils.ImageServerConfig;
import com.mystore.vo.ProductDetailVO;
import com.mystore.vo.ProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService categoryService;
    @Resource
    private ImageServerConfig imageServerConfig;

    //私有方法，将Product转变为ProductDetailVO
    private ProductDetailVO productToProductDetailVO(Product product){
        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setName(product.getName());

        //时间戳格式化
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        productDetailVO.setCreateTime(formatter.format(product.getCreateTime()));
        productDetailVO.setUpdateTime(formatter.format(product.getUpdateTime()));

        //增加父分类的ID
        Category category=categoryMapper.selectById(product.getCategoryId());
        productDetailVO.setParentCategoryId(category.getParentId());

        productDetailVO.setImageServer(imageServerConfig.getUrl()+","+imageServerConfig.getUsername()+","+imageServerConfig.getPassword());

        return productDetailVO;
    }

    private ProductListVO productToProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();

        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setPrice(product.getPrice());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setStatus(product.getStatus());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        //图片服务器信息
        productListVO.setImageServer(imageServerConfig.getUrl()+","+imageServerConfig.getUsername()+","+imageServerConfig.getPassword());

        return  productListVO;
    }

    //Page<Product> -> Page<ProductListVO>
    private Page<ProductListVO> toProductListPageVO(Page<Product> result){
        List<ProductListVO> productListVOList= Lists.newArrayList();
        for (Product item:result.getRecords()){
            ProductListVO productListVO=productToProductListVO(item);
            productListVOList.add(productListVO);
        }
        Page<ProductListVO> newResult=new Page<>();
        newResult.setSize(result.getSize());
        newResult.setPages(result.getPages());
        newResult.setTotal(result.getTotal());

        newResult.setRecords(productListVOList);

        return newResult;
    }

    @Override
    public CommonResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if(productId==null){//参数错误
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getDescription());
        }
        Product product=productMapper.selectById(productId);
        if(product==null){
            return CommonResponse.createForError("商品不存在");
        }

        //前台接口获取商品详情要考虑在售状态
        if(product.getStatus()!= CONSTANT.ProductStatus.ON_SALE.getCode()){
            return CommonResponse.createForError("产品不在售、下架或其他原因");
        }

        ProductDetailVO productDetailVO=productToProductDetailVO(product);
        return CommonResponse.createForSuccess(productDetailVO);
    }

    @Override
    public CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyWord, String orderBy, int pageNum, int pageSize) {

        //两种数据不正确的情况
        if(StringUtils.isBlank(keyWord)&&categoryId==null){
            return CommonResponse.createForError("分类ID和keyword至少提交一项");
        }
        if(categoryId!=null){
            Category category=categoryMapper.selectById(categoryId);
            if(category==null&&StringUtils.isBlank(keyWord)){
                log.info("没有查到分类ID为{}的商品信息，且keyWord为空",categoryId);
                return CommonResponse.createForSuccess();
            }
        }

        Page<Product> result=new Page<>();//不支持直接按照ProductListVO查询
        result.setCurrent(pageNum);//当前页
        result.setSize(pageSize);//每页几个

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        //增加按分类条件进行查询
        List<Integer> categoryIdList=categoryService.getCategoryAndAllChild(categoryId).getData();//用getData()获取通用响应中的值
        if(categoryIdList.size()!=0){
            queryWrapper.in("category_id",categoryIdList);//在categoryList中的每一个id都要查
        }
        //增加关键字模糊查询
        if(StringUtils.isNotBlank(keyWord)){
            queryWrapper.like("name",keyWord);//会自动在前后添加%
        }
        //增加排序(假设只支持价格排序，price_asc和price_desc)
        if(StringUtils.isNotBlank(orderBy)){
            if(StringUtils.equals(CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC,orderBy)){
                queryWrapper.orderByAsc("price");
            }else if(StringUtils.equals(CONSTANT.PRODUCT_ORDER_BY_PRICE_DESC,orderBy)){
                queryWrapper.orderByDesc("price");
            }
        }

        result=productMapper.selectPage(result,queryWrapper);

        Page<ProductListVO> newResult=toProductListPageVO(result);//Page<Product> -> Page<ProductListVO>

        return CommonResponse.createForSuccess(newResult);
    }
}
