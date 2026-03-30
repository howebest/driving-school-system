package com.drivingschool.controller;

import com.drivingschool.common.Result;
import com.drivingschool.face.BaiduFaceService;
import com.drivingschool.face.FaceVerifyResponse;
import com.drivingschool.vehicle.AliyunPlateService;
import com.drivingschool.vehicle.PlateRecognizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * 身份核验控制器
 */
@Api(tags = "身份核验")
@RestController
@RequestMapping("/verify")
public class IdentityVerifyController {

    private final BaiduFaceService faceService;
    private final AliyunPlateService plateService;

    public IdentityVerifyController(BaiduFaceService faceService, AliyunPlateService plateService) {
        this.faceService = faceService;
        this.plateService = plateService;
    }

    @ApiOperation("人脸验证")
    @PostMapping("/face")
    public Result<FaceVerifyResponse> verifyFace(@RequestParam("image") MultipartFile image) {
        try {
            // 将图片转换为Base64
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            
            // 调用人脸识别服务
            FaceVerifyResponse response = faceService.faceVerify(imageBase64);
            
            if (response.getErrorCode() == 0) {
                return Result.success("人脸识别成功", response);
            } else {
                return Result.error(response.getErrorMsg());
            }
        } catch (Exception e) {
            return Result.error("人脸识别失败: " + e.getMessage());
        }
    }

    @ApiOperation("人脸注册")
    @PostMapping("/face/register")
    public Result<Boolean> registerFace(
            @RequestParam("userId") String userId,
            @RequestParam("userInfo") String userInfo,
            @RequestParam("image") MultipartFile image) {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            boolean result = faceService.faceRegister(userId, userInfo, imageBase64);
            
            if (result) {
                return Result.success("人脸注册成功", true);
            } else {
                return Result.error("人脸注册失败");
            }
        } catch (Exception e) {
            return Result.error("人脸注册失败: " + e.getMessage());
        }
    }

    @ApiOperation("人脸更新")
    @PostMapping("/face/update")
    public Result<Boolean> updateFace(
            @RequestParam("userId") String userId,
            @RequestParam("image") MultipartFile image) {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            boolean result = faceService.faceUpdate(userId, imageBase64);
            
            if (result) {
                return Result.success("人脸更新成功", true);
            } else {
                return Result.error("人脸更新失败");
            }
        } catch (Exception e) {
            return Result.error("人脸更新失败: " + e.getMessage());
        }
    }

    @ApiOperation("车牌识别")
    @PostMapping("/plate")
    public Result<PlateRecognizeResponse> recognizePlate(@RequestParam("image") MultipartFile image) {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            PlateRecognizeResponse response = plateService.recognizePlate(imageBase64);
            
            if (response.getCode() == 200) {
                return Result.success("车牌识别成功", response);
            } else {
                return Result.error(response.getMsg());
            }
        } catch (Exception e) {
            return Result.error("车牌识别失败: " + e.getMessage());
        }
    }

    @ApiOperation("车辆信息查询")
    @GetMapping("/vehicle/{plateNumber}")
    public Result<VehicleInfoResponse> queryVehicleInfo(@PathVariable String plateNumber) {
        try {
            VehicleInfoResponse response = plateService.queryVehicleInfo(plateNumber);
            
            if (response.getCode() == 200) {
                return Result.success("车辆信息查询成功", response);
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e) {
            return Result.error("车辆信息查询失败: " + e.getMessage());
        }
    }
}