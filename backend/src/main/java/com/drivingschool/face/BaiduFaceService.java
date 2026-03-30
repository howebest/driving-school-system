package com.drivingschool.face;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度人脸识别服务
 */
@Slf4j
@Service
public class BaiduFaceService {

    @Value("${baidu.face.api-key:your-api-key}")
    private String apiKey;
    
    @Value("${baidu.face.secret-key:your-secret-key}")
    private String secretKey;
    
    @Value("${baidu.face.auth-url:https://aip.baidubce.com/oauth/2.0/token}")
    private String authUrl;
    
    @Value("${baidu.face.verify-url:https://aip.baidubce.com/rest/2.0/face/v3/search}")
    private String verifyUrl;
    
    private String accessToken;
    private long tokenExpireTime;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BaiduFaceService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取访问令牌
     */
    private String getAccessToken() {
        // 检查token是否过期
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "client_credentials");
            params.add("client_id", apiKey);
            params.add("client_secret", secretKey);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            String response = restTemplate.postForObject(authUrl, request, String.class);
            
            Map<String, Object> resultMap = objectMapper.readValue(response, HashMap.class);
            accessToken = (String) resultMap.get("access_token");
            Integer expiresIn = (Integer) resultMap.get("expires_in");
            
            // 提前5分钟过期，确保安全
            tokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
            
            log.info("获取百度人脸识别access_token成功");
            return accessToken;
        } catch (Exception e) {
            log.error("获取百度人脸识别access_token失败", e);
            throw new RuntimeException("获取人脸识别服务认证失败");
        }
    }

    /**
     * 人脸比对验证
     * @param imageBase64 图片Base64编码
     * @return 验证结果
     */
    public FaceVerifyResponse faceVerify(String imageBase64) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            
            FaceVerifyRequest request = new FaceVerifyRequest();
            request.setImage(imageBase64);
            
            HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(request), headers);
            
            String response = restTemplate.postForObject(verifyUrl, entity, String.class);
            
            return objectMapper.readValue(response, FaceVerifyResponse.class);
        } catch (Exception e) {
            log.error("人脸识别验证失败", e);
            FaceVerifyResponse errorResponse = new FaceVerifyResponse();
            errorResponse.setErrorCode(-1);
            errorResponse.setErrorMsg("人脸识别服务调用失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 人脸注册
     * @param userId 用户ID
     * @param userInfo 用户信息
     * @param imageBase64 图片Base64编码
     * @return 注册结果
     */
    public boolean faceRegister(String userId, String userInfo, String imageBase64) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image", imageBase64);
            requestBody.put("image_type", "BASE64");
            requestBody.put("group_id", "driving_school");
            requestBody.put("user_id", userId);
            requestBody.put("user_info", userInfo);
            
            HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody), headers);
            
            String registerUrl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
            String response = restTemplate.postForObject(registerUrl + "?access_token=" + token, entity, String.class);
            
            Map<String, Object> resultMap = objectMapper.readValue(response, HashMap.class);
            Integer errorCode = (Integer) resultMap.get("error_code");
            
            return errorCode == 0;
        } catch (Exception e) {
            log.error("人脸注册失败", e);
            return false;
        }
    }

    /**
     * 人脸更新
     * @param userId 用户ID
     * @param imageBase64 图片Base64编码
     * @return 更新结果
     */
    public boolean faceUpdate(String userId, String imageBase64) {
        try {
            String token = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image", imageBase64);
            requestBody.put("image_type", "BASE64");
            requestBody.put("group_id", "driving_school");
            requestBody.put("user_id", userId);
            
            HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody), headers);
            
            String updateUrl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update";
            String response = restTemplate.postForObject(updateUrl + "?access_token=" + token, entity, String.class);
            
            Map<String, Object> resultMap = objectMapper.readValue(response, HashMap.class);
            Integer errorCode = (Integer) resultMap.get("error_code");
            
            return errorCode == 0;
        } catch (Exception e) {
            log.error("人脸更新失败", e);
            return false;
        }
    }
}