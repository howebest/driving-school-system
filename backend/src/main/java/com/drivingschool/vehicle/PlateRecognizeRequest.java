package com.drivingschool.vehicle;

import lombok.Data;

/**
 * 车牌识别请求参数
 */
@Data
public class PlateRecognizeRequest {
    private String image; // 图片Base64编码或URL
    private Boolean multi_detect = false; // 是否检测多个车牌
    private Integer detect_direction = 0; // 检测方向
}