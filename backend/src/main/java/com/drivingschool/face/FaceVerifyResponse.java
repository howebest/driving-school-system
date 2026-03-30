package com.drivingschool.face;

import lombok.Data;

/**
 * 人脸识别响应结果
 */
@Data
public class FaceVerifyResponse {
    private Integer errorCode;
    private String errorMsg;
    private Result result;
    
    @Data
    public static class Result {
        private Double score; // 相似度分数
        private String userId; // 匹配的用户ID
        private String userInfo; // 用户信息
    }
}