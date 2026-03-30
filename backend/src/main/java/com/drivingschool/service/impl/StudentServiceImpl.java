package com.drivingschool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drivingschool.common.BusinessException;
import com.drivingschool.entity.Student;
import com.drivingschool.mapper.StudentMapper;
import com.drivingschool.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Override
    public List<Student> getStudentList(String keyword, Integer status) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("student_name", keyword)
                    .or()
                    .like("id_card", keyword)
                    .or()
                    .like("phone", keyword));
        }
        
        if (status != null) {
            queryWrapper.eq("student_status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public void addStudent(Student student) {
        // 检查身份证号是否已存在
        Student existingStudent = this.getOne(new QueryWrapper<Student>()
                .eq("id_card", student.getIdCard())
                .eq("is_deleted", 0));
        if (existingStudent != null) {
            throw new BusinessException("该身份证号已存在");
        }
        
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        student.setIsDeleted(0);
        this.save(student);
    }

    @Override
    public void editStudent(Student student) {
        Student existingStudent = this.getById(student.getId());
        if (existingStudent == null) {
            throw new BusinessException("学员不存在");
        }
        
        // 检查身份证号是否被其他人使用
        if (!existingStudent.getIdCard().equals(student.getIdCard())) {
            Student duplicateStudent = this.getOne(new QueryWrapper<Student>()
                    .eq("id_card", student.getIdCard())
                    .eq("is_deleted", 0));
            if (duplicateStudent != null && !duplicateStudent.getId().equals(student.getId())) {
                throw new BusinessException("该身份证号已被其他学员使用");
            }
        }
        
        student.setUpdateTime(LocalDateTime.now());
        this.updateById(student);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = this.getById(id);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        
        // 逻辑删除
        student.setIsDeleted(1);
        student.setUpdateTime(LocalDateTime.now());
        this.updateById(student);
    }

    @Override
    public void updateStudentStatus(Long id, Integer status) {
        Student student = this.getById(id);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        
        student.setStudentStatus(status);
        student.setUpdateTime(LocalDateTime.now());
        
        // 如果是结业或无效状态，记录相应时间
        if (status == 2) { // 已结业
            student.setGraduationDate(LocalDateTime.now());
        }
        
        this.updateById(student);
    }

    @Override
    public void bindStudentFace(Long id, String faceImageUrl) {
        Student student = this.getById(id);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        
        student.setFaceImageUrl(faceImageUrl);
        student.setUpdateTime(LocalDateTime.now());
        this.updateById(student);
    }
}