package com.mystore.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("image.server")//设置前缀，可以直接在application.properties中进行配置
@Data
@Component
public class ImageServerConfig {
    private String url;
    private String username;
    private String password;
}
