package com.drivingschool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}