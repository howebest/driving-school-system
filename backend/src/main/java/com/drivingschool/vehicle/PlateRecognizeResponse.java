package com.drivingschool.vehicle;

import lombok.Data;
import java.util.List;

/**
 * 车牌识别响应结果
 */
@Data
public class PlateRecognizeResponse {
    private String requestId;
    private Integer code;
    private String msg;
    private Data data;
    
    @Data
    public static class Data {
        private List<VehiclePlate> plates;
    }
    
    @Data
    public static class VehiclePlate {
        private String plateNumber; // 车牌号码
        private String plateType; // 车牌类型
        private Double confidence; // 置信度
        private String plateColor; // 车牌颜色
        private Location location; // 车牌位置
    }
    
    @Data
    public static class Location {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
}