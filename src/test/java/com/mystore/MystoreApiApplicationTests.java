package com.mystore;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mystore.domain.Category;
import com.mystore.domain.Product;
import com.mystore.persistence.ProductMapper;
import com.mystore.persistence.UserMapper;
import com.mystore.service.CategoryService;
import com.mystore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class MystoreApiApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testMybatisPlus(){
        System.out.println(userMapper);
    }

    @Test
    public void testMD5(){

        String s="abc";

        //Spring Security
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();//密码编码器
        String s1=passwordEncoder.encode(s);
        System.out.println(s1);
        String s2=passwordEncoder.encode(s);
        System.out.println(s2);

    }

    @Test
    public void  testCategoryRecursion(){
//        Set<Category> categorySet=new HashSet<>();
//        categoryService.findChildCategory(100001,categorySet);
//        System.out.println("...");
    }

    @Test
    public void testMybatisPlusPage(){
        Page<Product> result=new Page<>(2,3);

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("name","%A%");
        result=productMapper.selectPage(result,queryWrapper);

        System.out.println(",,,,,,");
    }
}
