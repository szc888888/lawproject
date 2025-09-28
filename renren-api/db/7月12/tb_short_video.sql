/*
 Navicat Premium Data Transfer

 Source Server         : 生产GPT
 Source Server Type    : MySQL
 Source Server Version : 80024 (8.0.24)
 Source Host           : 43.139.95.94:3306
 Source Schema         : chatgpt

 Target Server Type    : MySQL
 Target Server Version : 80024 (8.0.24)
 File Encoding         : 65001

 Date: 12/07/2023 17:50:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_short_video
-- ----------------------------
DROP TABLE IF EXISTS `tb_short_video`;
CREATE TABLE `tb_short_video` (
  `id` bigint NOT NULL COMMENT 'id',
  `video_url` longtext COLLATE utf8mb4_general_ci COMMENT '视频地址',
  `img_url` longtext COLLATE utf8mb4_general_ci COMMENT '封面图片',
  `voice_url` longtext COLLATE utf8mb4_general_ci COMMENT 'BMG地址',
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '视频标题',
  `content` longtext COLLATE utf8mb4_general_ci COMMENT '视频内容文案',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '拉取时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='短视频拉取数据';

SET FOREIGN_KEY_CHECKS = 1;
