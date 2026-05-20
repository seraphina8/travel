-- Rich seed data for the travel system.
-- Run this after importing travel.sql. The ids start after the original demo data.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Tags for global filters.
INSERT INTO `tag` (`id`, `name`, `type`, `sort_order`, `status`) VALUES
(33, '亲子友好', 1, 9, 1),
(34, '夜游', 1, 10, 1),
(35, '摄影出片', 1, 11, 1),
(36, '江苏', 2, 9, 1),
(37, '山东', 2, 10, 1),
(38, '黑龙江', 2, 11, 1),
(39, '甘肃', 2, 12, 1),
(40, '海滨度假', 3, 7, 1),
(41, '人少小众', 3, 8, 1),
(42, '博物馆', 3, 9, 1),
(43, '海鲜', 4, 7, 1),
(44, '城市漫步', 5, 7, 1),
(45, '自然风景', 5, 8, 1),
(46, '历史文化', 5, 9, 1),
(47, '亲子记录', 5, 10, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `type` = VALUES(`type`), `sort_order` = VALUES(`sort_order`), `status` = VALUES(`status`);

-- Scenic areas.
INSERT INTO `scenic_area` (`id`, `name`, `description`, `address`, `province`, `city`, `cover_image`, `images`, `longitude`, `latitude`, `open_time`, `level`, `tags`, `status`, `create_time`, `update_time`, `view_count`, `collect_count`, `price`) VALUES
(21, '南京夫子庙秦淮风光带', '夫子庙秦淮风光带串联秦淮河、江南贡院、老门东等历史街区，适合夜游、城市漫步和品尝南京小吃。', '江苏省南京市秦淮区贡院街', '江苏', '南京', '/uploads/seed_001.jpg', '["/uploads/seed_001.jpg","/uploads/seed_002.jpg"]', 118.788694, 32.021967, '09:00-22:00', '5A', '人文古迹,夜游,城市观光,美食为主', 1, '2026-05-01 09:00:00', '2026-05-01 09:00:00', 18760, 3280, 0.00),
(22, '苏州拙政园', '拙政园是江南古典园林代表，水池、亭台、花木布局精巧，适合慢游和摄影。', '江苏省苏州市姑苏区东北街178号', '江苏', '苏州', '/uploads/seed_002.jpg', '["/uploads/seed_002.jpg","/uploads/seed_003.jpg"]', 120.626355, 31.324954, '07:30-17:30', '5A', '人文古迹,摄影出片,轻松休闲', 1, '2026-05-01 09:05:00', '2026-05-01 09:05:00', 14220, 2460, 80.00),
(23, '青岛崂山风景区', '崂山山海相连，道观、海岸线和登山路线丰富，是青岛自然风光代表。', '山东省青岛市崂山区崂山景区', '山东', '青岛', '/uploads/seed_004.jpg', '["/uploads/seed_004.jpg","/uploads/seed_005.jpg"]', 120.608220, 36.195587, '07:00-17:30', '5A', '自然风光,登山徒步,海岛海滨', 1, '2026-05-01 09:10:00', '2026-05-01 09:10:00', 11890, 2050, 120.00),
(24, '三亚亚龙湾', '亚龙湾拥有细软沙滩和清澈海水，适合亲子度假、潜水和海滨休闲。', '海南省三亚市吉阳区亚龙湾路', '海南', '三亚', '/uploads/seed_006.jpg', '["/uploads/seed_006.jpg","/uploads/seed_007.jpg"]', 109.643414, 18.230274, '全天开放', '4A', '海岛海滨,亲子游,海滨度假', 1, '2026-05-01 09:15:00', '2026-05-01 09:15:00', 20340, 5120, 0.00),
(25, '乌镇西栅', '乌镇西栅以水乡夜景、古桥民居和江南生活气息闻名，适合周末短途。', '浙江省嘉兴市桐乡市乌镇石佛南路18号', '浙江', '嘉兴', '/uploads/seed_003.jpg', '["/uploads/seed_003.jpg","/uploads/seed_007.jpg"]', 120.481983, 30.746072, '09:00-22:00', '5A', '人文古迹,夜游,周末短途', 1, '2026-05-01 09:20:00', '2026-05-01 09:20:00', 16780, 3900, 150.00),
(26, '敦煌莫高窟', '莫高窟保存大量壁画和彩塑，是丝路文化和佛教艺术的重要遗产。', '甘肃省酒泉市敦煌市莫高窟数字展示中心', '甘肃', '敦煌', '/uploads/seed_007.jpg', '["/uploads/seed_007.jpg","/uploads/seed_008.jpg"]', 94.809228, 40.037335, '08:00-18:00', '5A', '人文古迹,深度人文,摄影出片', 1, '2026-05-01 09:25:00', '2026-05-01 09:25:00', 13020, 2860, 238.00),
(27, '哈尔滨冰雪大世界', '哈尔滨冰雪大世界以大型冰雕、雪雕和夜间灯光秀闻名，是冬季旅行热门目的地。', '黑龙江省哈尔滨市松北区太阳岛西侧', '黑龙江', '哈尔滨', '/uploads/seed_009.jpg', '["/uploads/seed_009.jpg","/uploads/seed_010.jpg"]', 126.559076, 45.799299, '11:00-22:00', '4A', '亲子游,夜游,摄影出片', 1, '2026-05-01 09:30:00', '2026-05-01 09:30:00', 15660, 3420, 328.00),
(28, '长沙岳麓山橘子洲旅游区', '岳麓山与橘子洲串联山水、人文和城市烟火气，适合城市观光与美食路线。', '湖南省长沙市岳麓区橘子洲头2号', '湖南', '长沙', '/uploads/seed_011.jpg', '["/uploads/seed_011.jpg","/uploads/seed_012.jpg"]', 112.958704, 28.191402, '07:00-22:00', '5A', '城市观光,人文古迹,美食为主', 1, '2026-05-01 09:35:00', '2026-05-01 09:35:00', 19450, 4310, 0.00)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `address` = VALUES(`address`), `province` = VALUES(`province`), `city` = VALUES(`city`), `cover_image` = VALUES(`cover_image`), `images` = VALUES(`images`), `longitude` = VALUES(`longitude`), `latitude` = VALUES(`latitude`), `open_time` = VALUES(`open_time`), `level` = VALUES(`level`), `tags` = VALUES(`tags`), `status` = VALUES(`status`), `view_count` = VALUES(`view_count`), `collect_count` = VALUES(`collect_count`), `price` = VALUES(`price`);

-- Attractions under the new scenic areas.
INSERT INTO `attraction` (`id`, `scenic_id`, `name`, `description`, `cover_image`, `province`, `city`, `tags`, `longitude`, `latitude`, `view_count`, `collect_count`, `create_time`) VALUES
(21, 21, '秦淮河画舫', '夜晚乘船看秦淮河两岸灯影和古建筑，是夫子庙经典体验。', '/uploads/seed_013.jpg', '江苏', '南京', '夜游,城市观光', 118.787401, 32.020431, 8200, 1320, '2026-05-01 10:00:00'),
(22, 21, '江南贡院', '中国古代科举文化展示地，适合了解南京历史。', '/uploads/seed_014.jpg', '江苏', '南京', '人文古迹,博物馆', 118.790249, 32.020102, 5400, 860, '2026-05-01 10:02:00'),
(23, 22, '远香堂', '拙政园中部主厅，可赏园内水景和荷风。', '/uploads/seed_014.jpg', '江苏', '苏州', '人文古迹,摄影出片', 120.626021, 31.324784, 6100, 970, '2026-05-01 10:04:00'),
(24, 22, '小飞虹', '园内廊桥小景精致，是拍摄江南园林构图的好位置。', '/uploads/seed_015.jpg', '江苏', '苏州', '摄影出片,轻松休闲', 120.627002, 31.324321, 4300, 690, '2026-05-01 10:06:00'),
(25, 23, '太清游览区', '崂山临海道观集中区域，山海景观开阔。', '/uploads/seed_016.jpg', '山东', '青岛', '自然风光,登山徒步', 120.666120, 36.161818, 7600, 1160, '2026-05-01 10:08:00'),
(26, 23, '仰口海岸', '山路与海岸线相连，适合轻徒步和拍海景。', '/uploads/seed_017.jpg', '山东', '青岛', '海岛海滨,摄影出片', 120.663511, 36.253665, 6900, 990, '2026-05-01 10:10:00'),
(27, 24, '亚龙湾沙滩', '沙质细软，适合亲子玩水和海边散步。', '/uploads/seed_018.jpg', '海南', '三亚', '海岛海滨,亲子游', 109.643102, 18.229917, 12600, 2860, '2026-05-01 10:12:00'),
(28, 24, '热带天堂森林公园', '俯瞰亚龙湾海岸线，玻璃栈道和雨林步道很受欢迎。', '/uploads/seed_019.jpg', '海南', '三亚', '自然风光,亲子游', 109.641021, 18.254431, 9800, 1740, '2026-05-01 10:14:00'),
(29, 25, '西栅老街', '水巷、石桥、老宅密集，夜景氛围很好。', '/uploads/seed_015.jpg', '浙江', '嘉兴', '人文古迹,夜游', 120.481672, 30.746130, 10100, 2100, '2026-05-01 10:16:00'),
(30, 25, '草木本色染坊', '蓝印花布展示区，适合亲子体验和拍照。', '/uploads/seed_019.jpg', '浙江', '嘉兴', '亲子游,摄影出片', 120.480983, 30.745502, 5200, 790, '2026-05-01 10:18:00'),
(31, 26, '莫高窟九层楼', '莫高窟标志建筑，集中展示石窟艺术。', '/uploads/seed_020.jpg', '甘肃', '敦煌', '人文古迹,深度人文', 94.809411, 40.037014, 8800, 1520, '2026-05-01 10:20:00'),
(32, 26, '鸣沙山月牙泉', '沙丘与泉水相映，是敦煌经典自然景观。', '/uploads/seed_019.jpg', '甘肃', '敦煌', '自然风光,摄影出片', 94.662010, 40.088700, 11300, 2430, '2026-05-01 10:22:00'),
(33, 27, '冰雪主题城堡', '大型冰雕群和灯光秀集中区域，适合夜间游览。', '/uploads/seed_021.jpg', '黑龙江', '哈尔滨', '夜游,亲子游', 126.559011, 45.798991, 9700, 2210, '2026-05-01 10:24:00'),
(34, 27, '雪雕艺术区', '雪雕作品体量大，白天观赏层次更清晰。', '/uploads/seed_022.jpg', '黑龙江', '哈尔滨', '摄影出片,亲子游', 126.557901, 45.799901, 6200, 1180, '2026-05-01 10:26:00'),
(35, 28, '橘子洲头', '长沙城市名片之一，适合傍晚散步和看湘江风景。', '/uploads/seed_023.jpg', '湖南', '长沙', '城市观光,人文古迹', 112.958222, 28.191010, 13200, 3010, '2026-05-01 10:28:00'),
(36, 28, '岳麓书院', '千年学府，适合了解湖湘文化和古建筑。', '/uploads/seed_024.jpg', '湖南', '长沙', '人文古迹,深度人文', 112.944869, 28.184201, 8900, 1690, '2026-05-01 10:30:00')
ON DUPLICATE KEY UPDATE `scenic_id` = VALUES(`scenic_id`), `description` = VALUES(`description`), `cover_image` = VALUES(`cover_image`), `province` = VALUES(`province`), `city` = VALUES(`city`), `tags` = VALUES(`tags`), `longitude` = VALUES(`longitude`), `latitude` = VALUES(`latitude`), `view_count` = VALUES(`view_count`), `collect_count` = VALUES(`collect_count`);

-- Tickets.
INSERT INTO `ticket` (`id`, `scenic_id`, `name`, `price`, `original_price`, `stock`, `description`, `status`, `create_time`) VALUES
(42, 21, '夫子庙秦淮河夜游船票', 80.00, 100.00, 320, '含秦淮河画舫夜游，建议提前预约班次。', 1, '2026-05-01 11:00:00'),
(43, 22, '拙政园成人票', 80.00, 90.00, 280, '实名预约入园，旺季建议提前购买。', 1, '2026-05-01 11:02:00'),
(44, 23, '崂山太清线成人票', 120.00, 130.00, 260, '含景区进山票，交通车另按景区规则执行。', 1, '2026-05-01 11:04:00'),
(45, 24, '亚龙湾雨林套票', 138.00, 158.00, 400, '适合亲子和轻徒步游客。', 1, '2026-05-01 11:06:00'),
(46, 25, '乌镇西栅成人票', 150.00, 160.00, 350, '西栅景区单人成人票。', 1, '2026-05-01 11:08:00'),
(47, 26, '莫高窟A类参观票', 238.00, 238.00, 180, '含数字展示和洞窟参观，以实际预约场次为准。', 1, '2026-05-01 11:10:00'),
(48, 27, '冰雪大世界成人票', 328.00, 348.00, 500, '夜场灯光效果更佳，注意防寒。', 1, '2026-05-01 11:12:00'),
(49, 28, '岳麓山橘子洲观光车票', 40.00, 50.00, 600, '景区观光车联票，适合轻松游览。', 1, '2026-05-01 11:14:00'),
(50, 21, '江南贡院成人票', 45.00, 50.00, 260, '科举文化展馆参观票。', 1, '2026-05-01 11:16:00'),
(51, 23, '崂山仰口线成人票', 90.00, 100.00, 240, '适合山海轻徒步路线。', 1, '2026-05-01 11:18:00')
ON DUPLICATE KEY UPDATE `scenic_id` = VALUES(`scenic_id`), `name` = VALUES(`name`), `price` = VALUES(`price`), `original_price` = VALUES(`original_price`), `stock` = VALUES(`stock`), `description` = VALUES(`description`), `status` = VALUES(`status`);

-- Approved strategies and a few review samples.
INSERT INTO `strategy` (`id`, `user_id`, `title`, `content`, `cover_image`, `tags`, `view_count`, `like_count`, `status`, `create_time`) VALUES
(12, 1, '南京秦淮夜游两日攻略', '第一天安排夫子庙、江南贡院和秦淮河夜游，晚餐可以选择鸭血粉丝汤和盐水鸭。第二天上午去老门东，下午到总统府或南京博物院。路线适合周末短途，住宿建议选在夫子庙或新街口。', '/uploads/seed_001.jpg', '江苏,南京,夜游,周末短途,美食为主', 1680, 146, 1, '2026-05-02 09:00:00'),
(13, 2, '苏州园林一日慢游路线', '上午先到拙政园，预留两到三小时慢慢看水景和厅堂。中午去平江路用餐，下午可接狮子林或苏州博物馆。拍照建议避开正午强光。', '/uploads/seed_002.jpg', '江苏,苏州,轻松休闲,摄影出片', 1320, 98, 1, '2026-05-02 09:10:00'),
(14, 3, '青岛崂山山海一日游', '推荐太清线或仰口线二选一，不建议一天把所有线路跑完。上午登山，下午去海边看日落，晚餐选择本地海鲜。', '/uploads/seed_004.jpg', '山东,青岛,登山徒步,海岛海滨', 1510, 121, 1, '2026-05-02 09:20:00'),
(15, 4, '三亚亲子海滨度假安排', '亚龙湾适合低强度度假，上午玩沙滩，下午安排雨林公园或酒店泳池。亲子出行注意防晒和补水，海上项目按孩子年龄选择。', '/uploads/seed_006.jpg', '海南,三亚,亲子游,海滨度假', 2230, 214, 1, '2026-05-02 09:30:00'),
(16, 5, '敦煌三天两晚文化路线', '第一天莫高窟和敦煌夜市，第二天鸣沙山月牙泉，第三天可安排阳关或玉门关。莫高窟门票一定要提前预约，旺季余票很紧张。', '/uploads/seed_007.jpg', '甘肃,敦煌,深度人文,摄影出片', 1760, 166, 1, '2026-05-02 09:40:00'),
(17, 6, '哈尔滨冬季三日玩法', '中央大街、索菲亚教堂、冰雪大世界可以组成经典路线。冰雪大世界建议傍晚入园，既能拍白天又能拍夜景。', '/uploads/seed_009.jpg', '黑龙江,哈尔滨,亲子游,夜游', 1890, 180, 1, '2026-05-02 09:50:00'),
(18, 7, '长沙吃逛两日攻略', '第一天橘子洲和岳麓山，晚上去坡子街或黄兴路。第二天湖南博物院和五一广场，茶饮和小吃建议错峰排队。', '/uploads/seed_011.jpg', '湖南,长沙,美食为主,城市观光', 2410, 230, 1, '2026-05-02 10:00:00'),
(19, 8, '乌镇西栅周末避坑指南', '住宿选在景区内更方便看夜景，白天可以避开旅行团高峰。西栅夜景适合慢慢走，不建议把行程排得太满。', '/uploads/seed_003.jpg', '浙江,嘉兴,夜游,周末短途', 1220, 88, 1, '2026-05-02 10:10:00'),
(20, 9, '待审核的海滨玩法草稿', '这是一条用于后台审核流程展示的攻略草稿。', '/uploads/seed_006.jpg', '海南,海滨度假', 0, 0, 0, '2026-05-02 10:20:00'),
(21, 10, '被拒绝的重复攻略示例', '这是一条用于后台查看拒绝状态的示例数据。', '/uploads/seed_004.jpg', '山东,青岛', 10, 0, 2, '2026-05-02 10:30:00')
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `title` = VALUES(`title`), `content` = VALUES(`content`), `cover_image` = VALUES(`cover_image`), `tags` = VALUES(`tags`), `view_count` = VALUES(`view_count`), `like_count` = VALUES(`like_count`), `status` = VALUES(`status`);

-- Travel notes with separated images and text.
INSERT INTO `travel_note` (`id`, `user_id`, `title`, `content`, `images`, `province`, `city`, `tags`, `scenic_id`, `view_count`, `like_count`, `status`, `create_time`) VALUES
(5, 1, '秦淮河夜景打卡', '晚上沿着秦淮河慢慢走，灯光亮起来之后很有氛围。游船大概半小时左右，适合第一次来南京的人。', '["/uploads/seed_025.jpg","/uploads/seed_026.jpg"]', '江苏', '南京', '城市漫步,历史文化,夜游', 21, 520, 48, 1, '2026-05-03 09:00:00'),
(6, 2, '拙政园慢游记录', '园子不大但细节很多，建议留足时间看窗景和水面倒影。上午人相对少，拍照更舒服。', '["/uploads/seed_026.jpg"]', '江苏', '苏州', '自然风景,历史文化,摄影出片', 22, 430, 36, 1, '2026-05-03 09:20:00'),
(7, 3, '崂山山海线', '一边是山路一边是海，体力一般的话建议只选一条线路。海边风大，外套很有必要。', '["/uploads/seed_027.jpg","/uploads/seed_028.jpg"]', '山东', '青岛', '自然风景,城市漫步', 23, 610, 57, 1, '2026-05-03 09:40:00'),
(8, 4, '亚龙湾亲子假期', '沙滩很适合孩子玩，上午紫外线已经很强，帽子、防晒衣和饮用水都要准备好。', '["/uploads/seed_029.jpg"]', '海南', '三亚', '亲子记录,自然风景', 24, 760, 83, 1, '2026-05-03 10:00:00'),
(9, 5, '敦煌看壁画的一天', '莫高窟讲解很重要，提前做一点功课会更容易看懂。洞窟内不能拍照，外面可以拍九层楼。', '["/uploads/seed_030.jpg"]', '甘肃', '敦煌', '历史文化,自然风景', 26, 680, 72, 1, '2026-05-03 10:20:00'),
(10, 6, '冰雪大世界夜场', '傍晚入园比较合适，天色变暗之后灯光会完全不一样。一定要穿防滑鞋和厚手套。', '["/uploads/seed_031.jpg"]', '黑龙江', '哈尔滨', '亲子记录,夜游,摄影出片', 27, 590, 65, 1, '2026-05-03 10:40:00'),
(11, 7, '长沙两天吃逛记录', '橘子洲适合散步，岳麓书院适合慢慢看。晚上去五一广场，人很多但吃的选择也多。', '["/uploads/seed_032.jpg"]', '湖南', '长沙', '城市漫步,历史文化', 28, 880, 94, 1, '2026-05-03 11:00:00'),
(12, 8, '乌镇西栅夜游', '西栅晚上比白天更有氛围，桥上和水边都很适合拍照。住宿在景区内会从容很多。', '["/uploads/seed_033.jpg"]', '浙江', '嘉兴', '城市漫步,夜游', 25, 530, 41, 1, '2026-05-03 11:20:00')
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `title` = VALUES(`title`), `content` = VALUES(`content`), `images` = VALUES(`images`), `province` = VALUES(`province`), `city` = VALUES(`city`), `tags` = VALUES(`tags`), `scenic_id` = VALUES(`scenic_id`), `view_count` = VALUES(`view_count`), `like_count` = VALUES(`like_count`), `status` = VALUES(`status`);

-- Foods.
INSERT INTO `food` (`id`, `name`, `description`, `cover_image`, `province`, `city`, `address`, `tags`, `create_time`, `user_id`, `status`) VALUES
(3, '南京鸭血粉丝汤', '南京经典小吃，汤头鲜，适合和盐水鸭一起安排在秦淮路线。', '/uploads/seed_034.jpg', '江苏', '南京', '秦淮区夫子庙周边', '地方小吃,老字号,美食为主', '2026-05-04 09:00:00', 1, 1),
(4, '苏州松鼠桂鱼', '酸甜口江南名菜，适合园林游之后体验苏帮菜。', '/uploads/seed_035.jpg', '江苏', '苏州', '姑苏区观前街', '特色餐厅,老字号', '2026-05-04 09:10:00', 2, 1),
(5, '青岛海鲜大排档', '蛤蜊、海虾和本地啤酒是青岛夜宵常见组合。', '/uploads/seed_036.jpg', '山东', '青岛', '市南区云霄路美食街', '海鲜,特色餐厅', '2026-05-04 09:20:00', 3, 1),
(6, '三亚清补凉', '椰奶、绿豆、芋圆和水果组合，适合海边消暑。', '/uploads/seed_037.jpg', '海南', '三亚', '吉阳区亚龙湾商业街', '甜品饮品,地方小吃', '2026-05-04 09:30:00', 4, 1),
(7, '乌镇定胜糕', '江南传统糕点，甜糯口感，适合边逛边吃。', '/uploads/seed_038.jpg', '浙江', '嘉兴', '桐乡市乌镇西栅景区内', '地方小吃,甜品饮品', '2026-05-04 09:40:00', 5, 1),
(8, '敦煌驴肉黄面', '敦煌代表性面食，黄面筋道，适合午餐。', '/uploads/seed_039.jpg', '甘肃', '敦煌', '敦煌夜市周边', '地方小吃,老字号', '2026-05-04 09:50:00', 6, 1),
(9, '哈尔滨锅包肉', '酸甜酥脆的东北菜代表，适合冬季旅行聚餐。', '/uploads/seed_040.jpg', '黑龙江', '哈尔滨', '道里区中央大街', '特色餐厅,老字号', '2026-05-04 10:00:00', 7, 1),
(10, '长沙臭豆腐', '长沙街头小吃代表，外酥里嫩，适合夜市打卡。', '/uploads/seed_041.jpg', '湖南', '长沙', '天心区坡子街', '地方小吃,美食为主', '2026-05-04 10:10:00', 8, 1),
(11, '待审核的海南椰子鸡', '用于后台美食审核流程展示。', '/uploads/seed_042.jpg', '海南', '三亚', '吉阳区迎宾路', '特色餐厅', '2026-05-04 10:20:00', 9, 0)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `description` = VALUES(`description`), `cover_image` = VALUES(`cover_image`), `province` = VALUES(`province`), `city` = VALUES(`city`), `address` = VALUES(`address`), `tags` = VALUES(`tags`), `user_id` = VALUES(`user_id`), `status` = VALUES(`status`);

-- Public trips and trip details.
INSERT INTO `trip` (`id`, `user_id`, `title`, `description`, `cover_image`, `tags`, `days`, `start_date`, `end_date`, `route_data`, `status`, `create_time`, `is_public`) VALUES
(3, 1, '南京秦淮周末两日行程', '夜游秦淮河，第二天补充博物馆或老门东。', '/uploads/seed_001.jpg', '江苏,南京,夜游,周末短途', 2, '2026-06-06', '2026-06-07', '[{"dayNum":1,"attractionId":21,"attractionName":"秦淮河画舫","description":"傍晚乘船夜游","sortOrder":0},{"dayNum":2,"attractionId":22,"attractionName":"江南贡院","description":"上午参观科举文化展","sortOrder":0}]', 1, '2026-05-05 09:00:00', 1),
(4, 2, '苏州园林慢游一日', '拙政园加平江路，轻松路线。', '/uploads/seed_002.jpg', '江苏,苏州,轻松休闲', 1, '2026-06-08', '2026-06-08', '[{"dayNum":1,"attractionId":23,"attractionName":"远香堂","description":"上午游览园林核心区","sortOrder":0},{"dayNum":1,"attractionId":24,"attractionName":"小飞虹","description":"拍摄廊桥水景","sortOrder":1}]', 1, '2026-05-05 09:10:00', 1),
(5, 3, '青岛崂山山海两日', '一天登山，一天看海和城市美食。', '/uploads/seed_004.jpg', '山东,青岛,登山徒步,海岛海滨', 2, '2026-06-10', '2026-06-11', '[{"dayNum":1,"attractionId":25,"attractionName":"太清游览区","description":"山海路线徒步","sortOrder":0},{"dayNum":2,"attractionId":26,"attractionName":"仰口海岸","description":"海边散步拍照","sortOrder":0}]', 1, '2026-05-05 09:20:00', 1),
(6, 4, '三亚亚龙湾亲子三日', '低强度亲子度假，海滩和雨林结合。', '/uploads/seed_006.jpg', '海南,三亚,亲子游,海滨度假', 3, '2026-06-15', '2026-06-17', '[{"dayNum":1,"attractionId":27,"attractionName":"亚龙湾沙滩","description":"沙滩适应和玩水","sortOrder":0},{"dayNum":2,"attractionId":28,"attractionName":"热带天堂森林公园","description":"雨林步道和观景","sortOrder":0}]', 1, '2026-05-05 09:30:00', 1),
(7, 5, '敦煌文化三日行程', '莫高窟、鸣沙山和夜市组合。', '/uploads/seed_007.jpg', '甘肃,敦煌,深度人文', 3, '2026-06-20', '2026-06-22', '[{"dayNum":1,"attractionId":31,"attractionName":"莫高窟九层楼","description":"预约参观莫高窟","sortOrder":0},{"dayNum":2,"attractionId":32,"attractionName":"鸣沙山月牙泉","description":"傍晚看日落","sortOrder":0}]', 1, '2026-05-05 09:40:00', 1),
(8, 6, '哈尔滨冰雪夜游两日', '冬季冰雪主题路线，适合亲子。', '/uploads/seed_009.jpg', '黑龙江,哈尔滨,亲子游,夜游', 2, '2026-12-20', '2026-12-21', '[{"dayNum":1,"attractionId":33,"attractionName":"冰雪主题城堡","description":"夜场灯光秀","sortOrder":0},{"dayNum":2,"attractionId":34,"attractionName":"雪雕艺术区","description":"白天看雪雕","sortOrder":0}]', 1, '2026-05-05 09:50:00', 1),
(9, 7, '长沙吃逛两日公开行程', '橘子洲、岳麓书院和夜市。', '/uploads/seed_011.jpg', '湖南,长沙,美食为主,城市观光', 2, '2026-06-25', '2026-06-26', '[{"dayNum":1,"attractionId":35,"attractionName":"橘子洲头","description":"傍晚散步","sortOrder":0},{"dayNum":2,"attractionId":36,"attractionName":"岳麓书院","description":"上午参观","sortOrder":0}]', 1, '2026-05-05 10:00:00', 1),
(10, 8, '乌镇西栅夜游一日', '下午入园，晚上看夜景。', '/uploads/seed_003.jpg', '浙江,嘉兴,夜游,周末短途', 1, '2026-06-28', '2026-06-28', '[{"dayNum":1,"attractionId":29,"attractionName":"西栅老街","description":"下午逛水乡","sortOrder":0},{"dayNum":1,"attractionId":30,"attractionName":"草木本色染坊","description":"拍照和体验","sortOrder":1}]', 1, '2026-05-05 10:10:00', 1)
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `title` = VALUES(`title`), `description` = VALUES(`description`), `cover_image` = VALUES(`cover_image`), `tags` = VALUES(`tags`), `days` = VALUES(`days`), `start_date` = VALUES(`start_date`), `end_date` = VALUES(`end_date`), `route_data` = VALUES(`route_data`), `status` = VALUES(`status`), `is_public` = VALUES(`is_public`);

INSERT INTO `trip_detail` (`id`, `trip_id`, `day_num`, `attraction_id`, `attraction_name`, `description`, `sort_order`) VALUES
(5, 3, 1, 21, '秦淮河画舫', '傍晚乘船夜游秦淮河', 0),
(6, 3, 2, 22, '江南贡院', '上午参观科举文化展', 0),
(7, 4, 1, 23, '远香堂', '拙政园核心区慢游', 0),
(8, 4, 1, 24, '小飞虹', '拍摄廊桥水景', 1),
(9, 5, 1, 25, '太清游览区', '山海路线徒步', 0),
(10, 5, 2, 26, '仰口海岸', '海边散步拍照', 0),
(11, 6, 1, 27, '亚龙湾沙滩', '沙滩玩水', 0),
(12, 6, 2, 28, '热带天堂森林公园', '雨林步道', 0),
(13, 7, 1, 31, '莫高窟九层楼', '预约参观莫高窟', 0),
(14, 7, 2, 32, '鸣沙山月牙泉', '傍晚看日落', 0),
(15, 8, 1, 33, '冰雪主题城堡', '夜场灯光秀', 0),
(16, 8, 2, 34, '雪雕艺术区', '白天看雪雕', 0),
(17, 9, 1, 35, '橘子洲头', '傍晚散步', 0),
(18, 9, 2, 36, '岳麓书院', '上午参观', 0),
(19, 10, 1, 29, '西栅老街', '下午逛水乡', 0),
(20, 10, 1, 30, '草木本色染坊', '拍照和体验', 1)
ON DUPLICATE KEY UPDATE `trip_id` = VALUES(`trip_id`), `day_num` = VALUES(`day_num`), `attraction_id` = VALUES(`attraction_id`), `attraction_name` = VALUES(`attraction_name`), `description` = VALUES(`description`), `sort_order` = VALUES(`sort_order`);

-- Homepage banners.
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `link_type`, `target_id`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(6, '秦淮夜色 周末慢游南京', '/uploads/seed_043.jpg', NULL, 1, 21, 6, 1, '2026-05-06 09:00:00', '2026-05-06 09:00:00'),
(7, '亚龙湾亲子海滨假期', '/uploads/seed_044.jpg', NULL, 1, 24, 7, 1, '2026-05-06 09:10:00', '2026-05-06 09:10:00'),
(8, '敦煌莫高窟 深度文化行', '/uploads/seed_045.jpg', NULL, 2, 16, 8, 1, '2026-05-06 09:20:00', '2026-05-06 09:20:00')
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `image_url` = VALUES(`image_url`), `link_url` = VALUES(`link_url`), `link_type` = VALUES(`link_type`), `target_id` = VALUES(`target_id`), `sort_order` = VALUES(`sort_order`), `status` = VALUES(`status`);

-- Collections for cards and recommendation signals.
INSERT INTO `collection` (`id`, `user_id`, `target_id`, `type`, `create_time`) VALUES
(9, 1, 21, 1, '2026-05-07 09:00:00'),
(10, 1, 22, 1, '2026-05-07 09:02:00'),
(11, 1, 12, 2, '2026-05-07 09:04:00'),
(12, 1, 3, 3, '2026-05-07 09:06:00'),
(13, 2, 22, 1, '2026-05-07 09:08:00'),
(14, 2, 25, 1, '2026-05-07 09:10:00'),
(15, 2, 13, 2, '2026-05-07 09:12:00'),
(16, 3, 23, 1, '2026-05-07 09:14:00'),
(17, 3, 14, 2, '2026-05-07 09:16:00'),
(18, 3, 5, 3, '2026-05-07 09:18:00'),
(19, 4, 24, 1, '2026-05-07 09:20:00'),
(20, 4, 15, 2, '2026-05-07 09:22:00'),
(21, 5, 26, 1, '2026-05-07 09:24:00'),
(22, 5, 16, 2, '2026-05-07 09:26:00'),
(23, 6, 27, 1, '2026-05-07 09:28:00'),
(24, 6, 17, 2, '2026-05-07 09:30:00'),
(25, 7, 28, 1, '2026-05-07 09:32:00'),
(26, 7, 18, 2, '2026-05-07 09:34:00'),
(27, 8, 25, 1, '2026-05-07 09:36:00'),
(28, 8, 19, 2, '2026-05-07 09:38:00'),
(29, 9, 24, 1, '2026-05-07 09:40:00'),
(30, 10, 23, 1, '2026-05-07 09:42:00')
ON DUPLICATE KEY UPDATE `create_time` = VALUES(`create_time`);

-- Comments and note interactions.
INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES
(4, 21, 'scenic', 2, '晚上灯光很漂亮，适合第一次来南京的人。', NULL, 6, 1, '2026-05-08 09:00:00'),
(5, 22, 'scenic', 3, '园林细节很多，建议早点入园。', NULL, 4, 1, '2026-05-08 09:10:00'),
(6, 23, 'scenic', 4, '山海线很舒服，风比较大。', NULL, 5, 1, '2026-05-08 09:20:00'),
(7, 24, 'scenic', 5, '亲子玩水方便，防晒一定要做好。', NULL, 8, 1, '2026-05-08 09:30:00'),
(8, 16, 'strategy', 6, '莫高窟预约提醒很实用。', NULL, 3, 1, '2026-05-08 09:40:00'),
(9, 18, 'strategy', 8, '长沙这条路线吃逛都覆盖到了。', NULL, 7, 1, '2026-05-08 09:50:00')
ON DUPLICATE KEY UPDATE `target_id` = VALUES(`target_id`), `target_type` = VALUES(`target_type`), `user_id` = VALUES(`user_id`), `content` = VALUES(`content`), `like_count` = VALUES(`like_count`), `status` = VALUES(`status`);

INSERT INTO `note_comment` (`id`, `note_id`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `create_time`) VALUES
(2, 5, 3, '秦淮河夜游确实适合周末。', NULL, 3, 1, '2026-05-08 10:00:00'),
(3, 6, 4, '拙政园上午人少很多。', NULL, 2, 1, '2026-05-08 10:10:00'),
(4, 8, 5, '亲子出行参考到了，谢谢。', NULL, 5, 1, '2026-05-08 10:20:00'),
(5, 10, 7, '冬天去哈尔滨这个提醒很实用。', NULL, 4, 1, '2026-05-08 10:30:00')
ON DUPLICATE KEY UPDATE `note_id` = VALUES(`note_id`), `user_id` = VALUES(`user_id`), `content` = VALUES(`content`), `like_count` = VALUES(`like_count`), `status` = VALUES(`status`);

INSERT INTO `note_like` (`id`, `note_id`, `user_id`, `create_time`) VALUES
(2, 5, 2, '2026-05-08 11:00:00'),
(3, 5, 4, '2026-05-08 11:02:00'),
(4, 6, 1, '2026-05-08 11:04:00'),
(5, 7, 3, '2026-05-08 11:06:00'),
(6, 8, 4, '2026-05-08 11:08:00'),
(7, 9, 6, '2026-05-08 11:10:00'),
(8, 10, 7, '2026-05-08 11:12:00'),
(9, 11, 8, '2026-05-08 11:14:00'),
(10, 12, 9, '2026-05-08 11:16:00')
ON DUPLICATE KEY UPDATE `create_time` = VALUES(`create_time`);

-- Orders for order center and paid behavior signals.
INSERT INTO `order` (`id`, `order_no`, `user_id`, `scenic_id`, `ticket_id`, `quantity`, `total_amount`, `visit_date`, `status`, `pay_time`, `pay_type`, `alipay_trade_no`, `create_time`) VALUES
(4, 'T202605090001', 1, 21, 42, 2, 160.00, '2026-06-06', 1, '2026-05-09 09:05:00', '支付宝', '202605092200000001', '2026-05-09 09:00:00'),
(5, 'T202605090002', 2, 22, 43, 1, 80.00, '2026-06-08', 2, '2026-05-09 09:15:00', '微信', '202605092200000002', '2026-05-09 09:10:00'),
(6, 'T202605090003', 3, 23, 44, 2, 240.00, '2026-06-10', 1, '2026-05-09 09:25:00', '支付宝', '202605092200000003', '2026-05-09 09:20:00'),
(7, 'T202605090004', 4, 24, 45, 3, 414.00, '2026-06-15', 1, '2026-05-09 09:35:00', '微信', '202605092200000004', '2026-05-09 09:30:00'),
(8, 'T202605090005', 5, 26, 47, 1, 238.00, '2026-06-20', 2, '2026-05-09 09:45:00', '支付宝', '202605092200000005', '2026-05-09 09:40:00'),
(9, 'T202605090006', 6, 27, 48, 2, 656.00, '2026-12-20', 0, NULL, NULL, NULL, '2026-05-09 09:50:00')
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `scenic_id` = VALUES(`scenic_id`), `ticket_id` = VALUES(`ticket_id`), `quantity` = VALUES(`quantity`), `total_amount` = VALUES(`total_amount`), `visit_date` = VALUES(`visit_date`), `status` = VALUES(`status`), `pay_time` = VALUES(`pay_time`), `pay_type` = VALUES(`pay_type`), `alipay_trade_no` = VALUES(`alipay_trade_no`);

-- User behavior matrix for collaborative filtering.
INSERT INTO `user_behavior` (`id`, `user_id`, `target_id`, `target_type`, `behavior_type`, `score`, `create_time`) VALUES
(47, 1, 21, 1, 1, 1, '2026-05-10 09:00:00'),
(48, 1, 21, 1, 2, 2, '2026-05-10 09:01:00'),
(49, 1, 12, 2, 3, 3, '2026-05-10 09:02:00'),
(50, 1, 3, 3, 1, 1, '2026-05-10 09:03:00'),
(51, 1, 3, 3, 2, 2, '2026-05-10 09:04:00'),
(52, 2, 22, 1, 1, 1, '2026-05-10 09:05:00'),
(53, 2, 22, 1, 2, 2, '2026-05-10 09:06:00'),
(54, 2, 13, 2, 3, 3, '2026-05-10 09:07:00'),
(55, 2, 4, 3, 1, 1, '2026-05-10 09:08:00'),
(56, 3, 23, 1, 1, 1, '2026-05-10 09:09:00'),
(57, 3, 23, 1, 2, 2, '2026-05-10 09:10:00'),
(58, 3, 14, 2, 3, 3, '2026-05-10 09:11:00'),
(59, 3, 5, 3, 2, 2, '2026-05-10 09:12:00'),
(60, 4, 24, 1, 1, 1, '2026-05-10 09:13:00'),
(61, 4, 24, 1, 2, 2, '2026-05-10 09:14:00'),
(62, 4, 15, 2, 3, 3, '2026-05-10 09:15:00'),
(63, 4, 6, 3, 2, 2, '2026-05-10 09:16:00'),
(64, 5, 26, 1, 1, 1, '2026-05-10 09:17:00'),
(65, 5, 26, 1, 2, 2, '2026-05-10 09:18:00'),
(66, 5, 16, 2, 3, 3, '2026-05-10 09:19:00'),
(67, 5, 7, 3, 2, 2, '2026-05-10 09:20:00'),
(68, 6, 27, 1, 1, 1, '2026-05-10 09:21:00'),
(69, 6, 27, 1, 2, 2, '2026-05-10 09:22:00'),
(70, 6, 17, 2, 3, 3, '2026-05-10 09:23:00'),
(71, 6, 8, 3, 2, 2, '2026-05-10 09:24:00'),
(72, 7, 28, 1, 1, 1, '2026-05-10 09:25:00'),
(73, 7, 28, 1, 2, 2, '2026-05-10 09:26:00'),
(74, 7, 18, 2, 3, 3, '2026-05-10 09:27:00'),
(75, 7, 9, 3, 2, 2, '2026-05-10 09:28:00'),
(76, 8, 25, 1, 1, 1, '2026-05-10 09:29:00'),
(77, 8, 25, 1, 2, 2, '2026-05-10 09:30:00'),
(78, 8, 19, 2, 3, 3, '2026-05-10 09:31:00'),
(79, 8, 10, 3, 2, 2, '2026-05-10 09:32:00'),
(80, 9, 24, 1, 1, 1, '2026-05-10 09:33:00'),
(81, 9, 24, 1, 2, 2, '2026-05-10 09:34:00'),
(82, 9, 15, 2, 3, 3, '2026-05-10 09:35:00'),
(83, 9, 6, 4, 1, 1, '2026-05-10 09:36:00'),
(84, 10, 23, 1, 1, 1, '2026-05-10 09:37:00'),
(85, 10, 23, 1, 2, 2, '2026-05-10 09:38:00'),
(86, 10, 14, 2, 3, 3, '2026-05-10 09:39:00'),
(87, 10, 5, 4, 1, 1, '2026-05-10 09:40:00'),
(88, 11, 21, 1, 1, 1, '2026-05-10 09:41:00'),
(89, 11, 22, 1, 1, 1, '2026-05-10 09:42:00'),
(90, 11, 12, 2, 2, 2, '2026-05-10 09:43:00'),
(91, 11, 13, 2, 3, 3, '2026-05-10 09:44:00'),
(92, 12, 24, 1, 1, 1, '2026-05-10 09:45:00'),
(93, 12, 26, 1, 2, 2, '2026-05-10 09:46:00'),
(94, 12, 15, 2, 3, 3, '2026-05-10 09:47:00'),
(95, 12, 16, 2, 1, 1, '2026-05-10 09:48:00')
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `target_id` = VALUES(`target_id`), `target_type` = VALUES(`target_type`), `behavior_type` = VALUES(`behavior_type`), `score` = VALUES(`score`), `create_time` = VALUES(`create_time`);

-- Feedback examples for admin handling.
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `create_time`) VALUES
(11, 1, '希望景区详情页可以展示更多交通提示。', '已记录，后续会补充交通和停车信息。', 1, '2026-05-11 09:00:00'),
(12, 3, '发布游记时希望图片和正文分开填写。', '该功能已调整为独立图片上传和正文输入。', 1, '2026-05-11 09:10:00'),
(13, 6, '推荐内容可以多展示一些冬季路线。', NULL, 0, '2026-05-11 09:20:00')
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`), `content` = VALUES(`content`), `reply` = VALUES(`reply`), `status` = VALUES(`status`);

-- Keep aggregate counters aligned with inserted interaction data.
UPDATE `scenic_area` s
SET s.`collect_count` = GREATEST(s.`collect_count`, (SELECT COUNT(*) FROM `collection` c WHERE c.`type` = 1 AND c.`target_id` = s.`id`)),
    s.`view_count` = GREATEST(s.`view_count`, (SELECT COUNT(*) FROM `user_behavior` b WHERE b.`target_type` = 1 AND b.`target_id` = s.`id`) * 100);

UPDATE `attraction` a
SET a.`collect_count` = GREATEST(a.`collect_count`, (SELECT COUNT(*) FROM `collection` c WHERE c.`type` = 5 AND c.`target_id` = a.`id`)),
    a.`view_count` = GREATEST(a.`view_count`, (SELECT COUNT(*) FROM `user_behavior` b WHERE b.`target_type` = 5 AND b.`target_id` = a.`id`) * 80);

UPDATE `strategy` s
SET s.`like_count` = GREATEST(s.`like_count`, (SELECT COUNT(*) FROM `collection` c WHERE c.`type` = 2 AND c.`target_id` = s.`id`)),
    s.`view_count` = GREATEST(s.`view_count`, (SELECT COUNT(*) FROM `user_behavior` b WHERE b.`target_type` = 2 AND b.`target_id` = s.`id`) * 90);

UPDATE `travel_note` n
SET n.`like_count` = GREATEST(n.`like_count`, (SELECT COUNT(*) FROM `note_like` l WHERE l.`note_id` = n.`id`));

ALTER TABLE `tag` AUTO_INCREMENT = 48;
ALTER TABLE `scenic_area` AUTO_INCREMENT = 29;
ALTER TABLE `attraction` AUTO_INCREMENT = 37;
ALTER TABLE `ticket` AUTO_INCREMENT = 52;
ALTER TABLE `strategy` AUTO_INCREMENT = 22;
ALTER TABLE `travel_note` AUTO_INCREMENT = 13;
ALTER TABLE `food` AUTO_INCREMENT = 12;
ALTER TABLE `trip` AUTO_INCREMENT = 11;
ALTER TABLE `trip_detail` AUTO_INCREMENT = 21;
ALTER TABLE `banner` AUTO_INCREMENT = 9;
ALTER TABLE `collection` AUTO_INCREMENT = 31;
ALTER TABLE `comment` AUTO_INCREMENT = 10;
ALTER TABLE `note_comment` AUTO_INCREMENT = 6;
ALTER TABLE `note_like` AUTO_INCREMENT = 11;
ALTER TABLE `order` AUTO_INCREMENT = 10;
ALTER TABLE `user_behavior` AUTO_INCREMENT = 96;
ALTER TABLE `feedback` AUTO_INCREMENT = 14;

SET FOREIGN_KEY_CHECKS = 1;
