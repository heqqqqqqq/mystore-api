package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.domain.Category;

import java.util.List;

public interface CategoryService {
    //获取单个分类信息详情
    CommonResponse<Category> getCategory(Integer categoryId);

    //获取一个分类信息的一级子分类列表，不递归
    CommonResponse<List<Category>> getChildCategories(Integer categoryId);

    //获取一个分类信息的所有子分类ID，递归查询
    CommonResponse<List<Integer>> getCategoryAndAllChild(Integer categoryId);

    //新增分类
    CommonResponse<Object> addCategory(String categoryName,Integer parentId);

    //更新分类的名称
    CommonResponse<Object> updateCategory(Integer categoryId,String newCategoryName);
}
