package com.drivingschool.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学员实体类
 */
@Data
@TableName("student")
public class Student {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentName;
    
    private String idCard;
    
    private String phone;
    
    private Integer studentStatus;
    
    private String faceImageUrl;
    
    private LocalDateTime enrollmentDate;
    
    private LocalDateTime graduationDate;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}