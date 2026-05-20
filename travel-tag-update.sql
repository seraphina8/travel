-- Global tag support migration.
-- Run this once on the existing travel database before using tag filters.

ALTER TABLE `scenic_area`
  ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔';

ALTER TABLE `attraction`
  ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔';

ALTER TABLE `food`
  ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔';

ALTER TABLE `trip`
  ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔';

ALTER TABLE `travel_note`
  ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔';

INSERT INTO `tag` (`name`, `type`, `sort_order`, `status`) VALUES
('自然风光', 1, 1, 1),
('人文古迹', 1, 2, 1),
('亲子游', 1, 3, 1),
('登山徒步', 1, 4, 1),
('城市观光', 1, 5, 1),
('海岛海滨', 1, 6, 1),
('北京', 2, 1, 1),
('上海', 2, 2, 1),
('杭州', 2, 3, 1),
('成都', 2, 4, 1),
('云南', 2, 5, 1),
('西藏', 2, 6, 1),
('新疆', 2, 7, 1),
('海南', 2, 8, 1),
('经典打卡', 3, 1, 1),
('轻松休闲', 3, 2, 1),
('深度人文', 3, 3, 1),
('美食为主', 3, 4, 1),
('周末短途', 3, 5, 1),
('家庭出行', 3, 6, 1),
('地方小吃', 4, 1, 1),
('特色餐厅', 4, 2, 1),
('火锅烧烤', 4, 3, 1),
('甜品饮品', 4, 4, 1),
('老字号', 4, 5, 1),
('夜市', 4, 6, 1),
('摄影打卡', 5, 1, 1),
('旅行故事', 5, 2, 1),
('避坑经验', 5, 3, 1),
('交通住宿', 5, 4, 1),
('小众路线', 5, 5, 1),
('省钱攻略', 5, 6, 1);

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
