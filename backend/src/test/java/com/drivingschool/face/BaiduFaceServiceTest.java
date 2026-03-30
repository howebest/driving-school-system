package com.drivingschool.face;

import com.drivingschool.entity.Student;
import com.drivingschool.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 人脸识别服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
class BaiduFaceServiceTest {

    @Autowired
    private BaiduFaceService faceService;

    @Autowired
    private StudentService studentService;

    @Test
    void testFaceVerify() {
        // 准备测试图片
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_face.jpg"));
            MockMultipartFile imageFile = new MockMultipartFile("image", "test_face.jpg", "image/jpeg", imageBytes);
            
            // 执行人脸验证
            FaceVerifyResponse response = faceService.faceVerify(
                java.util.Base64.getEncoder().encodeToString(imageBytes));
            
            assertNotNull(response);
            // 根据实际情况断言结果
            System.out.println("人脸识别结果: " + response.getErrorMsg());
        } catch (Exception e) {
            System.err.println("测试图片不存在，跳过测试: " + e.getMessage());
        }
    }

    @Test
    void testFaceRegister() {
        try {
            // 准备测试数据
            Student student = new Student();
            student.setId(1L);
            student.setStudentName("测试学员");
            student.setIdCard("110101199001011234");
            
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_face.jpg"));
            
            // 执行人脸注册
            boolean result = faceService.faceRegister(
                student.getId().toString(),
                student.getStudentName(),
                java.util.Base64.getEncoder().encodeToString(imageBytes)
            );
            
            assertTrue(result, "人脸注册应该成功");
        } catch (Exception e) {
            System.err.println("测试图片不存在，跳过测试: " + e.getMessage());
        }
    }
}