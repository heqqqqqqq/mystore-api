package com.mystore.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.mystore.common.CommonResponse;
import com.mystore.service.OSSService;
import com.mystore.vo.AliOSSCallbackResult;
import com.mystore.vo.AliOSSPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service("ossService")
@Slf4j
public class AliOSSServiceImp implements OSSService {

    @Autowired
    private OSSClient ossClient;

    @Value("${aliyun.oss.accessId}")
    private String ACCESS_ID;
    @Value("${aliyun.oss.accessKey}")
    private String ACCESS_KEY;
    @Value("${aliyun.oss.endpoint}")
    private String ENDPOINT;
    @Value("${aliyun.oss.bucket}")
    private String BUCKET;
    @Value("${aliyun.oss.dir}")
    private String DIR;
    @Value("${aliyun.oss.policy.expire}")
    private long EXPIRE_TIME;
    @Value("${aliyun.oss.maxSize}")
    private long MAX_SIZE;
    @Value("{aliyun.oss.callbackURL}")
    private String CALLBACK_URL;

    @Override
    public CommonResponse<AliOSSPolicy> generatePolicy() {

        AliOSSPolicy aliOSSPolicy=new AliOSSPolicy();

        String host = "http://" + BUCKET + "." + ENDPOINT; // host的格式为 bucketname.endpoint
        try{
            long expireTime = EXPIRE_TIME;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1024*1024*MAX_SIZE);//上传图片大小范围
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, DIR);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            JSONObject jasonCallback = new JSONObject();
            jasonCallback.put("callbackUrl",CALLBACK_URL);
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());

            aliOSSPolicy.setAccessId(ACCESS_ID);
            aliOSSPolicy.setPolicy(encodedPolicy);
            aliOSSPolicy.setSignature(postSignature);
            aliOSSPolicy.setHost(host);
            aliOSSPolicy.setDir(DIR);
            aliOSSPolicy.setCallback(base64CallbackBody);

        }catch (Exception e){
            log.info("服务端生成AliOSS的policy失败",e);
        }

        return CommonResponse.createForSuccess(aliOSSPolicy);
    }

    @Override
    public CommonResponse<AliOSSCallbackResult> callback(HttpServletRequest request){

        AliOSSCallbackResult result=new AliOSSCallbackResult();
        result.setFilename(request.getParameter("filename"));
        result.setSize(request.getParameter("size"));
        result.setHeight(request.getParameter("height"));
        result.setWidth(request.getParameter("width"));
        result.setMimeType(request.getParameter("mimeType"));

        return CommonResponse.createForSuccess(result);
    }
}
