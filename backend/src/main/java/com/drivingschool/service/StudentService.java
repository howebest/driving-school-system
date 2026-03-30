package com.drivingschool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.drivingschool.entity.Student;
import java.util.List;

/**
 * 学员服务接口
 */
public interface StudentService extends IService<Student> {
    
    /**
     * 获取学员列表
     * @param keyword 关键词搜索
     * @param status 状态筛选
     * @return 学员列表
     */
    List<Student> getStudentList(String keyword, Integer status);
    
    /**
     * 新增学员
     * @param student 学员信息
     */
    void addStudent(Student student);
    
    /**
     * 编辑学员
     * @param student 学员信息
     */
    void editStudent(Student student);
    
    /**
     * 删除学员
     * @param id 学员ID
     */
    void deleteStudent(Long id);
    
    /**
     * 更新学员状态
     * @param id 学员ID
     * @param status 状态
     */
    void updateStudentStatus(Long id, Integer status);
    
    /**
     * 绑定学员人脸
     * @param id 学员ID
     * @param faceImageUrl 人脸图片URL
     */
    void bindStudentFace(Long id, String faceImageUrl);
}