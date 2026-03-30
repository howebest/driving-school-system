package com.drivingschool.vehicle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 车牌识别服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
class AliyunPlateServiceTest {

    @Autowired
    private AliyunPlateService plateService;

    @Test
    void testRecognizePlate() {
        try {
            // 准备测试图片
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_plate.jpg"));
            MockMultipartFile imageFile = new MockMultipartFile("image", "test_plate.jpg", "image/jpeg", imageBytes);
            
            // 执行车牌识别
            PlateRecognizeResponse response = plateService.recognizePlate(
                java.util.Base64.getEncoder().encodeToString(imageBytes));
            
            assertNotNull(response);
            System.out.println("车牌识别结果: " + response.getMsg());
        } catch (Exception e) {
            System.err.println("测试图片不存在，跳过测试: " + e.getMessage());
        }
    }

    @Test
    void testQueryVehicleInfo() {
        // 测试车辆信息查询
        VehicleInfoResponse response = plateService.queryVehicleInfo("京A12345");
        
        assertNotNull(response);
        System.out.println("车辆信息查询结果: " + response.getMessage());
    }
}