package com.mystore.vo;

import lombok.Data;

@Data
public class AliOSSPolicy {

    private String AccessId;
    private String policy;
    private String signature;
    private String dir;
    private String host;

    private String callback;
}
