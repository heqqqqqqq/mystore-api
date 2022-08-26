package com.mystore.vo;

import lombok.Data;

@Data
public class QRCodeVO {
    private Long orderNo;
    private String qrCodeBase64;
}
