
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_flagstudio
-- ----------------------------
DROP TABLE IF EXISTS `tb_flagstudio`;
CREATE TABLE `tb_flagstudio` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `api_key` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'apikey',
                                 `token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '根据apikey生成的token,30天生成一次',
                                 `token_time` datetime DEFAULT NULL COMMENT 'token过期时间',
                                 `status` int DEFAULT NULL COMMENT '状态(0开启1关闭)',
                                 `create_time` datetime DEFAULT NULL COMMENT '添加时间',
                                 `day_count` int DEFAULT NULL COMMENT '每日次数(每日一个账号只能画500张图)',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1663816359227772930 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='FS绘画配置';

SET FOREIGN_KEY_CHECKS = 1;
-- 菜单初始SQL
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `url`, `permissions`, `menu_type`, `icon`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES (1663798428490035201, 0, 'FS绘画管理', '', '', 0, 'icon-save', 9, 1067246875800000001, '2023-05-31 14:43:52', 1067246875800000001, '2023-05-31 14:44:15');
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date)VALUES (1663797255951425537, 1663798428490035201, 'FS绘画配置', 'flagstudio/flagstudio', NULL, 0, 'icon-desktop', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1663797255951425538, 1663797255951425537, '查看', NULL, 'flagstudio:flagstudio:page,flagstudio:flagstudio:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1663797255951425539, 1663797255951425537, '新增', NULL, 'flagstudio:flagstudio:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1663797255951425540, 1663797255951425537, '修改', NULL, 'flagstudio:flagstudio:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1663797255951425541, 1663797255951425537, '删除', NULL, 'flagstudio:flagstudio:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1663797255951425542, 1663797255951425537, '导出', NULL, 'flagstudio:flagstudio:export', 1, NULL, 4, 1067246875800000001, now(), 1067246875800000001, now());

-- 参数表添加参数
INSERT INTO `sys_params` (`id`, `param_code`, `param_value`, `param_type`, `remark`, `creator`, `create_date`, `updater`, `update_date`) VALUES (1664279425438478338, 'is_open_sample', '1', 1, '是否开启百度内容审核,0是开启1是关闭', 1067246875800000001, '2023-06-01 22:35:10', 1067246875800000001, '2023-06-01 22:35:10');
INSERT INTO `sys_params` (`id`, `param_code`, `param_value`, `param_type`, `remark`, `creator`, `create_date`, `updater`, `update_date`) VALUES (1664279535866114049, 'baidu_apiKey', 'key', 1, '百度内容审核使用的key', 1067246875800000001, '2023-06-01 22:35:37', 1067246875800000001, '2023-06-01 22:35:37');
INSERT INTO `sys_params` (`id`, `param_code`, `param_value`, `param_type`, `remark`, `creator`, `create_date`, `updater`, `update_date`) VALUES (1664279653562478594, 'baidu_secretKey', 'secret', 1, '百度内容审核使用的secret', 1067246875800000001, '2023-06-01 22:36:05', 1067246875800000001, '2023-06-01 22:36:05');

-- 定时器表数据
INSERT INTO `schedule_job` (`id`, `bean_name`, `params`, `cron_expression`, `status`, `remark`, `creator`, `create_date`, `updater`, `update_date`) VALUES (1664552079437361154, 'FlagStudiDayCount', '', '0 0 0 * * ? ', 1, 'FS每日绘画次数归零', 1067246875800000001, '2023-06-02 16:38:36', 1067246875800000001, '2023-06-02 16:38:36');