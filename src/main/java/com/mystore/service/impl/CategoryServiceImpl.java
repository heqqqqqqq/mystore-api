package com.mystore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.domain.Category;
import com.mystore.persistence.CategoryMapper;
import com.mystore.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("categoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CommonResponse<Category> getCategory(Integer categoryId) {
        if(categoryId==null){
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }
        if (categoryId== CONSTANT.CATEGORY_ROOT){
            return CommonResponse.createForError("分类的根节点无详细信息");
        }
        Category category= categoryMapper.selectById(categoryId);
        if(category==null){
            return CommonResponse.createForError("无该分类信息");
        }
        return CommonResponse.createForSuccess(category);
    }

    @Override
    public CommonResponse<List<Category>> getChildCategories(Integer categoryId) {
        if(categoryId==null){
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }
        QueryWrapper<Category> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",categoryId);
        List<Category> categoryList=categoryMapper.selectList(queryWrapper);

        //线程不安全：categoryList.size()==0
        if(CollectionUtils.isEmpty(categoryList)){//mybatis-plus即便没有查到信息，返回的也不会是null
            log.info("非递归查询分类信息时没有查询到相关子分类信息");//*没有查到子分类不代表失败，不要直接用createForError
        }

        return CommonResponse.createForSuccess(categoryList);
    }

    //利用Java中的Set和传引用调用的特性（Set当中的元素是不能重复的）
    private Set<Category> findChildCategory(Integer categoryId,Set<Category> categorySet){
        Category category=categoryMapper.selectById(categoryId);
        if(category!=null){
            categorySet.add(category);
        }

        QueryWrapper<Category> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",categoryId);
        List<Category> categoryList=categoryMapper.selectList(queryWrapper);

        for(Category categoryItem:categoryList){
            findChildCategory(categoryItem.getId(),categorySet);//递归
        }
        return categorySet;
    }

    @Override
    public CommonResponse<List<Integer>> getCategoryAndAllChild(Integer categoryId) {
        Set<Category> categorySet= Sets.newHashSet();//结合工具类使用
        List<Integer> categoryIdList= Lists.newArrayList();

        if(categoryId==null){
            return CommonResponse.createForSuccess(categoryIdList);
        }
        findChildCategory(categoryId,categorySet);

        for (Category categoryItem:categorySet){
            categoryIdList.add(categoryItem.getId());
        }
        return CommonResponse.createForSuccess(categoryIdList);
    }

    @Override
    public CommonResponse<Object> addCategory(String categoryName, Integer parentId) {
        return null;
    }

    @Override
    public CommonResponse<Object> updateCategory(Integer categoryId, String newCategoryName) {
        return null;
    }
}
