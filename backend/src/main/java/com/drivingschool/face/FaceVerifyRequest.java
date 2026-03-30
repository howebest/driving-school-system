package com.drivingschool.face;

import lombok.Data;

/**
 * 人脸识别请求参数
 */
@Data
public class FaceVerifyRequest {
    private String image; // 图片Base64编码
    private String imageType = "BASE64";
    private String groupIdList = "driving_school"; // 用户组ID
    private String qualityControl = "NORMAL";
    private String livenessControl = "NORMAL";
}