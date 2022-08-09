package com.mystore.controller.admin;

import com.mystore.common.CommonResponse;
import com.mystore.domain.Category;
import com.mystore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/category/")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("get_child_categories")
    public CommonResponse<List<Category>> getChildCategories(
            @RequestParam(defaultValue = "0") Integer categoryId){
        return categoryService.getChildCategories(categoryId);
    }

    @GetMapping("get_all_child_categories")
    public CommonResponse<List<Integer>> getAllChildCategories(
            @RequestParam(defaultValue = "0") Integer categoryId){
        return categoryService.getCategoryAndAllChild(categoryId);
    }
}
