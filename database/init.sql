-- 驾校场地监管系统数据库初始化脚本

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS driving_school DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE driving_school;

-- ----------------------------
-- 1. 学员信息表
-- ----------------------------
DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '学员ID',
  student_name VARCHAR(50) NOT NULL COMMENT '学员姓名',
  id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
  phone VARCHAR(20) COMMENT '联系电话',
  student_status TINYINT(1) DEFAULT 1 COMMENT '状态：1-在读，2-已结业，3-无效',
  face_image_url VARCHAR(255) COMMENT '人脸照片URL',
  enrollment_date DATE COMMENT '入学日期',
  graduation_date DATE COMMENT '结业日期',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (id),
  INDEX idx_id_card (id_card),
  INDEX idx_student_status (student_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员信息表';

-- ----------------------------
-- 2. 教练信息表
-- ----------------------------
DROP TABLE IF EXISTS coach;
CREATE TABLE coach (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '教练ID',
  coach_name VARCHAR(50) NOT NULL COMMENT '教练姓名',
  coach_license VARCHAR(50) NOT NULL UNIQUE COMMENT '教练证号',
  id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
  phone VARCHAR(20) COMMENT '联系电话',
  coach_status TINYINT(1) DEFAULT 1 COMMENT '状态：1-在职，2-离职',
  face_image_url VARCHAR(255) COMMENT '人脸照片URL',
  hire_date DATE COMMENT '入职日期',
  leave_date DATE COMMENT '离职日期',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (id),
  INDEX idx_coach_license (coach_license),
  INDEX idx_coach_status (coach_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练信息表';

-- ----------------------------
-- 3. 车辆信息表
-- ----------------------------
DROP TABLE IF EXISTS car;
CREATE TABLE car (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  license_plate VARCHAR(20) NOT NULL UNIQUE COMMENT '车牌号',
  car_number VARCHAR(50) COMMENT '车辆编号',
  coach_id BIGINT(20) COMMENT '所属教练ID',
  car_status TINYINT(1) DEFAULT 1 COMMENT '状态：1-可用，2-禁用',
  brand VARCHAR(50) COMMENT '品牌',
  model VARCHAR(50) COMMENT '型号',
  purchase_date DATE COMMENT '购买日期',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (id),
  INDEX idx_license_plate (license_plate),
  INDEX idx_coach_id (coach_id),
  INDEX idx_car_status (car_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- ----------------------------
-- 4. 场地区域表
-- ----------------------------
DROP TABLE IF EXISTS site_area;
CREATE TABLE site_area (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '区域ID',
  area_name VARCHAR(100) NOT NULL COMMENT '区域名称',
  area_type TINYINT(1) COMMENT '区域类型：1-科二，2-科三',
  project_type VARCHAR(50) COMMENT '项目类型',
  responsible_coach_id BIGINT(20) COMMENT '负责教练ID',
  responsible_car_id BIGINT(20) COMMENT '负责车辆ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (id),
  INDEX idx_area_type (area_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地区域表';

-- ----------------------------
-- 5. 教练排班表
-- ----------------------------
DROP TABLE IF EXISTS coach_schedule;
CREATE TABLE coach_schedule (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  coach_id BIGINT(20) NOT NULL COMMENT '教练ID',
  area_id BIGINT(20) COMMENT '区域ID',
  schedule_date DATE NOT NULL COMMENT '排班日期',
  start_time TIME NOT NULL COMMENT '开始时间',
  end_time TIME NOT NULL COMMENT '结束时间',
  car_id BIGINT(20) COMMENT '使用车辆ID',
  schedule_status TINYINT(1) DEFAULT 1 COMMENT '排班状态：1-正常，2-取消',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_coach_id (coach_id),
  INDEX idx_schedule_date (schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练排班表';

-- ----------------------------
-- 6. 违规记录表
-- ----------------------------
DROP TABLE IF EXISTS illegal_record;
CREATE TABLE illegal_record (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '违规记录ID',
  illegal_time DATETIME NOT NULL COMMENT '违规时间',
  area_id BIGINT(20) COMMENT '违规区域ID',
  person_type TINYINT(1) COMMENT '人员类型：1-学员，2-教练',
  person_id BIGINT(20) COMMENT '人员ID',
  license_plate VARCHAR(20) COMMENT '车牌号',
  illegal_type VARCHAR(50) NOT NULL COMMENT '违规类型',
  illegal_desc TEXT COMMENT '违规描述',
  evidence_urls TEXT COMMENT '证据图片URL（JSON格式）',
  warning_status TINYINT(1) DEFAULT 1 COMMENT '预警状态：1-未处理，2-已处理，3-已追责',
  handler_id BIGINT(20) COMMENT '处理人ID',
  handle_time DATETIME COMMENT '处理时间',
  handle_remark TEXT COMMENT '处理备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_illegal_time (illegal_time),
  INDEX idx_person_id (person_id),
  INDEX idx_warning_status (warning_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违规记录表';

-- ----------------------------
-- 7. 用户权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '联系电话',
  role_type TINYINT(1) NOT NULL COMMENT '角色类型：1-管理员，2-场控负责人，3-普通操作员',
  user_status TINYINT(1) DEFAULT 1 COMMENT '用户状态：1-启用，2-禁用',
  last_login_time DATETIME COMMENT '最后登录时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (id),
  INDEX idx_username (username),
  INDEX idx_role_type (role_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 8. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
  config_value TEXT COMMENT '配置值',
  config_desc VARCHAR(255) COMMENT '配置描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入初始数据
-- 初始管理员账户
INSERT INTO sys_user (username, password, real_name, role_type, user_status) 
VALUES ('admin', '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', '系统管理员', 1, 1);

-- 系统配置
INSERT INTO sys_config (config_key, config_value, config_desc) VALUES
('system.name', '驾校场地监管系统', '系统名称'),
('face.verify.threshold', '0.85', '人脸识别阈值'),
('car.verify.enabled', 'true', '车牌识别是否启用'),
('warning.sound.enabled', 'true', '预警声音是否启用');

SET FOREIGN_KEY_CHECKS = 1;