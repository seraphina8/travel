-- Rating fields and corresponding seed data.
-- Run this after the base travel.sql if your database already exists.

ALTER TABLE `scenic_area`
  ADD COLUMN `rating` decimal(2,1) NOT NULL DEFAULT 0.0 COMMENT '平均评分' AFTER `collect_count`,
  ADD COLUMN `rating_count` int NOT NULL DEFAULT 0 COMMENT '评分人数' AFTER `rating`;

ALTER TABLE `attraction`
  ADD COLUMN `rating` decimal(2,1) NOT NULL DEFAULT 0.0 COMMENT '平均评分' AFTER `collect_count`,
  ADD COLUMN `rating_count` int NOT NULL DEFAULT 0 COMMENT '评分人数' AFTER `rating`;

ALTER TABLE `comment`
  ADD COLUMN `rating` tinyint DEFAULT NULL COMMENT '评分，1-5分' AFTER `content`;

DELETE FROM `comment`;
ALTER TABLE `comment` AUTO_INCREMENT = 1;

INSERT INTO `comment` (`id`, `target_id`, `target_type`, `user_id`, `content`, `rating`, `parent_id`, `like_count`, `status`, `create_time`) VALUES
(1, 1, 'scenic', 11, '故宫路线清晰，展览内容很丰富，建议早上入园。', 5, NULL, 3, 1, '2026-01-03 09:20:00'),
(2, 1, 'scenic', 12, '建筑很震撼，人流较多但整体体验好。', 5, NULL, 1, 1, '2026-01-05 14:10:00'),
(3, 1, 'scenic', 13, '门票和交通都方便，适合第一次来北京。', 4, NULL, 0, 1, '2026-01-08 16:30:00'),
(4, 2, 'scenic', 11, '八达岭维护很好，登城体验完整。', 5, NULL, 2, 1, '2026-01-10 10:12:00'),
(5, 2, 'scenic', 14, '风景开阔，冬天风比较大。', 4, NULL, 1, 1, '2026-01-12 15:00:00'),
(6, 3, 'scenic', 12, '西湖适合慢慢走，免费开放很友好。', 5, NULL, 4, 1, '2026-01-13 18:20:00'),
(7, 3, 'scenic', 15, '景色好，但节假日拥挤。', 4, NULL, 1, 1, '2026-01-15 11:05:00'),
(8, 4, 'scenic', 13, '黄山云海很惊艳，登山强度偏高。', 5, NULL, 2, 1, '2026-01-17 09:45:00'),
(9, 4, 'scenic', 16, '索道和指示牌完善，适合两日游。', 5, NULL, 0, 1, '2026-01-18 13:25:00'),
(10, 5, 'scenic', 14, '张家界地貌独特，拍照非常出片。', 5, NULL, 5, 1, '2026-01-20 10:30:00'),
(11, 6, 'scenic', 15, '九寨沟水色很美，景区接驳效率高。', 5, NULL, 3, 1, '2026-01-22 12:40:00'),
(12, 7, 'scenic', 16, '丽江夜晚氛围好，商业化稍明显。', 4, NULL, 2, 1, '2026-01-23 20:18:00'),
(13, 8, 'scenic', 11, '漓江山水很经典，竹筏体验不错。', 5, NULL, 1, 1, '2026-01-25 08:55:00'),
(14, 9, 'scenic', 12, '鼓浪屿适合步行，建筑很有特色。', 4, NULL, 1, 1, '2026-01-26 17:35:00'),
(15, 10, 'scenic', 13, '泰山日出值得早起，路线成熟。', 5, NULL, 2, 1, '2026-01-28 06:50:00'),
(16, 2, 'attraction', 11, '故宫博物院馆藏丰富，建议预留半天以上。', 5, NULL, 2, 1, '2026-02-01 10:00:00'),
(17, 2, 'attraction', 12, '展线清楚，适合亲子和历史爱好者。', 5, NULL, 1, 1, '2026-02-02 11:30:00'),
(18, 7, 'attraction', 13, '西湖核心区很美，傍晚体验最好。', 5, NULL, 3, 1, '2026-02-03 18:10:00'),
(19, 7, 'attraction', 14, '人多但景色稳定在线。', 4, NULL, 0, 1, '2026-02-04 15:22:00'),
(20, 16, 'attraction', 15, '洪崖洞夜景漂亮，拍照点很多。', 5, NULL, 4, 1, '2026-02-05 20:05:00'),
(21, 16, 'attraction', 16, '交通略拥堵，夜景值得。', 4, NULL, 1, 1, '2026-02-06 21:15:00'),
(22, 14, 'attraction', 11, '广州塔视野开阔，夜景体验更好。', 5, NULL, 2, 1, '2026-02-07 19:40:00'),
(23, 5, 'attraction', 12, '东方明珠地标感强，排队时间稍长。', 4, NULL, 1, 1, '2026-02-08 16:28:00'),
(24, 8, 'attraction', 13, '灵隐寺环境清幽，文化氛围好。', 5, NULL, 3, 1, '2026-02-09 09:35:00');

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
