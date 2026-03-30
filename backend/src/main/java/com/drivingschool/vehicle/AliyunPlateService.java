package com.drivingschool.vehicle;

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

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云车牌识别服务
 */
@Slf4j
@Service
public class AliyunPlateService {

    @Value("${aliyun.plate.access-key-id:your-access-key-id}")
    private String accessKeyId;
    
    @Value("${aliyun.plate.access-key-secret:your-access-key-secret}")
    private String accessKeySecret;
    
    @Value("${aliyun.plate.endpoint:ocr.cn-shanghai.aliyuncs.com}")
    private String endpoint;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AliyunPlateService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 车牌识别
     * @param imageBase64 图片Base64编码
     * @return 识别结果
     */
    public PlateRecognizeResponse recognizePlate(String imageBase64) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "APPCODE " + accessKeyId);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("image", imageBase64);
            params.add("configure", "{\"multi_detect\":false,\"detect_direction\":0}");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            String url = "https://" + endpoint + "/recognize/vehicle/plate";
            String response = restTemplate.postForObject(url, request, String.class);
            
            return objectMapper.readValue(response, PlateRecognizeResponse.class);
        } catch (Exception e) {
            log.error("车牌识别失败", e);
            PlateRecognizeResponse errorResponse = new PlateRecognizeResponse();
            errorResponse.setCode(-1);
            errorResponse.setMsg("车牌识别服务调用失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 车辆信息查询
     * @param plateNumber 车牌号码
     * @return 车辆信息
     */
    public VehicleInfoResponse queryVehicleInfo(String plateNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "APPCODE " + accessKeyId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("plate_number", plateNumber);

            HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody), headers);
            
            String url = "https://" + endpoint + "/query/vehicle/info";
            String response = restTemplate.postForObject(url, entity, String.class);
            
            return objectMapper.readValue(response, VehicleInfoResponse.class);
        } catch (Exception e) {
            log.error("车辆信息查询失败", e);
            VehicleInfoResponse errorResponse = new VehicleInfoResponse();
            errorResponse.setCode(-1);
            errorResponse.setMessage("车辆信息查询服务调用失败: " + e.getMessage());
            return errorResponse;
        }
    }
}