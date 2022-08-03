package com.mystore;

import com.mystore.persistence.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MystoreApiApplicationTests {

    @Autowired
    private UserMapper userMapper;

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
}
