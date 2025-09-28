DROP TABLE IF EXISTS `tb_draw_record`;
CREATE TABLE `tb_draw_record` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `img_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '图片地址',
                                  `type` int DEFAULT NULL COMMENT '画图类型(0GPT1SD2MJ)',
                                  `create_time` datetime DEFAULT NULL COMMENT '画图时间',
                                  `prompt` longtext COLLATE utf8mb4_general_ci COMMENT '正向提示词(GPT绘画就只有这个)',
                                  `negative_prompt` longtext COLLATE utf8mb4_general_ci COMMENT '反向提示词',
                                  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1659454201416380419 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='画图记录';



-- 添加数据到参数表
INSERT INTO `sys_params` VALUES ('1659536826391621634', 'sd_img_secret', 'key', '1', 'SD绘画秘钥', '1067246875800000001', '2023-05-19 20:29:47', '1067246875800000001', '2023-05-22 23:06:37');
INSERT INTO `sys_params` VALUES ('1659536896176451586', 'sd_img_url', 'http://127.0.0.1:7860/sdapi/v1/txt2img', '1', 'SD绘画的接口', '1067246875800000001', '2023-05-19 20:30:03', '1067246875800000001', '2023-05-22 23:06:55');
INSERT INTO `sys_params` VALUES ('1659536896176451323', 'sd_img_price', '5', '1', 'SD绘画价钱', '1067246875800000001', '2023-05-19 20:30:03', '1067246875800000001', '2023-05-22 23:06:55');
INSERT INTO `sys_params` VALUES ('1659747623407480833', 'mp_wx_wechat_token', '公众号token', '1', '公众号token', '1067246875800000001', '2023-05-20 10:27:25', '1067246875800000001', '2023-05-20 10:27:45');
INSERT INTO `sys_params` VALUES ('1659747775245479937', 'mp_wx_wechat_encodingaeskey', '公众号授权秘钥', '1', '公众号授权秘钥', '1067246875800000001', '2023-05-20 10:28:01', '1067246875800000001', '2023-05-20 10:28:01');
-- 菜单初始SQL
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date)VALUES (1659406661042614273, 1067246875800000035, '画图记录', 'drawRecord/drawrecord', NULL, 0, 'icon-desktop', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1659406661042614274, 1659406661042614273, '查看', NULL, 'drawRecord:drawrecord:page,drawRecord:drawrecord:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1659406661042614275, 1659406661042614273, '新增', NULL, 'drawRecord:drawrecord:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1659406661042614276, 1659406661042614273, '修改', NULL, 'drawRecord:drawrecord:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1659406661042614277, 1659406661042614273, '删除', NULL, 'drawRecord:drawrecord:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, name, url, permissions, menu_type, icon, sort, creator, create_date, updater, update_date) VALUES (1659406661042614278, 1659406661042614273, '导出', NULL, 'drawRecord:drawrecord:export', 1, NULL, 4, 1067246875800000001, now(), 1067246875800000001, now());
