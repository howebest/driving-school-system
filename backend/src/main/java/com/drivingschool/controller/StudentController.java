package com.drivingschool.controller;

import com.drivingschool.common.Result;
import com.drivingschool.entity.Student;
import com.drivingschool.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 学员管理控制器
 */
@Api(tags = "学员管理")
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation("获取学员列表")
    @GetMapping("/list")
    public Result<List<Student>> getStudentList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<Student> students = studentService.getStudentList(keyword, status);
        return Result.success(students);
    }

    @ApiOperation("根据ID获取学员信息")
    @GetMapping("/{id}")
    public Result<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    @ApiOperation("新增学员")
    @PostMapping("/add")
    public Result<Void> addStudent(@Valid @RequestBody Student student) {
        studentService.addStudent(student);
        return Result.success("新增学员成功");
    }

    @ApiOperation("编辑学员")
    @PutMapping("/edit")
    public Result<Void> editStudent(@Valid @RequestBody Student student) {
        studentService.editStudent(student);
        return Result.success("编辑学员成功");
    }

    @ApiOperation("删除学员")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success("删除学员成功");
    }

    @ApiOperation("更新学员状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateStudentStatus(@PathVariable Long id, @RequestParam Integer status) {
        studentService.updateStudentStatus(id, status);
        return Result.success("更新学员状态成功");
    }

    @ApiOperation("绑定学员人脸")
    @PostMapping("/face/bind")
    public Result<Void> bindStudentFace(@RequestParam Long id, @RequestParam String faceImageUrl) {
        studentService.bindStudentFace(id, faceImageUrl);
        return Result.success("人脸绑定成功");
    }
}