/*
 Navicat Premium Dump SQL

 Source Server         : 8.0
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : travel

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 25/02/2026 22:48:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `status` tinyint DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `role_id`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', NULL, NULL, NULL, NULL, 1, '2025-12-14 19:08:16', '2025-12-13 00:21:50', '2025-12-13 00:21:50');
COMMIT;

-- ----------------------------
-- Table structure for attraction
-- ----------------------------
DROP TABLE IF EXISTS `attraction`;
CREATE TABLE `attraction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `scenic_id` bigint DEFAULT NULL COMMENT '所属景区ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- ----------------------------
-- Records of attraction
-- ----------------------------
BEGIN;
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (1, 1, '天安门广场', '世界上最大的城市广场，中国的象征性建筑之一', '/uploads/seed_082.jpg', '北京', '北京市', 116.397428, 39.909230, 15680, 2340, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (2, 1, '故宫博物院', '中国明清两代的皇家宫殿，世界上现存规模最大的宫殿型建筑', '/uploads/seed_083.jpg', '北京', '北京市', 116.403414, 39.924091, 28902, 5670, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (3, 1, '颐和园', '中国清朝时期皇家园林，被誉为皇家园林博物馆', '/uploads/seed_084.jpg', '北京', '北京市', 116.275000, 39.999000, 12300, 1890, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (4, 2, '外滩', '上海的标志性景点，欣赏黄浦江两岸风光', '/uploads/seed_023.jpg', '上海', '上海市', 121.490317, 31.240018, 18700, 3210, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (5, 2, '东方明珠', '上海地标性建筑，亚洲第一高塔', '/uploads/seed_085.jpg', '上海', '上海市', 121.499809, 31.239666, 22100, 4560, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (6, 2, '豫园', '江南古典园林，上海著名的古迹', '/uploads/seed_086.jpg', '上海', '上海市', 121.492137, 31.227362, 9800, 1230, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (7, 3, '西湖', '中国十大风景名胜之一，世界文化遗产', '/uploads/seed_087.jpg', '浙江', '杭州市', 120.148732, 30.242489, 35605, 7890, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (8, 3, '灵隐寺', '杭州最著名的佛教寺院', '/uploads/seed_014.jpg', '浙江', '杭州市', 120.101000, 30.241000, 14500, 2340, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (9, 3, '雷峰塔', '西湖十景之一，白蛇传的传说发生地', '/uploads/seed_015.jpg', '浙江', '杭州市', 120.149000, 30.231000, 11200, 1780, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (12, 4, '华清池', '唐代皇家温泉宫殿', '/uploads/seed_088.jpg', '陕西', '西安市', 109.213000, 34.365000, 13400, 2100, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (13, 5, '珠江夜游', '欣赏广州璀璨夜景的最佳方式', '/uploads/seed_024.jpg', '广东', '广州市', 113.264385, 23.129110, 16700, 2890, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (14, 5, '广州塔', '广州新地标，小蛮腰', '/uploads/seed_089.jpg', '广东', '广州市', 113.324000, 23.106000, 21500, 4120, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (15, 5, '陈家祠', '岭南建筑艺术的瑰宝', '/uploads/seed_001.jpg', '广东', '广州市', 113.245000, 23.125000, 8900, 1340, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (16, 6, '洪崖洞', '重庆最具特色的吊脚楼建筑群', '/uploads/seed_084.jpg', '重庆', '重庆市', 106.584000, 29.563000, 28701, 5670, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (17, 6, '解放碑', '重庆的标志性建筑和商业中心', '/uploads/seed_088.jpg', '重庆', '重庆市', 106.578000, 29.558000, 19800, 3210, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (18, 6, '长江索道', '重庆独特的过江交通工具', '/uploads/seed_086.jpg', '重庆', '重庆市', 106.586000, 29.557000, 15600, 2780, '2025-12-13 18:01:33');
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES (20, 7, '锦里古街', '成都著名的古街，体验巴蜀文化', '/uploads/seed_088.jpg', '四川', '成都市', 104.048000, 30.645000, 21700, 3890, '2025-12-13 18:01:33');
COMMIT;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片地址',
  `link_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转链接',
  `link_type` tinyint DEFAULT '0' COMMENT '链接类型：0无跳转 1景区 2攻略 3外链',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- ----------------------------
-- Records of banner
-- ----------------------------
BEGIN;
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES (1, '探索张家界 感受大自然的鬼斧神工', '/uploads/seed_093.jpg', NULL, 1, 3, 1, 1, '2025-12-13 15:53:08', '2025-12-13 15:53:08');
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES (2, '杭州西湖 人间天堂', '/uploads/seed_094.jpg', NULL, 1, 2, 2, 1, '2025-12-13 15:53:08', '2025-12-13 15:53:08');
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES (3, '北京故宫 穿越六百年历史', '/uploads/seed_095.jpg', NULL, 1, 1, 3, 1, '2025-12-13 15:53:08', '2025-12-13 15:53:08');
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES (4, '成都美食之旅 舌尖上的天府', '/uploads/seed_096.jpg', NULL, 2, 4, 4, 1, '2025-12-13 15:53:08', '2025-12-13 15:53:08');
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES (5, '春季特惠 门票低至5折', '/uploads/seed_097.jpg', NULL, 0, NULL, 5, 1, '2025-12-13 15:53:08', '2025-12-13 15:53:08');
COMMIT;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` tinyint DEFAULT '0' COMMENT '消息类型：0文本 1图片 2文件',
  `status` tinyint DEFAULT '0' COMMENT '状态：0未读 1已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- ----------------------------
-- Records of chat_message
-- ----------------------------
BEGIN;
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `type`, `status`, `create_time`) VALUES (1, 12, 11, '你好', 0, 1, '2025-12-13 21:52:04');
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `type`, `status`, `create_time`) VALUES (2, 11, 12, '你好啊', 0, 1, '2025-12-13 21:55:34');
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `type`, `status`, `create_time`) VALUES (3, 12, 11, '好', 0, 1, '2025-12-13 22:00:20');
INSERT INTO `chat_message` (`id`, `sender_id`, `receiver_id`, `content`, `type`, `status`, `create_time`) VALUES (4, 11, 12, '你好', 0, 0, '2025-12-14 18:14:08');
COMMIT;

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '对方ID',
  `last_message` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后一条消息',
  `unread_count` int DEFAULT '0' COMMENT '未读消息数',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- ----------------------------
-- Records of chat_session
-- ----------------------------
BEGIN;
INSERT INTO `chat_session` (`id`, `user_id`, `target_id`, `last_message`, `unread_count`, `update_time`, `create_time`) VALUES (1, 12, 11, '你好', 2, '2025-12-14 18:14:08', '2025-12-13 21:52:04');
INSERT INTO `chat_session` (`id`, `user_id`, `target_id`, `last_message`, `unread_count`, `update_time`, `create_time`) VALUES (2, 11, 12, '你好', 1, '2025-12-14 18:14:08', '2025-12-13 21:52:04');
COMMIT;

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `type` tinyint NOT NULL COMMENT '类型：1景区 2攻略 3行程 4美食 5景点',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ----------------------------
-- Records of collection
-- ----------------------------
BEGIN;
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (1, 11, 1, 1, '2025-12-13 16:11:29');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (2, 11, 5, 1, '2025-12-13 16:19:16');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (3, 11, 1, 2, '2025-12-13 16:23:58');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (4, 11, 7, 1, '2025-12-13 20:02:28');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (5, 11, 6, 2, '2025-12-13 20:13:57');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (6, 11, 2, 1, '2025-12-13 20:38:51');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (7, 11, 2, 3, '2025-12-13 21:01:06');
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES (8, 11, 1, 4, '2025-12-13 21:28:44');
COMMIT;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型：attraction-景点, scenic-景区, strategy-攻略, note-游记',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-删除 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通用评论表';

-- ----------------------------
-- Records of comment
-- ----------------------------
BEGIN;
INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES (1, 7, 'attraction', 11, '好的很', NULL, 0, 1, '2025-12-13 20:13:48');
INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES (2, 6, 'strategy', 11, '不错', NULL, 0, 1, '2025-12-13 20:14:03');
INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES (3, 2, 'scenic', 11, '真不错', NULL, 0, 1, '2025-12-14 18:12:49');
COMMIT;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容',
  `reply` text COLLATE utf8mb4_unicode_ci COMMENT '回复内容',
  `status` tinyint DEFAULT '0' COMMENT '状态：0待处理 1已回复',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- ----------------------------
-- Records of feedback
-- ----------------------------
BEGIN;
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (1, 1, '网站整体体验很好，景区信息很详细，门票预订也很方便。希望能增加更多的景区攻略内容。', '感谢您的反馈！我们会持续丰富攻略内容，为您提供更好的旅行参考。', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (2, 2, '建议增加景区实时人流量显示功能，方便游客错峰出行。', '非常好的建议！我们已将此需求纳入开发计划，预计下个版本上线。', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (3, 3, '门票支付流程很顺畅，但希望能支持更多支付方式，比如云闪付。', '感谢您的建议，我们正在对接更多支付渠道，敬请期待！', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (4, 4, '地图导航功能很实用，但有时候定位不太准确，希望能优化一下。', '感谢反馈，我们会优化地图定位精度，提升用户体验。', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (5, 5, '希望能增加语音导览功能，这样游览景区时更方便。', NULL, 0, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (6, 6, '网站加载速度有时候比较慢，特别是图片加载。', NULL, 0, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (7, 7, '攻略内容很丰富，对我的旅行规划帮助很大，感谢！', '感谢您的认可，我们会继续努力提供优质内容！', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (8, 8, '建议增加景区周边酒店和餐厅推荐功能。', '好建议！我们正在规划周边服务模块，感谢您的反馈。', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (9, 9, '客服响应很快，问题解决得很及时，点赞！', '感谢您的肯定，我们会继续保持优质服务！', 1, '2025-12-13 11:14:46');
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES (10, 10, '希望能增加亲子游、老年游等特色路线推荐。', NULL, 0, '2025-12-13 11:14:46');
COMMIT;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `follow_user_id` bigint NOT NULL COMMENT '被关注用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`user_id`,`follow_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- ----------------------------
-- Records of follow
-- ----------------------------
BEGIN;
INSERT INTO `follow` (`id`, `user_id`, `follow_user_id`, `create_time`) VALUES (2, 11, 12, '2025-12-13 21:52:26');
INSERT INTO `follow` (`id`, `user_id`, `follow_user_id`, `create_time`) VALUES (3, 12, 11, '2025-12-13 22:01:46');
COMMIT;

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '美食名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `user_id` bigint DEFAULT NULL COMMENT '发布者ID',
  `status` tinyint DEFAULT '1' COMMENT '状态：0待审核 1已审核',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='美食表';

-- ----------------------------
-- Records of food
-- ----------------------------
BEGIN;
INSERT INTO `food` (`id`, `name`, `description`, `cover_image`, `province`, `city`, `address`, `create_time`, `user_id`, `status`) VALUES (1, '北京烤鸭', '王府井大街200号', '/uploads/30cc4c2f9b9443838c6935edb79a11c4.jpeg', '北京', '城关区', '王府井大街200号', '2025-12-13 21:28:23', 11, 1);
INSERT INTO `food` (`id`, `name`, `description`, `cover_image`, `province`, `city`, `address`, `create_time`, `user_id`, `status`) VALUES (2, '兰州牛肉面', '测试', '/uploads/a085c5bda6c04834996f8e99b11e81e4.jpg', '甘肃', '兰州市', '城关区', '2025-12-13 21:31:22', 12, 1);
COMMIT;

-- ----------------------------
-- Table structure for note_comment
-- ----------------------------
DROP TABLE IF EXISTS `note_comment`;
CREATE TABLE `note_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `note_id` bigint NOT NULL COMMENT '游记ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，用于回复',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-删除 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_note_id` (`note_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游记评论表';

-- ----------------------------
-- Records of note_comment
-- ----------------------------
BEGIN;
INSERT INTO `note_comment` (`id`, `note_id`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES (1, 2, 11, '好的', NULL, 0, 1, '2025-12-13 17:49:32');
COMMIT;

-- ----------------------------
-- Table structure for note_like
-- ----------------------------
DROP TABLE IF EXISTS `note_like`;
CREATE TABLE `note_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `note_id` bigint NOT NULL COMMENT '游记ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_note_user` (`note_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游记点赞表';

-- ----------------------------
-- Records of note_like
-- ----------------------------
BEGIN;
INSERT INTO `note_like` (`id`, `note_id`, `user_id`, `create_time`) VALUES (1, 2, 11, '2025-12-13 17:49:35');
COMMIT;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scenic_id` bigint NOT NULL COMMENT '景区ID',
  `ticket_id` bigint NOT NULL COMMENT '门票ID',
  `quantity` int NOT NULL COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `visit_date` date NOT NULL COMMENT '游玩日期',
  `status` tinyint DEFAULT '0' COMMENT '状态：0待支付 1已支付 2已使用 3已取消 4已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
  `alipay_trade_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付宝交易号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------
BEGIN;
INSERT INTO `order` (`id`, `order_no`, `user_id`, `scenic_id`, `ticket_id`, `quantity`, `total_amount`, `visit_date`, `status`, `pay_time`, `pay_type`, `alipay_trade_no`, `create_time`) VALUES (1, 'T1765680346705565D', 12, 2, 4, 2, 80.00, '2025-12-14', 2, '2025-12-14 10:53:07', 'alipay', NULL, '2025-12-14 10:45:47');
INSERT INTO `order` (`id`, `order_no`, `user_id`, `scenic_id`, `ticket_id`, `quantity`, `total_amount`, `visit_date`, `status`, `pay_time`, `pay_type`, `alipay_trade_no`, `create_time`) VALUES (2, 'T1765707182058C9EE', 11, 2, 6, 2, 280.00, '2025-12-14', 2, '2025-12-14 18:13:18', 'alipay', NULL, '2025-12-14 18:13:02');
INSERT INTO `order` (`id`, `order_no`, `user_id`, `scenic_id`, `ticket_id`, `quantity`, `total_amount`, `visit_date`, `status`, `pay_time`, `pay_type`, `alipay_trade_no`, `create_time`) VALUES (3, 'T17720306359795D6A', 11, 1, 1, 1, 60.00, '2026-02-25', 1, '2026-02-25 22:44:53', '支付宝', '2026022522001481040508446447', '2026-02-25 22:43:56');
COMMIT;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `type` tinyint DEFAULT NULL COMMENT '类型：1菜单 2按钮',
  `parent_id` bigint DEFAULT '0' COMMENT '父级ID',
  `path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径',
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ----------------------------
-- Records of permission
-- ----------------------------
BEGIN;
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES (1, '系统管理', 'system', 1, 0, '/system', 'setting', 1, 1);
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES (2, '用户管理', 'user:manage', 1, 1, '/system/user', 'user', 1, 1);
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES (3, '景区管理', 'scenic:manage', 1, 1, '/system/scenic', 'picture', 2, 1);
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES (4, '订单管理', 'order:manage', 1, 1, '/system/order', 'shopping-cart', 3, 1);
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES (5, '管理员管理', 'admin:manage', 1, 1, '/system/admin', 'team', 4, 1);
COMMIT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` (`id`, `name`, `code`, `description`, `status`, `create_time`) VALUES (1, '超级管理员', 'super_admin', '拥有所有权限', 1, '2025-12-13 00:21:50');
INSERT INTO `role` (`id`, `name`, `code`, `description`, `status`, `create_time`) VALUES (2, '普通管理员', 'admin', '拥有部分管理权限', 1, '2025-12-13 00:21:50');
COMMIT;

-- ----------------------------
-- Table structure for scenic_area
-- ----------------------------
DROP TABLE IF EXISTS `scenic_area`;
CREATE TABLE `scenic_area` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '景区ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景区名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '景区描述',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '图片集(JSON)',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `open_time` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开放时间',
  `level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '景区等级(5A/4A等)',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `collect_count` int DEFAULT '0' COMMENT '收藏数',
  `price` decimal(10,2) DEFAULT NULL COMMENT '门票价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景区表';

-- ----------------------------
-- Records of scenic_area
-- ----------------------------
BEGIN;
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (1, '故宫博物院', '<p>北京故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。是世界上现存规模最大、保存最为完整的木质结构古建筑之一。</p>', '北京市东城区景山前街4号', '北京', '北京', '/uploads/de4ac58094374a3abb45ac80cf7b98a4.jpeg', NULL, 116.397026, 39.918058, '08:30-17:00', '5A', 1, '2025-12-13 11:06:37', '2026-02-25 22:43:51', 15682, 2341, 60.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (2, '长城-八达岭', '八达岭长城是中国古代伟大的防御工程万里长城的重要组成部分，是明长城的一个隘口。八达岭长城为居庸关的重要前哨，古称居庸之险不在关而在八达岭。', '北京市延庆区八达岭镇', '北京', '北京', '/uploads/seed_068.jpg', NULL, 116.024067, 40.359947, '06:30-19:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-14 18:12:28', 23454, 4561, 40.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (3, '西湖风景区', '杭州西湖是中国大陆首批国家重点风景名胜区和中国十大风景名胜之一，被誉为人间天堂。西湖三面环山，面积约6.39平方千米。', '浙江省杭州市西湖区龙井路1号', '浙江', '杭州', '/uploads/seed_069.jpg', NULL, 120.148732, 30.242489, '全天开放', '5A', 1, '2025-12-13 11:06:37', '2025-12-14 18:14:21', 18921, 3210, 0.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (4, '黄山风景区', '黄山位于安徽省南部黄山市境内，有72峰，主峰莲花峰海拔1864米，与光明顶、天都峰并称三大黄山主峰，为36大峰之一。', '安徽省黄山市黄山区', '安徽', '黄山', '/uploads/seed_070.jpg', NULL, 118.337481, 30.131949, '06:00-17:30', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 16:06:10', 12341, 2890, 190.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (5, '张家界国家森林公园', '张家界国家森林公园是中国第一个国家森林公园，以独特的石英砂岩峰林地貌著称，被誉为扩大的盆景，缩小的仙境。', '湖南省张家界市武陵源区', '湖南', '张家界', '/uploads/seed_071.jpg', NULL, 110.479191, 29.325039, '07:00-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 16:22:43', 9871, 1981, 225.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (6, '九寨沟风景区', '九寨沟位于四川省阿坝藏族羌族自治州九寨沟县境内，是中国第一个以保护自然风景为主要目的的自然保护区。', '四川省阿坝藏族羌族自治州九寨沟县', '四川', '阿坝', '/uploads/seed_072.jpg', NULL, 103.918186, 33.262048, '08:00-17:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 8760, 1670, 169.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (7, '丽江古城', '丽江古城位于云南省丽江市古城区，又名大研镇，是中国以整座古城申报世界文化遗产获得成功的两座古城之一。', '云南省丽江市古城区', '云南', '丽江', '/uploads/seed_003.jpg', NULL, 100.233070, 26.872108, '全天开放', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 14560, 2781, 50.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (8, '桂林漓江风景区', '漓江风景区是世界上规模最大、风景最美的岩溶山水游览区，以桂林山水甲天下闻名于世。', '广西壮族自治区桂林市', '广西', '桂林', '/uploads/seed_073.jpg', NULL, 110.290195, 25.273566, '08:00-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 11230, 2340, 210.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (9, '鼓浪屿', '鼓浪屿位于厦门岛西南隅，与厦门岛隔海相望，是国家5A级旅游景区，全国重点文物保护单位。', '福建省厦门市思明区', '福建', '厦门', '/uploads/seed_011.jpg', NULL, 118.064882, 24.448231, '全天开放', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 16780, 3120, 0.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (10, '泰山风景区', '泰山位于山东省泰安市中部，主峰玉皇顶海拔1545米，气势雄伟磅礴，有五岳之首、天下第一山之称。', '山东省泰安市泰山区红门路', '山东', '泰安', '/uploads/seed_008.jpg', NULL, 117.129063, 36.256013, '全天开放', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 10890, 2190, 115.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (11, '兵马俑博物馆', '秦始皇兵马俑博物馆位于陕西省西安市临潼区，是秦始皇陵的陪葬坑，被誉为世界第八大奇迹。', '陕西省西安市临潼区秦陵北路', '陕西', '西安', '/uploads/seed_074.jpg', NULL, 109.278348, 34.384431, '08:30-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 13450, 2670, 120.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (12, '峨眉山风景区', '峨眉山位于四川省乐山市峨眉山市境内，是中国四大佛教名山之一，地势陡峭，风景秀丽，素有峨眉天下秀之称。', '四川省乐山市峨眉山市', '四川', '乐山', '/uploads/seed_075.jpg', NULL, 103.484478, 29.520420, '06:00-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 8920, 1780, 160.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (13, '华山风景区', '华山位于陕西省渭南市华阴市，是中国著名的五岳之一，有奇险天下第一山的美誉。', '陕西省渭南市华阴市华山镇', '陕西', '渭南', '/uploads/seed_005.jpg', NULL, 110.089027, 34.474428, '全天开放', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 7650, 1560, 160.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (14, '普陀山风景区', '普陀山位于浙江省舟山市普陀区，是中国佛教四大名山之一，素有海天佛国、南海圣境之称。', '浙江省舟山市普陀区', '浙江', '舟山', '/uploads/seed_076.jpg', NULL, 122.385433, 29.996950, '06:00-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 6780, 1340, 160.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (15, '武夷山风景区', '武夷山位于福建省武夷山市南郊，是中国著名的风景旅游区和避暑胜地，属典型的丹霞地貌。', '福建省南平市武夷山市', '福建', '南平', '/uploads/seed_077.jpg', NULL, 117.966667, 27.716667, '07:30-17:30', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 5890, 1120, 140.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (16, '颐和园', '颐和园位于北京市海淀区，是中国清朝时期皇家园林，前身为清漪园，是保存最完整的一座皇家行宫御苑。', '北京市海淀区新建宫门路19号', '北京', '北京', '/uploads/seed_078.jpg', NULL, 116.275045, 39.999615, '06:30-18:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 12340, 2450, 30.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (17, '天坛公园', '天坛位于北京市南部，是明清两代皇帝祭祀皇天、祈五谷丰登之场所，是中国现存最大的古代祭祀性建筑群。', '北京市东城区天坛东里甲1号', '北京', '北京', '/uploads/seed_079.jpg', NULL, 116.410886, 39.881954, '06:00-21:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 9870, 1890, 15.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (18, '乐山大佛', '乐山大佛位于四川省乐山市南岷江东岸凌云寺侧，是中国最大的一尊摩崖石刻造像，被誉为山是一尊佛，佛是一座山。', '四川省乐山市市中区凌云路', '四川', '乐山', '/uploads/seed_002.jpg', NULL, 103.773438, 29.544530, '07:30-18:30', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 8450, 1670, 80.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (19, '布达拉宫', '布达拉宫位于西藏自治区拉萨市区西北玛布日山上，是世界上海拔最高、最雄伟的宫殿，是拉萨乃至西藏最重要的象征。', '西藏自治区拉萨市城关区北京中路35号', '西藏', '拉萨', '/uploads/seed_080.jpg', NULL, 91.117525, 29.655755, '09:00-16:00', '5A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 7890, 1560, 200.00);
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES (20, '外滩', '外滩位于上海市黄浦区的黄浦江畔，是上海的标志性景观，被誉为万国建筑博览群。', '上海市黄浦区中山东一路', '上海', '上海', '/uploads/seed_081.jpg', NULL, 121.490317, 31.240018, '全天开放', '4A', 1, '2025-12-13 11:06:37', '2025-12-13 11:06:37', 19870, 3890, 0.00);
COMMIT;

-- ----------------------------
-- Table structure for strategy
-- ----------------------------
DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '攻略ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '0' COMMENT '状态：0待审核 1已发布 2已拒绝',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='攻略表';

-- ----------------------------
-- Records of strategy
-- ----------------------------
BEGIN;
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (1, 1, '北京三日游完整攻略', '第一天：故宫+天安门广场+王府井\n早上8点到达故宫，建议从午门进入，游览时间约4-5小时。中午在故宫附近用餐，下午游览天安门广场，傍晚前往王府井步行街购物和品尝小吃。\n\n第二天：长城+明十三陵\n建议早起前往八达岭长城，避开人流高峰。下午参观明十三陵，感受皇家陵寝的庄严。\n\n第三天：颐和园+圆明园\n上午游览颐和园，欣赏皇家园林美景。下午参观圆明园遗址，了解历史。', '/uploads/seed_091.jpg', '北京,三日游,故宫,长城', 3256, 428, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (2, 2, '杭州西湖一日游最佳路线', '西湖一日游推荐路线：\n\n上午：断桥残雪→白堤→孤山→西泠印社→曲院风荷\n从断桥开始，沿白堤漫步，欣赏湖光山色。\n\n中午：楼外楼品尝杭帮菜\n推荐西湖醋鱼、东坡肉、龙井虾仁。\n\n下午：苏堤春晓→花港观鱼→雷峰塔→南屏晚钟\n苏堤是西湖最美的堤岸，雷峰塔可俯瞰西湖全景。\n\n傍晚：乘船游湖，欣赏三潭印月', '/uploads/seed_069.jpg', '杭州,西湖,一日游,美食', 2890, 356, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (3, 3, '张家界自由行五日攻略', 'Day1：抵达张家界，入住市区酒店\nDay2：张家界国家森林公园（黄石寨+金鞭溪）\nDay3：袁家界+杨家界（天子山）\nDay4：天门山玻璃栈道\nDay5：凤凰古城一日游\n\n必备物品：舒适的登山鞋、防晒霜、雨具\n门票建议：提前在网上预订，可享受优惠\n住宿推荐：武陵源景区内有农家乐，方便游玩', '/uploads/seed_071.jpg', '张家界,自由行,五日游,天门山', 1876, 234, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (4, 4, '成都美食之旅三日攻略', '成都，一座来了就不想走的城市！\n\nDay1：宽窄巷子+锦里+武侯祠\n品尝：龙抄手、钟水饺、担担面\n\nDay2：大熊猫繁育研究基地+春熙路\n早起看大熊猫最活跃，下午逛春熙路\n品尝：火锅（推荐小龙坎、大龙燚）\n\nDay3：都江堰+青城山\n世界文化遗产，感受道教文化\n品尝：青城山老腊肉、白果炖鸡', '/uploads/seed_092.jpg', '成都,美食,三日游,火锅,熊猫', 4521, 567, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (5, 5, '云南大理丽江七日深度游', '第1-2天：昆明\n石林、滇池、翠湖公园\n\n第3-4天：大理\n洱海环湖、双廊古镇、苍山、崇圣寺三塔\n\n第5-7天：丽江\n丽江古城、玉龙雪山、蓝月谷、束河古镇\n\n交通建议：昆明-大理火车约2小时，大理-丽江大巴约3小时\n最佳季节：3-5月、9-11月，避开雨季和黄金周', '/uploads/seed_003.jpg', '云南,大理,丽江,七日游,洱海', 3654, 478, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (6, 6, '西安古都文化四日游', 'Day1：兵马俑+华清宫\n世界第八大奇迹，震撼人心\n\nDay2：西安城墙+回民街+钟鼓楼\n骑行城墙，品尝回民街美食\n\nDay3：陕西历史博物馆+大雁塔+大唐不夜城\n了解十三朝古都历史，夜游大唐不夜城\n\nDay4：华山一日游\n奇险天下第一山，建议乘索道上山', '/uploads/seed_074.jpg', '西安,兵马俑,四日游,美食,华山', 2345, 312, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (7, 7, '厦门鼓浪屿浪漫三日游', 'Day1：厦门大学+南普陀寺+曾厝垵\n中国最美大学，文艺小渔村\n\nDay2：鼓浪屿一日游\n日光岩、菽庄花园、钢琴博物馆、万国建筑\n\nDay3：环岛路骑行+中山路\n海边骑行，逛吃中山路\n\n美食推荐：沙茶面、海蛎煎、土笋冻、姜母鸭', '/uploads/seed_011.jpg', '厦门,鼓浪屿,三日游,文艺,海边', 2678, 345, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (8, 8, '桂林阳朔山水四日游', 'Day1：桂林市区\n象鼻山、两江四湖夜游\n\nDay2：漓江竹筏漂流\n桂林山水甲天下，漓江风光最迷人\n\nDay3：阳朔\n西街、遇龙河漂流、十里画廊骑行\n\nDay4：龙脊梯田\n壮观的梯田风光，体验少数民族文化', '/uploads/seed_073.jpg', '桂林,阳朔,四日游,漓江,山水', 1987, 256, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (9, 9, '上海都市风情三日游', 'Day1：外滩+南京路+城隍庙\n感受魔都的繁华与历史\n\nDay2：迪士尼乐园\n一整天的童话世界\n\nDay3：田子坊+新天地+东方明珠\n文艺与时尚的碰撞，登高俯瞰浦江两岸\n\n美食：生煎包、小笼包、本帮菜', '/uploads/seed_081.jpg', '上海,三日游,迪士尼,外滩,都市', 3123, 412, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (10, 10, '黄山日出观景两日攻略', 'Day1：\n早上从汤口出发，乘云谷索道上山\n游览始信峰、北海景区、西海大峡谷\n入住山顶酒店（建议提前预订）\n\nDay2：\n凌晨4点起床，前往光明顶看日出\n游览飞来石、迎客松\n乘玉屏索道下山\n\n必备：厚外套（山顶温差大）、登山杖、充足的水和食物', '/uploads/seed_070.jpg', '黄山,日出,两日游,登山,云海', 2456, 334, 1, '2025-12-13 11:11:50');
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES (11, 11, '我的攻略', '测试', '/uploads/7bebbc5a0b764af8a0146641cf87f677.jpeg', '美食', 0, 0, 1, '2025-12-14 18:13:56');
COMMIT;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `type` tinyint DEFAULT NULL COMMENT '类型',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统标签表';

-- ----------------------------
-- Records of tag
-- ----------------------------
BEGIN;
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (1, '自然风光', 1, 1, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (2, '人文古迹', 1, 2, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (3, '亲子游', 1, 3, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (4, '登山徒步', 1, 4, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (5, '城市观光', 1, 5, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (6, '海岛海滨', 1, 6, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (7, '北京', 2, 1, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (8, '上海', 2, 2, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (9, '杭州', 2, 3, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (10, '成都', 2, 4, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (11, '云南', 2, 5, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (12, '西藏', 2, 6, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (13, '新疆', 2, 7, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (14, '海南', 2, 8, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (15, '经典打卡', 3, 1, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (16, '轻松休闲', 3, 2, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (17, '深度人文', 3, 3, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (18, '美食为主', 3, 4, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (19, '周末短途', 3, 5, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (20, '家庭出行', 3, 6, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (21, '地方小吃', 4, 1, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (22, '特色餐厅', 4, 2, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (23, '火锅烧烤', 4, 3, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (24, '甜品饮品', 4, 4, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (25, '老字号', 4, 5, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (26, '夜市', 4, 6, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (27, '摄影打卡', 5, 1, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (28, '旅行故事', 5, 2, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (29, '避坑经验', 5, 3, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (30, '交通住宿', 5, 4, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (31, '小众路线', 5, 5, 1);
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES (32, '省钱攻略', 5, 6, 1);
COMMIT;

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '门票ID',
  `scenic_id` bigint NOT NULL COMMENT '景区ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '票种名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int DEFAULT '0' COMMENT '库存',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '说明',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门票表';

-- ----------------------------
-- Records of ticket
-- ----------------------------
BEGIN;
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (1, 1, '成人票', 60.00, 60.00, 5000, '故宫博物院成人门票，含钟表馆、珍宝馆', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (2, 1, '学生票', 20.00, 60.00, 2000, '全日制大中小学生凭有效证件购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (3, 1, '老年票', 30.00, 60.00, 1000, '60周岁以上老人凭身份证购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (4, 2, '成人票', 40.00, 45.00, 8000, '八达岭长城成人门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (5, 2, '学生票', 20.00, 45.00, 3000, '全日制学生凭学生证购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (6, 2, '索道往返票', 140.00, 160.00, 2000, '含门票+索道往返', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (7, 3, '游船票', 55.00, 60.00, 1000, '西湖游船环湖游览', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (8, 3, '雷峰塔门票', 40.00, 40.00, 2000, '雷峰塔景点门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (9, 4, '成人票', 190.00, 230.00, 3000, '黄山风景区大门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (10, 4, '学生票', 95.00, 230.00, 1500, '全日制学生凭证购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (11, 4, '索道+门票套票', 300.00, 350.00, 1000, '含大门票+云谷索道往返', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (12, 5, '成人票', 225.00, 248.00, 2000, '张家界国家森林公园门票，4日内有效', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (13, 5, '学生票', 115.00, 248.00, 1000, '全日制学生凭证购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (14, 6, '成人票', 169.00, 250.00, 2000, '九寨沟风景区门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (15, 6, '观光车票', 90.00, 90.00, 2000, '景区内观光车票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (16, 7, '古城维护费', 50.00, 80.00, 10000, '丽江古城维护费', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (17, 8, '漓江精华游', 210.00, 260.00, 1500, '磨盘山-阳朔精华游船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (18, 8, '漓江全程游', 350.00, 400.00, 800, '桂林-阳朔全程游船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (19, 9, '往返船票', 35.00, 35.00, 5000, '厦门-鼓浪屿往返船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (20, 9, '环岛游船票', 80.00, 100.00, 1000, '鼓浪屿环岛游览船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (21, 10, '成人票', 115.00, 125.00, 3000, '泰山风景区大门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (22, 10, '索道往返票', 200.00, 230.00, 1500, '含门票+索道往返', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (23, 11, '成人票', 120.00, 150.00, 4000, '秦始皇兵马俑博物馆门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (24, 11, '学生票', 60.00, 150.00, 2000, '全日制学生凭证购买', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (25, 12, '成人票', 160.00, 185.00, 2000, '峨眉山风景区门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (26, 12, '金顶索道往返', 120.00, 130.00, 1500, '金顶索道往返票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (27, 13, '成人票', 160.00, 180.00, 2500, '华山风景区门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (28, 13, '西峰索道往返', 280.00, 320.00, 1000, '西峰索道往返票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (29, 14, '成人票', 160.00, 180.00, 2000, '普陀山风景区门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (30, 14, '往返船票', 30.00, 30.00, 3000, '朱家尖-普陀山往返船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (31, 15, '一日游套票', 140.00, 170.00, 1500, '武夷山一日游门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (32, 15, '竹筏漂流票', 100.00, 130.00, 800, '九曲溪竹筏漂流', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (33, 16, '成人票', 30.00, 30.00, 5000, '颐和园大门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (34, 16, '联票', 60.00, 70.00, 3000, '含大门票+苏州街+佛香阁', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (35, 17, '成人票', 15.00, 15.00, 8000, '天坛公园大门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (36, 17, '联票', 34.00, 40.00, 4000, '含大门票+祈年殿+回音壁+圜丘', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (37, 18, '成人票', 80.00, 90.00, 3000, '乐山大佛景区门票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (38, 18, '游船票', 70.00, 80.00, 1500, '乐山大佛游船观光票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (39, 19, '成人票', 200.00, 200.00, 1000, '布达拉宫参观门票，需提前预约', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (40, 20, '浦江游船票', 120.00, 150.00, 2000, '黄浦江夜游船票', 1, '2025-12-13 11:08:43');
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES (41, 20, '东方明珠套票', 180.00, 220.00, 1500, '东方明珠观光+浦江游船', 1, '2025-12-13 11:08:43');
COMMIT;

-- ----------------------------
-- Table structure for travel_note
-- ----------------------------
DROP TABLE IF EXISTS `travel_note`;
CREATE TABLE `travel_note` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '游记ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '图片(JSON)',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `scenic_id` bigint DEFAULT NULL COMMENT '景区ID',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0待审核 1已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游记打卡表';

-- ----------------------------
-- Records of travel_note
-- ----------------------------
BEGIN;
INSERT INTO `travel_note` (`id`, `user_id`, `title`, `content`, `images`, `province`, `city`, `scenic_id`, `view_count`, `like_count`, `status`, `create_time`) VALUES (1, 11, '兰州牛肉面', '兰州牛肉面', NULL, '甘肃', NULL, NULL, 1, 0, 1, '2025-12-13 16:03:18');
INSERT INTO `travel_note` (`id`, `user_id`, `title`, `content`, `images`, `province`, `city`, `scenic_id`, `view_count`, `like_count`, `status`, `create_time`) VALUES (2, 11, '测试', '测试水电费', NULL, '天津', NULL, NULL, 7, 1, 1, '2025-12-13 17:28:20');
INSERT INTO `travel_note` (`id`, `user_id`, `title`, `content`, `images`, `province`, `city`, `scenic_id`, `view_count`, `like_count`, `status`, `create_time`) VALUES (3, 11, '测试2', '<img src=\"/uploads/f6ddb26113b444558dfa474c858c4fb0.jpg\" style=\"max-width: 100%; margin-top: 10px; margin-bottom: 10px; border-radius: 8px;\">', NULL, '山东', NULL, NULL, 2, 0, 1, '2025-12-13 17:52:47');
INSERT INTO `travel_note` (`id`, `user_id`, `title`, `content`, `images`, `province`, `city`, `scenic_id`, `view_count`, `like_count`, `status`, `create_time`) VALUES (4, 11, '测网速', '成都色粉', NULL, '重庆', NULL, NULL, 0, 0, 1, '2025-12-14 18:14:37');
COMMIT;

-- ----------------------------
-- Table structure for trip
-- ----------------------------
DROP TABLE IF EXISTS `trip`;
CREATE TABLE `trip` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '行程ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID(null为系统推荐)',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行程标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `days` int DEFAULT NULL COMMENT '天数',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `route_data` text COLLATE utf8mb4_unicode_ci COMMENT '路线数据(JSON)',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_public` tinyint DEFAULT '0' COMMENT '是否公开：0私有 1公开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程表';

-- ----------------------------
-- Records of trip
-- ----------------------------
BEGIN;
INSERT INTO `trip` (`id`, `user_id`, `title`, `description`, `cover_image`, `days`, `start_date`, `end_date`, `route_data`, `status`, `create_time`, `is_public`) VALUES (2, 11, '测试', '测试', '/uploads/793987066e4744eaa85acc87568ca9ac.jpeg', 3, '2025-12-13', '2025-12-17', '[{\"dayNum\":1,\"attractionName\":\"故宫博物院\",\"attractionId\":2,\"latitude\":39.924091,\"longitude\":116.403414,\"description\":\"测试\",\"sortOrder\":0,\"showDropdown\":false,\"searchResults\":[]},{\"dayNum\":1,\"attractionName\":\"西湖\",\"attractionId\":7,\"latitude\":30.242489,\"longitude\":120.148732,\"description\":\"好的\",\"sortOrder\":1,\"showDropdown\":false,\"searchResults\":[]}]', 1, '2025-12-13 21:00:57', 0);
COMMIT;

-- ----------------------------
-- Table structure for trip_detail
-- ----------------------------
DROP TABLE IF EXISTS `trip_detail`;
CREATE TABLE `trip_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `trip_id` bigint NOT NULL COMMENT '行程ID',
  `day_num` int NOT NULL COMMENT '第几天',
  `attraction_id` bigint DEFAULT NULL COMMENT '景点ID',
  `attraction_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '景点名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '安排说明',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程明细表';

-- ----------------------------
-- Records of trip_detail
-- ----------------------------
BEGIN;
INSERT INTO `trip_detail` (`id`, `trip_id`, `day_num`, `attraction_id`, `attraction_name`, `description`, `sort_order`) VALUES (1, 1, 1, NULL, '故宫', '8小时', 0);
INSERT INTO `trip_detail` (`id`, `trip_id`, `day_num`, `attraction_id`, `attraction_name`, `description`, `sort_order`) VALUES (2, 1, 2, NULL, '测试', '3', 1);
INSERT INTO `trip_detail` (`id`, `trip_id`, `day_num`, `attraction_id`, `attraction_name`, `description`, `sort_order`) VALUES (3, 2, 1, 2, '故宫博物院', '测试', 0);
INSERT INTO `trip_detail` (`id`, `trip_id`, `day_num`, `attraction_id`, `attraction_name`, `description`, `sort_order`) VALUES (4, 2, 1, 7, '西湖', '好的', 1);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `role` tinyint DEFAULT '0' COMMENT '角色：0用户 1管理员',
  `status` tinyint DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (1, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', NULL, '13800138001', 'zhangsan@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (2, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', NULL, '13800138002', 'lisi@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (3, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', NULL, '13800138003', 'wangwu@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (4, 'zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', NULL, '13800138004', 'zhaoliu@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (5, 'sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', NULL, '13800138005', 'sunqi@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (6, 'zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', NULL, '13800138006', 'zhouba@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (7, 'wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', NULL, '13800138007', 'wujiu@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (8, 'zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', NULL, '13800138008', 'zhengshi@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (9, 'test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', NULL, '13800138009', 'test@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (10, 'demo', 'e10adc3949ba59abbe56e057f20f883e', '演示用户', NULL, '13800138010', 'demo@example.com', 0, 1, '2025-12-13 11:02:00', '2025-12-13 11:02:00');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (11, 'cwp', 'e10adc3949ba59abbe56e057f20f883e', 'cwp', '/uploads/e9fc7c231e0a4239a90c5941126b4e22.jpg', '', '2123@qq.com', 0, 1, '2025-12-13 16:02:29', '2025-12-13 16:02:29');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES (12, 'xx', 'e10adc3949ba59abbe56e057f20f883e', 'xx', NULL, NULL, NULL, 0, 1, '2025-12-13 21:29:22', '2025-12-14 18:16:11');
COMMIT;

-- ----------------------------
-- Table structure for user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `user_behavior`;
CREATE TABLE `user_behavior` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1景区 2攻略 3行程 4美食 5景点',
  `behavior_type` tinyint NOT NULL COMMENT '行为类型：1浏览 2收藏 3评论 4购票 5点赞',
  `score` double DEFAULT '1' COMMENT '行为权重分数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_target` (`target_id`),
  KEY `idx_user_target` (`user_id`,`target_id`,`target_type`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- ----------------------------
-- Records of user_behavior
-- ----------------------------
BEGIN;
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (1, 11, 1, 1, 1, 1, '2025-12-13 16:05:30');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (2, 11, 4, 1, 1, 1, '2025-12-13 16:06:11');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (3, 11, 5, 1, 1, 1, '2025-12-13 16:22:44');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (4, 11, 7, 5, 1, 1, '2025-12-13 20:02:25');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (5, 11, 7, 5, 1, 1, '2025-12-13 20:05:20');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (6, 11, 7, 5, 1, 1, '2025-12-13 20:06:01');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (7, 11, 7, 5, 1, 1, '2025-12-13 20:06:16');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (8, 11, 7, 5, 1, 1, '2025-12-13 20:13:41');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (9, 11, 6, 2, 1, 1, '2025-12-13 20:13:55');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (10, 11, 2, 5, 1, 1, '2025-12-13 20:38:48');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (11, 11, 1, 3, 1, 1, '2025-12-13 20:42:11');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (12, 11, 1, 3, 1, 1, '2025-12-13 20:50:43');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (13, 11, 1, 3, 1, 1, '2025-12-13 20:52:27');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (14, 11, 1, 3, 1, 1, '2025-12-13 20:54:50');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (15, 11, 1, 3, 1, 1, '2025-12-13 20:55:03');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (16, 11, 1, 3, 1, 1, '2025-12-13 20:55:15');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (17, 11, 1, 3, 1, 1, '2025-12-13 20:55:36');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (18, 11, 1, 3, 1, 1, '2025-12-13 20:55:52');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (19, 11, 2, 5, 1, 1, '2025-12-13 20:56:32');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (20, 11, 2, 3, 1, 1, '2025-12-13 21:01:00');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (21, 11, 1, 4, 1, 1, '2025-12-13 21:28:41');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (22, 12, 2, 4, 1, 1, '2025-12-13 21:31:39');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (23, 12, 2, 4, 1, 1, '2025-12-13 21:34:04');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (24, 12, 2, 4, 1, 1, '2025-12-13 21:34:15');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (25, 12, 2, 4, 1, 1, '2025-12-13 21:35:45');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (26, 12, 2, 2, 1, 1, '2025-12-13 21:35:58');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (27, 12, 1, 4, 1, 1, '2025-12-13 21:37:38');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (28, 12, 3, 2, 1, 1, '2025-12-13 21:38:17');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (29, 12, 10, 2, 1, 1, '2025-12-13 21:42:37');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (30, 12, 1, 4, 1, 1, '2025-12-13 21:43:14');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (31, 12, 2, 4, 1, 1, '2025-12-13 21:44:13');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (32, 12, 2, 4, 1, 1, '2025-12-13 21:44:59');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (33, 12, 2, 4, 1, 1, '2025-12-13 21:45:08');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (34, 12, 2, 4, 1, 1, '2025-12-13 21:51:46');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (35, 12, 2, 4, 1, 1, '2025-12-13 21:51:50');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (36, 12, 1, 4, 1, 1, '2025-12-13 21:51:52');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (37, 12, 2, 1, 1, 1, '2025-12-14 10:22:45');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (38, 12, 2, 1, 1, 1, '2025-12-14 10:29:44');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (39, 12, 2, 1, 1, 1, '2025-12-14 10:29:58');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (40, 11, 2, 1, 1, 1, '2025-12-14 18:12:29');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (41, 11, 3, 1, 1, 1, '2025-12-14 18:14:21');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (42, 11, 16, 5, 1, 1, '2025-12-14 18:14:47');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (43, 11, 2, 3, 1, 1, '2025-12-14 18:14:55');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (44, 11, 2, 4, 1, 1, '2025-12-14 18:15:08');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (45, 11, 2, 3, 1, 1, '2025-12-14 19:05:49');
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES (46, 11, 1, 1, 1, 1, '2026-02-25 22:43:52');
COMMIT;

-- ----------------------------
-- Table structure for user_footprint
-- ----------------------------
DROP TABLE IF EXISTS `user_footprint`;
CREATE TABLE `user_footprint` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `first_visit_time` datetime DEFAULT NULL COMMENT '首次到访时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_province` (`user_id`,`province`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户足迹表';

-- ----------------------------
-- Records of user_footprint
-- ----------------------------
BEGIN;
INSERT INTO `user_footprint` (`id`, `user_id`, `province`, `first_visit_time`, `create_time`) VALUES (1, 11, '甘肃', '2025-12-13 16:03:18', '2025-12-13 16:03:18');
INSERT INTO `user_footprint` (`id`, `user_id`, `province`, `first_visit_time`, `create_time`) VALUES (2, 11, '天津', '2025-12-13 17:28:20', '2025-12-13 17:28:20');
INSERT INTO `user_footprint` (`id`, `user_id`, `province`, `first_visit_time`, `create_time`) VALUES (3, 11, '山东', '2025-12-13 17:52:48', '2025-12-13 17:52:48');
INSERT INTO `user_footprint` (`id`, `user_id`, `province`, `first_visit_time`, `create_time`) VALUES (4, 11, '重庆', '2025-12-14 18:14:37', '2025-12-14 18:14:37');
COMMIT;

-- ----------------------------
-- Backfill tags for demo data
-- ----------------------------
UPDATE `scenic_area`
SET `tags` = CASE
  WHEN `name` LIKE '%故宫%' THEN '人文古迹,城市观光,北京'
  WHEN `name` LIKE '%长城%' THEN '人文古迹,登山徒步,北京'
  WHEN `name` LIKE '%西湖%' THEN '自然风光,城市观光,杭州'
  WHEN `name` LIKE '%黄山%' THEN '自然风光,登山徒步'
  WHEN `name` LIKE '%张家界%' THEN '自然风光,登山徒步'
  WHEN `name` LIKE '%兵马俑%' THEN '人文古迹,城市观光'
  WHEN `name` LIKE '%广州%' OR `city` LIKE '%广州%' THEN '城市观光,地方小吃'
  WHEN `name` LIKE '%三亚%' OR `province` = '海南' THEN '自然风光,海岛海滨,海南'
  ELSE CONCAT_WS(',', NULLIF(`tags`, ''), '自然风光')
END
WHERE `tags` IS NULL OR `tags` = '';

UPDATE `attraction`
SET `tags` = CASE
  WHEN `name` LIKE '%天安门%' OR `name` LIKE '%外滩%' OR `name` LIKE '%东方明珠%' THEN '城市观光,人文古迹'
  WHEN `name` LIKE '%故宫%' OR `name` LIKE '%颐和园%' OR `name` LIKE '%灵隐寺%' OR `name` LIKE '%华清池%' THEN '人文古迹,城市观光'
  WHEN `name` LIKE '%西湖%' OR `name` LIKE '%雷峰塔%' THEN '自然风光,城市观光,杭州'
  WHEN `name` LIKE '%珠江%' THEN '城市观光,夜市'
  ELSE CONCAT_WS(',', NULLIF(`tags`, ''), '自然风光')
END
WHERE `tags` IS NULL OR `tags` = '';

UPDATE `food`
SET `tags` = CASE
  WHEN `name` LIKE '%烤鸭%' THEN '地方小吃,老字号,北京'
  WHEN `name` LIKE '%牛肉面%' OR `name` LIKE '%面%' THEN '地方小吃,特色餐厅'
  WHEN `name` LIKE '%火锅%' THEN '火锅烧烤,地方小吃'
  ELSE CONCAT_WS(',', NULLIF(`tags`, ''), '特色餐厅')
END
WHERE `tags` IS NULL OR `tags` = '';

UPDATE `strategy`
SET `tags` = CONCAT_WS(',', NULLIF(`tags`, ''),
  CASE
    WHEN `title` LIKE '%美食%' OR `content` LIKE '%美食%' THEN '美食为主'
    WHEN `title` LIKE '%文化%' OR `content` LIKE '%博物馆%' THEN '深度人文'
    WHEN `title` LIKE '%日出%' OR `title` LIKE '%山%' THEN '经典打卡'
    ELSE '轻松休闲'
  END
)
WHERE `tags` IS NULL OR (`tags` NOT LIKE '%轻松休闲%' AND `tags` NOT LIKE '%美食为主%' AND `tags` NOT LIKE '%深度人文%' AND `tags` NOT LIKE '%经典打卡%');

UPDATE `trip`
SET `tags` = CASE
  WHEN `title` LIKE '%美食%' OR `description` LIKE '%美食%' THEN '美食为主,轻松休闲'
  WHEN `days` <= 2 THEN '周末短途,经典打卡'
  WHEN `days` >= 5 THEN '深度人文,家庭出行'
  ELSE '经典打卡,轻松休闲'
END
WHERE `tags` IS NULL OR `tags` = '';

UPDATE `travel_note`
SET `tags` = CASE
  WHEN `title` LIKE '%测%' OR `content` LIKE '%测%' THEN '旅行故事,避坑经验'
  WHEN `content` LIKE '%<img%' THEN '摄影打卡,旅行故事'
  WHEN `title` LIKE '%面%' OR `content` LIKE '%面%' OR `content` LIKE '%美食%' THEN '旅行故事,地方小吃'
  ELSE '旅行故事,小众路线'
END
WHERE `tags` IS NULL OR `tags` = '';

SET FOREIGN_KEY_CHECKS = 1;
-- Rating fields and corresponding seed data.
-- Run this after the base travel.sql if your database already exists.

ALTER TABLE `scenic_area`
  ADD COLUMN `rating` decimal(2,1) NOT NULL DEFAULT 0.0 COMMENT '平均评分' AFTER `collect_count`,
  ADD COLUMN `rating_count` int NOT NULL DEFAULT 0 COMMENT '评分人数' AFTER `rating`;

ALTER TABLE `attraction`
  ADD COLUMN `rating` decimal(2,1) NOT NULL DEFAULT 0.0 COMMENT '平均评分' AFTER `collect_count`,
  ADD COLUMN `rating_count` int NOT NULL DEFAULT 0 COMMENT '评分人数' AFTER `rating`;

ALTER TABLE `comment`
  ADD COLUMN `rating` tinyint DEFAULT NULL COMMENT '评分�?-5�? AFTER `content`;

DELETE FROM `comment`;
ALTER TABLE `comment` AUTO_INCREMENT = 1;

INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `rating`, `parent_id`, `like_count`, `status`, `create_time`) VALUES
(1, 1, 'scenic', 11, '故宫路线清晰，展览内容很丰富，建议早上入园�?, 5, NULL, 3, 1, '2026-01-03 09:20:00'),
(2, 1, 'scenic', 12, '建筑很震撼，人流较多但整体体验好�?, 5, NULL, 1, 1, '2026-01-05 14:10:00'),
(3, 1, 'scenic', 13, '门票和交通都方便，适合第一次来北京�?, 4, NULL, 0, 1, '2026-01-08 16:30:00'),
(4, 2, 'scenic', 11, '八达岭维护很好，登城体验完整�?, 5, NULL, 2, 1, '2026-01-10 10:12:00'),
(5, 2, 'scenic', 14, '风景开阔，冬天风比较大�?, 4, NULL, 1, 1, '2026-01-12 15:00:00'),
(6, 3, 'scenic', 12, '西湖适合慢慢走，免费开放很友好�?, 5, NULL, 4, 1, '2026-01-13 18:20:00'),
(7, 3, 'scenic', 15, '景色好，但节假日拥挤�?, 4, NULL, 1, 1, '2026-01-15 11:05:00'),
(8, 4, 'scenic', 13, '黄山云海很惊艳，登山强度偏高�?, 5, NULL, 2, 1, '2026-01-17 09:45:00'),
(9, 4, 'scenic', 16, '索道和指示牌完善，适合两日游�?, 5, NULL, 0, 1, '2026-01-18 13:25:00'),
(10, 5, 'scenic', 14, '张家界地貌独特，拍照非常出片�?, 5, NULL, 5, 1, '2026-01-20 10:30:00'),
(11, 6, 'scenic', 15, '九寨沟水色很美，景区接驳效率高�?, 5, NULL, 3, 1, '2026-01-22 12:40:00'),
(12, 7, 'scenic', 16, '丽江夜晚氛围好，商业化稍明显�?, 4, NULL, 2, 1, '2026-01-23 20:18:00'),
(13, 8, 'scenic', 11, '漓江山水很经典，竹筏体验不错�?, 5, NULL, 1, 1, '2026-01-25 08:55:00'),
(14, 9, 'scenic', 12, '鼓浪屿适合步行，建筑很有特色�?, 4, NULL, 1, 1, '2026-01-26 17:35:00'),
(15, 10, 'scenic', 13, '泰山日出值得早起，路线成熟�?, 5, NULL, 2, 1, '2026-01-28 06:50:00'),
(16, 2, 'attraction', 11, '故宫博物院馆藏丰富，建议预留半天以上�?, 5, NULL, 2, 1, '2026-02-01 10:00:00'),
(17, 2, 'attraction', 12, '展线清楚，适合亲子和历史爱好者�?, 5, NULL, 1, 1, '2026-02-02 11:30:00'),
(18, 7, 'attraction', 13, '西湖核心区很美，傍晚体验最好�?, 5, NULL, 3, 1, '2026-02-03 18:10:00'),
(19, 7, 'attraction', 14, '人多但景色稳定在线�?, 4, NULL, 0, 1, '2026-02-04 15:22:00'),
(20, 16, 'attraction', 15, '洪崖洞夜景漂亮，拍照点很多�?, 5, NULL, 4, 1, '2026-02-05 20:05:00'),
(21, 16, 'attraction', 16, '交通略拥堵，夜景值得�?, 4, NULL, 1, 1, '2026-02-06 21:15:00'),
(22, 14, 'attraction', 11, '广州塔视野开阔，夜景体验更好�?, 5, NULL, 2, 1, '2026-02-07 19:40:00'),
(23, 5, 'attraction', 12, '东方明珠地标感强，排队时间稍长�?, 4, NULL, 1, 1, '2026-02-08 16:28:00'),
(24, 8, 'attraction', 13, '灵隐寺环境清幽，文化氛围好�?, 5, NULL, 3, 1, '2026-02-09 09:35:00');

UPDATE `scenic_area` s
LEFT JOIN (
  SELECT target_id, ROUND(AVG(rating), 1) AS avg_rating, COUNT(*) AS cnt
  FROM `comment`
  WHERE target_type = 'scenic' AND status = 1 AND rating IS NOT NULL
  GROUP BY target_id
) r ON r.target_id = s.id
SET s.rating = COALESCE(r.avg_rating, 0.0),
    s.rating_count = COALESCE(r.cnt, 0);

UPDATE `attraction` a
LEFT JOIN (
  SELECT target_id, ROUND(AVG(rating), 1) AS avg_rating, COUNT(*) AS cnt
  FROM `comment`
  WHERE target_type = 'attraction' AND status = 1 AND rating IS NOT NULL
  GROUP BY target_id
) r ON r.target_id = a.id
SET a.rating = COALESCE(r.avg_rating, 0.0),
    a.rating_count = COALESCE(r.cnt, 0);
