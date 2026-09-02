-- ============================================================
-- 个人全栈数字中心系统 - 数据库初始化脚本
-- 数据库：personalcenter  字符集：utf8mb4  引擎：InnoDB
-- 共 8 张表：user / category / article / comment
--            interview_experience / diary / file_record / lolita_garment
-- ============================================================

-- 创建数据库（若已存在请忽略此句）
CREATE DATABASE IF NOT EXISTS `personalcenter` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `personalcenter`;

-- 1. 用户表
CREATE TABLE `user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(50) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL COMMENT 'BCrypt加密',
  `nickname` varchar(50) NOT NULL COMMENT '昵称（注册必填，展示用户名字）',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 分类表（供技术博客使用）
CREATE TABLE `category` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) UNIQUE NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 3. 技术文章表（公开，面向所有访客）
CREATE TABLE `article` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'Markdown内容',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `view_count` int DEFAULT 0,
  `like_count` int DEFAULT 0,
  `category_id` bigint,
  `tags` varchar(255) DEFAULT NULL COMMENT '标签（英文逗号分隔）',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `status` tinyint DEFAULT 1 COMMENT '0草稿 1已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术文章表';

-- 4. 评论表（多态设计，同时服务于文章和面试经验）
CREATE TABLE `comment` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `content` text NOT NULL,
  `biz_type` varchar(20) NOT NULL COMMENT 'ARTICLE 或 INTERVIEW',
  `biz_id` bigint NOT NULL COMMENT '对应文章ID或面试ID',
  `user_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT 0 COMMENT '0表示顶级评论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 5. 面试经验表（支持公开/私有）
CREATE TABLE `interview_experience` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `company_name` varchar(100) NOT NULL,
  `job_position` varchar(100) NOT NULL,
  `interview_round` varchar(20) DEFAULT NULL COMMENT '一面/二面/HR面',
  `question_text` text COMMENT '面试问题',
  `my_answer` text COMMENT '我的回答',
  `reflection` text COMMENT '复盘反思/踩坑点',
  `is_success` tinyint DEFAULT 0 COMMENT '0待定 1通过 2未过',
  `interview_date` date DEFAULT NULL,
  `is_public` tinyint DEFAULT 0 COMMENT '0仅自己 1公开',
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试经验表';

-- 6. 私密日记表（绝对私有，无公开字段）
CREATE TABLE `diary` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL COMMENT '支持Markdown',
  `mood` varchar(20) DEFAULT NULL COMMENT '开心/平静/emo/奋斗',
  `weather` varchar(20) DEFAULT NULL COMMENT '晴/雨/阴/雪',
  `diary_date` date NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私密日记表';

-- 7. 文件/照片记录表（只存元数据，不存二进制）
CREATE TABLE `file_record` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `original_name` varchar(255) NOT NULL,
  `file_url` varchar(500) NOT NULL COMMENT '访问URL',
  `file_size` bigint DEFAULT 0 COMMENT '字节',
  `file_type` varchar(50) DEFAULT NULL COMMENT '如 image/png',
  `biz_type` varchar(50) DEFAULT 'COMMON' COMMENT '关联业务(avatar/article_cover/diary_img/lolita_img)',
  `biz_id` bigint DEFAULT NULL COMMENT '关联业务主键ID',
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

-- 8. Lolita 服饰收藏表（兴趣管理）
CREATE TABLE `lolita_garment` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '服饰名称/款式名',
  `brand` varchar(50) DEFAULT NULL COMMENT '品牌',
  `series` varchar(50) DEFAULT NULL COMMENT '系列/印花名',
  `category` varchar(20) NOT NULL COMMENT 'OP/JSK/SK/衬衫/配饰/假发/鞋/其他',
  `color` varchar(30) DEFAULT NULL COMMENT '主色',
  `size` varchar(20) DEFAULT NULL COMMENT '尺码',
  `purchase_date` date DEFAULT NULL,
  `purchase_price` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT '现货' COMMENT '预约中/在途/待补尾款/现货/已出/已送人',
  `balance_due` decimal(10,2) DEFAULT NULL COMMENT '待补金额（状态为待补尾款时填写）',
  `wear_count` int DEFAULT 0 COMMENT '穿着次数',
  `location` varchar(100) DEFAULT NULL COMMENT '存放位置',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '主图URL',
  `images` json DEFAULT NULL COMMENT '多图JSON数组',
  `note` text DEFAULT NULL COMMENT '个人笔记/种草心得',
  `is_public` tinyint DEFAULT 0 COMMENT '0仅自己 1公开',
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Lolita服饰收藏表';
-- 默认分类
INSERT INTO `category` (`name`) VALUES ('技术'), ('生活'), ('随笔'), ('收藏');

-- 9. 联系方式配置表（左侧 Connect 卡片：公开读取、登录后更新）
CREATE TABLE `contact_info` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `contact_key` varchar(50) UNIQUE NOT NULL COMMENT '键：bilibili/github/tiktok/red/qq/mail',
  `contact_value` varchar(255) NOT NULL DEFAULT '' COMMENT '联系方式内容',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系方式配置表';

-- 默认联系方式（占位，登录后可在前端浮窗双击编辑）
INSERT INTO `contact_info` (`contact_key`, `contact_value`) VALUES
('bilibili', 'space.bilibili.com/待补充'),
('github', 'github.com/待补充'),
('tiktok', '@待补充'),
('red', 'xiaohongshu.com/user/待补充'),
('qq', 'QQ：待补充'),
('mail', '待补充@example.com');

-- 10. 文章点赞记录表（一人一赞，UNIQUE(article_id,user_id) 去重）
CREATE TABLE `article_like` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  FOREIGN KEY (`article_id`) REFERENCES `article`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞记录表';

-- 11. 首页播放器歌曲表（公开播放，登录后新增/删除）
CREATE TABLE `music` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '歌曲标题',
  `artist` varchar(50) DEFAULT 'Lynn' COMMENT '艺术家',
  `url` varchar(500) NOT NULL COMMENT '音频URL（本地 /uploads/... 或 OSS）',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面URL',
  `sort` int DEFAULT 0 COMMENT '排序（越小越靠前）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页播放器歌曲表';

-- 12. 好友链接表（公开展示，登录后管理；申请提交时 visible=0 待审核，user_id 可空）
CREATE TABLE `friend_link` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '站点名',
  `url` varchar(500) NOT NULL COMMENT '友链地址',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `description` varchar(200) DEFAULT NULL COMMENT '一句话介绍',
  `sort` int DEFAULT 0 COMMENT '排序（越小越靠前）',
  `visible` tinyint DEFAULT 1 COMMENT '1显示 0隐藏/待审核',
  `user_id` bigint DEFAULT NULL COMMENT '录入者（申请提交可为空）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友链接表';

-- 13. 作品集表（公开展示，登录后管理）
CREATE TABLE `project` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `description` varchar(500) DEFAULT NULL COMMENT '项目简介',
  `url` varchar(500) DEFAULT NULL COMMENT '在线地址',
  `github_url` varchar(500) DEFAULT NULL COMMENT 'GitHub链接',
  `tech` varchar(200) DEFAULT NULL COMMENT '技术栈（逗号分隔）',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `sort` int DEFAULT 0 COMMENT '排序（越小越靠前）',
  `visible` tinyint DEFAULT 1 COMMENT '1显示 0隐藏',
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品集表';

-- 14. 留言板表（公开读取 + 游客留言，无需登录）
CREATE TABLE `guestbook` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `nickname` varchar(30) NOT NULL COMMENT '昵称（游客自填）',
  `content` varchar(300) NOT NULL COMMENT '留言内容',
  `ip` varchar(45) DEFAULT NULL COMMENT '留言IP（不对外展示）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言板表';