-- ========================================
-- 情侣互动平台数据库结构设计
-- ========================================

-- 删除已存在的数据库并重新创建
DROP DATABASE IF EXISTS couple_platform;
CREATE DATABASE couple_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE couple_platform;

-- ========================================
-- 用户相关表
-- ========================================

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    username VARCHAR(50) UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '生日',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    is_paired BOOLEAN DEFAULT FALSE COMMENT '是否已配对',
    partner_id BIGINT COMMENT '配对对象ID',
    pair_code VARCHAR(20) UNIQUE COMMENT '配对码',
    pair_date DATETIME COMMENT '配对时间',
    wechat_openid VARCHAR(100) COMMENT '微信OpenID',
    wechat_unionid VARCHAR(100) COMMENT '微信UnionID',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_phone (phone),
    INDEX idx_username (username),
    INDEX idx_pair_code (pair_code),
    INDEX idx_partner_id (partner_id),
    INDEX idx_wechat_openid (wechat_openid),
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '用户表';

-- 用户设置表
CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    theme_mode VARCHAR(20) DEFAULT 'light' COMMENT '主题模式：light-浅色，dark-深色',
    primary_color VARCHAR(10) DEFAULT '#007AFF' COMMENT '主色调',
    background_color VARCHAR(10) DEFAULT '#FFFFFF' COMMENT '背景色',
    background_image_url VARCHAR(255) COMMENT '背景图片URL',
    notification_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用通知',
    sound_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用声音',
    vibration_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用震动',
    language VARCHAR(10) DEFAULT 'zh_CN' COMMENT '语言设置',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '用户设置表';

-- ========================================
-- 管理员相关表
-- ========================================

-- 管理员表
CREATE TABLE admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '管理员用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色：super_admin-超级管理员，admin-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
) COMMENT '管理员表';

-- ========================================
-- 订单相关表
-- ========================================

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单号',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    receiver_id BIGINT COMMENT '接收者ID（配对对象）',
    title VARCHAR(100) NOT NULL COMMENT '订单标题',
    description TEXT COMMENT '订单描述',
    type VARCHAR(20) DEFAULT 'food' COMMENT '订单类型：food-餐饮，shopping-购物，other-其他',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态：pending-待处理，accepted-已接受，processing-处理中，completed-已完成，cancelled-已取消',
    total_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '订单总金额',
    note TEXT COMMENT '备注信息',
    images JSON COMMENT '图片列表',
    location VARCHAR(255) COMMENT '地址位置',
    estimated_time DATETIME COMMENT '预计完成时间',
    accepted_time DATETIME COMMENT '接受时间',
    completed_time DATETIME COMMENT '完成时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_order_no (order_no),
    INDEX idx_creator_id (creator_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '订单表';

-- 订单项表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(8,2) DEFAULT 0.00 COMMENT '单价',
    total_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '小计',
    image_url VARCHAR(255) COMMENT '商品图片',
    note TEXT COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_order_id (order_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) COMMENT '订单项表';

-- 订单评价表
CREATE TABLE order_evaluations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    evaluator_id BIGINT NOT NULL COMMENT '评价者ID',
    rating TINYINT NOT NULL COMMENT '评分：1-5分',
    comment TEXT COMMENT '评价内容',
    images JSON COMMENT '评价图片',
    emojis JSON COMMENT '表情列表',
    tags JSON COMMENT '标签列表',
    is_anonymous BOOLEAN DEFAULT FALSE COMMENT '是否匿名评价',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_order_evaluator (order_id, evaluator_id),
    INDEX idx_order_id (order_id),
    INDEX idx_evaluator_id (evaluator_id),
    INDEX idx_rating (rating),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '订单评价表';

-- ========================================
-- 问卷相关表
-- ========================================

-- 问卷表
CREATE TABLE surveys (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '问卷标题',
    description TEXT COMMENT '问卷描述',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    target_id BIGINT COMMENT '目标用户ID（配对对象）',
    type VARCHAR(20) DEFAULT 'custom' COMMENT '问卷类型：custom-自定义，template-模板',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布，closed-已关闭',
    is_anonymous BOOLEAN DEFAULT FALSE COMMENT '是否匿名',
    allow_multiple BOOLEAN DEFAULT FALSE COMMENT '是否允许多次提交',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    max_responses INT COMMENT '最大回复数',
    current_responses INT DEFAULT 0 COMMENT '当前回复数',
    settings JSON COMMENT '问卷设置',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_creator_id (creator_id),
    INDEX idx_target_id (target_id),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '问卷表';

-- 问卷问题表
CREATE TABLE survey_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,
    question_text TEXT NOT NULL COMMENT '问题内容',
    question_type VARCHAR(20) NOT NULL COMMENT '问题类型：single_choice-单选，multiple_choice-多选，text-填空，rating-评分，date-日期',
    is_required BOOLEAN DEFAULT FALSE COMMENT '是否必答',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    options JSON COMMENT '选项列表（适用于选择题）',
    validation_rules JSON COMMENT '验证规则',
    description TEXT COMMENT '问题描述或提示',
    image_url VARCHAR(255) COMMENT '问题配图',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_survey_id (survey_id),
    INDEX idx_sort_order (sort_order),
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
) COMMENT '问卷问题表';

-- 问卷回答表
CREATE TABLE survey_responses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,
    respondent_id BIGINT COMMENT '回答者ID',
    response_data JSON NOT NULL COMMENT '回答数据',
    completion_time INT COMMENT '完成耗时（秒）',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    is_completed BOOLEAN DEFAULT TRUE COMMENT '是否完成',
    submitted_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_survey_id (survey_id),
    INDEX idx_respondent_id (respondent_id),
    INDEX idx_submitted_time (submitted_time),
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE,
    FOREIGN KEY (respondent_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '问卷回答表';

-- ========================================
-- 消息通知相关表
-- ========================================

-- 消息表
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT COMMENT '发送者ID（系统消息为NULL）',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    type VARCHAR(20) NOT NULL COMMENT '消息类型：system-系统，order-订单，survey-问卷，pair-配对',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    data JSON COMMENT '附加数据',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '消息表';

-- 推送订阅表
CREATE TABLE push_subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    endpoint VARCHAR(500) NOT NULL COMMENT '推送端点',
    p256dh_key VARCHAR(200) NOT NULL COMMENT 'P256DH密钥',
    auth_key VARCHAR(50) NOT NULL COMMENT '认证密钥',
    user_agent TEXT COMMENT '用户代理',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_endpoint (user_id, endpoint(191)),
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '推送订阅表';

-- ========================================
-- 文件存储相关表
-- ========================================

-- 文件表
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_type VARCHAR(50) NOT NULL COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    bucket_name VARCHAR(100) COMMENT '存储桶名称',
    uploader_id BIGINT COMMENT '上传者ID',
    usage_type VARCHAR(50) COMMENT '用途类型：avatar-头像，order-订单，survey-问卷，emoji-表情',
    access_url VARCHAR(500) COMMENT '访问URL',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_usage_type (usage_type),
    INDEX idx_file_type (file_type),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '文件表';

-- ========================================
-- 系统相关表
-- ========================================

-- 系统设置表
CREATE TABLE system_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) UNIQUE NOT NULL COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    setting_type VARCHAR(20) DEFAULT 'string' COMMENT '设置类型：string-字符串，number-数字，boolean-布尔，json-JSON',
    description TEXT COMMENT '设置描述',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    category VARCHAR(50) COMMENT '设置分类',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_is_public (is_public)
) COMMENT '系统设置表';

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator_id BIGINT COMMENT '操作者ID',
    operator_type VARCHAR(20) NOT NULL COMMENT '操作者类型：user-用户，admin-管理员',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    description TEXT COMMENT '操作描述',
    request_data JSON COMMENT '请求数据',
    response_data JSON COMMENT '响应数据',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    execution_time INT COMMENT '执行时间（毫秒）',
    status VARCHAR(20) DEFAULT 'success' COMMENT '状态：success-成功，error-失败',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_operator_id (operator_id),
    INDEX idx_operator_type (operator_type),
    INDEX idx_operation (operation),
    INDEX idx_resource_type (resource_type),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time)
) COMMENT '操作日志表';

-- ========================================
-- 初始化系统设置数据
-- ========================================

INSERT INTO system_settings (setting_key, setting_value, setting_type, description, is_public, category) VALUES
('site.name', '情侣互动平台', 'string', '网站名称', true, 'site'),
('site.description', '为情侣提供互动体验的在线平台', 'string', '网站描述', true, 'site'),
('site.keywords', '情侣,互动,点单,问卷', 'string', '网站关键词', true, 'site'),
('site.logo', '', 'string', '网站Logo', true, 'site'),
('site.favicon', '', 'string', '网站图标', true, 'site'),

('user.max_pair_attempts', '5', 'number', '最大配对尝试次数', false, 'user'),
('user.pair_code_length', '8', 'number', '配对码长度', false, 'user'),
('user.avatar_max_size', '5242880', 'number', '头像最大大小（字节）', false, 'user'),

('order.auto_cancel_hours', '24', 'number', '订单自动取消时间（小时）', false, 'order'),
('order.max_items', '50', 'number', '订单最大商品数', false, 'order'),
('order.image_max_count', '9', 'number', '订单最大图片数', false, 'order'),

('survey.max_questions', '100', 'number', '问卷最大问题数', false, 'survey'),
('survey.max_options', '20', 'number', '选择题最大选项数', false, 'survey'),
('survey.max_responses', '1000', 'number', '问卷最大回复数', false, 'survey'),

('file.max_size', '10485760', 'number', '文件最大大小（字节）', false, 'file'),
('file.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx', 'string', '允许的文件类型', false, 'file'),
('file.image_max_width', '1920', 'number', '图片最大宽度', false, 'file'),
('file.image_max_height', '1920', 'number', '图片最大高度', false, 'file'),

('notification.enabled', 'true', 'boolean', '是否启用通知', false, 'notification'),
('notification.email_enabled', 'false', 'boolean', '是否启用邮件通知', false, 'notification'),
('notification.sms_enabled', 'false', 'boolean', '是否启用短信通知', false, 'notification'),
('notification.push_enabled', 'true', 'boolean', '是否启用推送通知', false, 'notification');