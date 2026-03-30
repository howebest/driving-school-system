package com.drivingschool.vehicle;

import lombok.Data;

/**
 * 车辆信息查询响应
 */
@Data
public class VehicleInfoResponse {
    private Integer code;
    private String message;
    private Data data;
    
    @Data
    public static class Data {
        private String plateNumber; // 车牌号码
        private String vehicleType; // 车辆类型
        private String owner; // 车主姓名
        private String vin; // 车架号
        private String engineNumber; // 发动机号
        private String brand; // 品牌
        private String model; // 型号
        private String color; // 颜色
        private String registerDate; // 注册日期
        private String status; // 状态
    }
}