/*
 Navicat Premium Data Transfer

 Source Server         : 生产GPT
 Source Server Type    : MySQL
 Source Server Version : 80024 (8.0.24)
 Source Host           : 127.0.0.1:3306
 Source Schema         : chatgpt

 Target Server Type    : MySQL
 Target Server Version : 80024 (8.0.24)
 File Encoding         : 65001

 Date: 12/06/2023 23:29:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_pdf_doc
-- ----------------------------
DROP TABLE IF EXISTS `tb_pdf_doc`;
CREATE TABLE `tb_pdf_doc` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `file_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名',
  `type` int DEFAULT NULL COMMENT '类型(0是PDF 1doc)',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='PDF和DOC表';

SET FOREIGN_KEY_CHECKS = 1;
