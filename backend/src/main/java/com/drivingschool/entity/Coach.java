package com.drivingschool.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教练实体类
 */
@Data
@TableName("coach")
public class Coach {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String coachName;
    
    private String coachLicense;
    
    private String idCard;
    
    private String phone;
    
    private Integer coachStatus;
    
    private String faceImageUrl;
    
    private LocalDateTime hireDate;
    
    private LocalDateTime leaveDate;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}