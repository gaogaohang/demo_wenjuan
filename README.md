# 互动在线点单与问卷平台

## 项目概述

互动在线点单与问卷平台是一个为情侣提供互动体验的web应用。平台支持两种核心功能：
1. **在线点单功能**：一方创建类似堂食的订单，另一方接收推送并完成订单，支持状态同步和评价
2. **问卷调查功能**：支持发布问卷，情侣另一方填写并生成数据表格

## 技术栈

### 后端
- Java 17 + Spring Boot 3.x
- MySQL 8.0
- Spring Security
- WebSocket
- MinIO 对象存储
- PWA 推送通知

### 前端
- Vue 3 + TypeScript
- Vant UI (移动端)
- Element Plus (管理后台)
- Pinia 状态管理
- Axios HTTP客户端

## 项目结构

```
demo_wenjuan/
├── backend/                    # 后端 Spring Boot 应用
│   ├── src/main/java/
│   │   └── com/couple/platform/
│   │       ├── CoupleApplication.java
│   │       ├── config/         # 配置类
│   │       ├── controller/     # REST 控制器
│   │       ├── service/        # 业务服务层
│   │       ├── repository/     # 数据访问层
│   │       ├── entity/         # 实体类
│   │       ├── dto/            # 数据传输对象
│   │       ├── security/       # 安全配置
│   │       ├── websocket/      # WebSocket处理
│   │       └── utils/          # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml     # 应用配置
│   │   ├── static/             # 静态资源
│   │   └── templates/          # 模板文件
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # 前端 Vue 3 移动端应用
│   ├── src/
│   │   ├── components/         # 组件
│   │   ├── views/              # 页面
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── router/             # 路由配置
│   │   ├── api/                # API 接口
│   │   ├── utils/              # 工具函数
│   │   ├── assets/             # 静态资源
│   │   └── styles/             # 样式文件
│   ├── public/
│   │   ├── manifest.json       # PWA 配置
│   │   ├── sw.js               # Service Worker
│   │   └── icons/              # 图标资源
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
├── admin-frontend/             # 管理后台 Vue 3 应用
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── stores/
│   │   ├── router/
│   │   ├── api/
│   │   └── utils/
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
├── database/                   # 数据库相关
│   ├── migrations/             # 数据库迁移脚本
│   ├── schema.sql              # 数据库结构
│   └── init-data.sql           # 初始化数据
├── docs/                       # 文档
│   ├── api/                    # API 文档
│   ├── design/                 # 设计文档
│   └── deployment/             # 部署文档
├── docker-compose.yml          # Docker 编排
└── README.md
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose

### 本地开发

1. 克隆项目
```bash
git clone <repository-url>
cd demo_wenjuan
```

2. 启动后端服务
```bash
cd backend
./mvnw spring-boot:run
```

3. 启动前端应用
```bash
cd frontend
npm install
npm run dev
```

4. 启动管理后台
```bash
cd admin-frontend
npm install
npm run dev
```

### 部署

使用 Docker Compose 一键部署：
```bash
docker-compose up -d
```

## 核心功能

### 用户功能
- 手机号注册登录 + 微信一键登录
- 账号配对
- 个人资料管理
- UI主题自定义

### 订单功能
- 订单创建与管理
- 实时订单状态同步
- 订单评价（支持表情和图片）
- 推送通知

### 问卷功能
- 多类型问卷创建（单选、多选、填空）
- 问卷填写与提交
- 数据统计与分析
- 结果导出

### 管理功能
- 用户管理
- 订单管理
- 问卷管理
- 系统统计
- 文件资源管理

## 开发指南

### API 文档
API 文档位于 `/docs/api/` 目录，使用 Swagger UI 在开发环境中可访问 `http://localhost:8080/swagger-ui.html`

### 开发规范
- 代码风格遵循团队约定
- 提交信息格式：`type(scope): description`
- 所有新功能需要编写单元测试

## 许可证
MIT License
