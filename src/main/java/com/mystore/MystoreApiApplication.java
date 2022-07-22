package com.mystore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

/*这个类开始运行就意味着整个项目跑起来，将@MapperScan注解标在该类上就意味着在项目跑起来之前需要进行Mapper扫描，
* 从而将persistence包中的每一个Mapper接口变成实体类 */
@MapperScan("com.mystore.persistence")

public class MystoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MystoreApiApplication.class, args);
    }

}
