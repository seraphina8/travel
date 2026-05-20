-- Backfill tags for existing demo/business data.
-- Safe to run after travel-tag-update.sql or after manually adding tags columns.

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
WHERE `tags` IS NULL OR `tags` NOT LIKE '%轻松休闲%' AND `tags` NOT LIKE '%美食为主%' AND `tags` NOT LIKE '%深度人文%' AND `tags` NOT LIKE '%经典打卡%';

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
