-- ========================================
-- 情侣互动平台初始化数据
-- ========================================

USE couple_platform;

-- ========================================
-- 初始化管理员账号
-- ========================================

INSERT INTO admins (username, password, real_name, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '系统管理员', 'admin@couple-platform.com', 'super_admin', 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '运营管理员', 'operator@couple-platform.com', 'admin', 1);
-- 默认密码为：admin123456

-- ========================================
-- 初始化测试用户数据
-- ========================================

INSERT INTO users (phone, username, password, nickname, gender, status, pair_code) VALUES
('13800138001', 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '小明', 1, 1, 'PAIR001A'),
('13800138002', 'user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '小红', 2, 1, 'PAIR001B'),
('13800138003', 'user003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '小李', 1, 1, 'PAIR002A'),
('13800138004', 'user004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGf2krfOFrOmhqrK4GUwI7S8q', '小张', 2, 1, 'PAIR002B');
-- 默认密码为：123456

-- 配对用户1和用户2
UPDATE users SET is_paired = TRUE, partner_id = 2, pair_date = NOW() WHERE id = 1;
UPDATE users SET is_paired = TRUE, partner_id = 1, pair_date = NOW() WHERE id = 2;

-- ========================================
-- 初始化用户设置
-- ========================================

INSERT INTO user_settings (user_id, theme_mode, primary_color, background_color, notification_enabled) VALUES
(1, 'light', '#007AFF', '#FFFFFF', TRUE),
(2, 'light', '#FF3B30', '#F2F2F7', TRUE),
(3, 'dark', '#30D158', '#1C1C1E', TRUE),
(4, 'light', '#5856D6', '#FFFFFF', TRUE);

-- ========================================
-- 初始化测试订单数据
-- ========================================

INSERT INTO orders (order_no, creator_id, receiver_id, title, description, type, status, total_amount, note) VALUES
('ORD202501150001', 1, 2, '晚餐订单', '今晚想吃火锅，帮我点一下吧~', 'food', 'pending', 128.50, '微辣，不要香菜'),
('ORD202501150002', 2, 1, '奶茶外卖', '想喝奶茶了，半糖去冰', 'food', 'completed', 25.00, '珍珠奶茶'),
('ORD202501150003', 1, 2, '购物清单', '帮我买点日用品', 'shopping', 'processing', 89.90, '洗发水和护发素'),
('ORD202501150004', 2, 1, '生日礼物', '下周是我生日，想要个惊喜', 'other', 'accepted', 299.00, '自己选择就好');

-- ========================================
-- 初始化订单项数据
-- ========================================

INSERT INTO order_items (order_id, name, description, quantity, unit_price, total_price) VALUES
-- 订单1的商品项
(1, '番茄火锅底料', '微辣番茄锅底', 1, 28.00, 28.00),
(1, '肥牛卷', '新鲜肥牛', 2, 35.00, 70.00),
(1, '蔬菜拼盘', '时令蔬菜', 1, 18.50, 18.50),
(1, '手工面条', '现做面条', 1, 12.00, 12.00),

-- 订单2的商品项
(2, '珍珠奶茶', '经典珍珠奶茶，半糖去冰', 1, 25.00, 25.00),

-- 订单3的商品项
(3, '洗发水', '某品牌洗发水 500ml', 1, 45.90, 45.90),
(3, '护发素', '配套护发素 500ml', 1, 44.00, 44.00),

-- 订单4的商品项
(4, '神秘礼物', '生日惊喜礼物', 1, 299.00, 299.00);

-- ========================================
-- 初始化订单评价数据
-- ========================================

INSERT INTO order_evaluations (order_id, evaluator_id, rating, comment, emojis, tags) VALUES
(2, 1, 5, '奶茶很好喝，配送也很快！', '["😋", "👍", "❤️"]', '["好喝", "快速", "贴心"]');

-- ========================================
-- 初始化测试问卷数据
-- ========================================

INSERT INTO surveys (title, description, creator_id, target_id, type, status, is_anonymous, allow_multiple) VALUES
('了解你的口味偏好', '想了解一下你平时喜欢吃什么类型的食物', 1, 2, 'custom', 'published', FALSE, FALSE),
('周末活动调查', '看看这个周末你想做什么', 2, 1, 'custom', 'published', FALSE, FALSE),
('情侣关系测试', '测试一下我们的默契程度', 1, 2, 'template', 'draft', FALSE, TRUE);

-- ========================================
-- 初始化问卷问题数据
-- ========================================

-- 问卷1的问题
INSERT INTO survey_questions (survey_id, question_text, question_type, is_required, sort_order, options) VALUES
(1, '你最喜欢的菜系是？', 'single_choice', TRUE, 1, '["川菜", "粤菜", "湘菜", "东北菜", "西餐", "日料", "韩料", "其他"]'),
(1, '你能接受的辣度等级？', 'single_choice', TRUE, 2, '["不吃辣", "微辣", "中辣", "重辣", "变态辣"]'),
(1, '你喜欢的饮品类型有哪些？', 'multiple_choice', FALSE, 3, '["奶茶", "咖啡", "果汁", "汽水", "茶类", "酒类", "白开水"]'),
(1, '对于食物，你还有什么特殊的偏好吗？', 'text', FALSE, 4, NULL);

-- 问卷2的问题
INSERT INTO survey_questions (survey_id, question_text, question_type, is_required, sort_order, options) VALUES
(2, '这个周末你最想做的事情是？', 'single_choice', TRUE, 1, '["在家看电影", "出去逛街", "户外运动", "朋友聚会", "学习充电", "睡觉休息"]'),
(2, '如果出去玩，你希望去哪里？', 'multiple_choice', FALSE, 2, '["电影院", "购物中心", "公园", "游乐场", "博物馆", "餐厅", "咖啡厅"]'),
(2, '你对这个周末的期待程度？', 'rating', TRUE, 3, '{"min": 1, "max": 5, "labels": ["不期待", "一般", "比较期待", "很期待", "超级期待"]}');

-- 问卷3的问题（情侣关系测试）
INSERT INTO survey_questions (survey_id, question_text, question_type, is_required, sort_order, options) VALUES
(3, '你认为在感情中最重要的是什么？', 'single_choice', TRUE, 1, '["信任", "沟通", "理解", "陪伴", "浪漫", "支持"]'),
(3, '你们在一起最喜欢做的事情有哪些？', 'multiple_choice', TRUE, 2, '["看电影", "逛街", "做饭", "旅行", "运动", "聊天", "玩游戏"]'),
(3, '你对我们的关系满意度？', 'rating', TRUE, 3, '{"min": 1, "max": 10, "labels": ["完全不满意", "非常满意"]}'),
(3, '你想对我说的话', 'text', FALSE, 4, NULL);

-- ========================================
-- 初始化问卷回答数据
-- ========================================

INSERT INTO survey_responses (survey_id, respondent_id, response_data, completion_time, is_completed) VALUES
(1, 2, '{"1": "川菜", "2": "中辣", "3": ["奶茶", "咖啡", "果汁"], "4": "我比较喜欢甜食，不太能吃太油腻的"}', 120, TRUE);

-- ========================================
-- 初始化消息数据
-- ========================================

INSERT INTO messages (sender_id, receiver_id, type, title, content, data) VALUES
(NULL, 1, 'system', '欢迎使用情侣互动平台', '欢迎使用我们的平台！希望能为你们的感情增添更多乐趣。', NULL),
(NULL, 2, 'system', '欢迎使用情侣互动平台', '欢迎使用我们的平台！希望能为你们的感情增添更多乐趣。', NULL),
(1, 2, 'order', '新订单提醒', '小明创建了一个新订单：晚餐订单', '{"order_id": 1, "order_no": "ORD202501150001"}'),
(2, 1, 'order', '订单已完成', '你的订单"奶茶外卖"已完成', '{"order_id": 2, "order_no": "ORD202501150002"}'),
(1, 2, 'survey', '新问卷邀请', '小明邀请你填写问卷：了解你的口味偏好', '{"survey_id": 1}'),
(2, 1, 'survey', '问卷回答完成', '小红已完成问卷"了解你的口味偏好"', '{"survey_id": 1, "response_id": 1}');

-- 标记部分消息为已读
UPDATE messages SET is_read = TRUE, read_time = NOW() WHERE id IN (1, 2, 4, 6);

-- ========================================
-- 创建数据库索引优化查询性能
-- ========================================

-- 复合索引优化
CREATE INDEX idx_users_pair_status ON users(is_paired, status);
CREATE INDEX idx_orders_creator_status ON orders(creator_id, status);
CREATE INDEX idx_orders_receiver_status ON orders(receiver_id, status);
CREATE INDEX idx_surveys_creator_status ON surveys(creator_id, status);
CREATE INDEX idx_messages_receiver_read ON messages(receiver_id, is_read);

-- 日期范围查询优化
CREATE INDEX idx_orders_created_status ON orders(created_time, status);
CREATE INDEX idx_surveys_created_status ON surveys(created_time, status);
CREATE INDEX idx_messages_created_type ON messages(created_time, type);

-- ========================================
-- 创建视图简化常用查询
-- ========================================

-- 用户配对信息视图
CREATE VIEW view_user_pairs AS
SELECT 
    u1.id as user_id,
    u1.username as username,
    u1.nickname as nickname,
    u1.avatar_url as avatar_url,
    u1.is_paired,
    u1.pair_date,
    u2.id as partner_id,
    u2.username as partner_username,
    u2.nickname as partner_nickname,
    u2.avatar_url as partner_avatar_url
FROM users u1
LEFT JOIN users u2 ON u1.partner_id = u2.id
WHERE u1.status = 1;

-- 订单详情视图
CREATE VIEW view_order_details AS
SELECT 
    o.id,
    o.order_no,
    o.title,
    o.description,
    o.type,
    o.status,
    o.total_amount,
    o.created_time,
    o.completed_time,
    creator.username as creator_username,
    creator.nickname as creator_nickname,
    receiver.username as receiver_username,
    receiver.nickname as receiver_nickname,
    COUNT(oi.id) as item_count,
    AVG(oe.rating) as avg_rating
FROM orders o
LEFT JOIN users creator ON o.creator_id = creator.id
LEFT JOIN users receiver ON o.receiver_id = receiver.id
LEFT JOIN order_items oi ON o.id = oi.order_id
LEFT JOIN order_evaluations oe ON o.id = oe.order_id
GROUP BY o.id;

-- 问卷统计视图
CREATE VIEW view_survey_stats AS
SELECT 
    s.id,
    s.title,
    s.description,
    s.type,
    s.status,
    s.created_time,
    creator.username as creator_username,
    creator.nickname as creator_nickname,
    COUNT(DISTINCT sq.id) as question_count,
    COUNT(DISTINCT sr.id) as response_count,
    s.max_responses,
    CASE 
        WHEN s.max_responses > 0 THEN (COUNT(DISTINCT sr.id) * 100.0 / s.max_responses)
        ELSE 0 
    END as completion_rate
FROM surveys s
LEFT JOIN users creator ON s.creator_id = creator.id
LEFT JOIN survey_questions sq ON s.id = sq.survey_id
LEFT JOIN survey_responses sr ON s.id = sr.survey_id
GROUP BY s.id;