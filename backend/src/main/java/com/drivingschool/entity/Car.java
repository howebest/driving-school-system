package com.drivingschool.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 车辆实体类
 */
@Data
@TableName("car")
public class Car {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String licensePlate;
    
    private String carNumber;
    
    private Long coachId;
    
    private Integer carStatus;
    
    private String brand;
    
    private String model;
    
    private LocalDateTime purchaseDate;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}