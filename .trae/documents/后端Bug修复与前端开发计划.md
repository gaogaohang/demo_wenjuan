## 后端Bug修复计划

### 1. 修复OrderService类型转换错误
- 问题：`buildEvaluationResponse` 返回类型不匹配
- 解决方案：修改返回类型为 `OrderResponse.OrderEvaluationResponse`

### 2. 修复SurveyController和MessageController的类型转换错误  
- 问题：`ApiResponse.success(String message)` 方法不存在
- 解决方案：改为 `ApiResponse.success("消息", null)` 或创建新的重载方法

### 3. 修复SurveyService中缺少的User实体导入
- 问题：`User` 类未导入
- 解决方案：添加 `import com.couple.platform.entity.User;`

### 4. 验证修复
- 重新编译项目
- 确保所有错误已解决

## 前端开发计划（腾讯风格）

### 5. 创建前端项目结构
- 使用 Vue 3 + TypeScript + Vant UI
- 搭建基础项目框架

### 6. 实现用户认证模块
- 登录/注册页面（手机号+验证码）
- JWT Token管理
- 路由守卫

### 7. 实现用户配对模块
- 配对码生成与输入
- 配对成功状态展示
- 个人信息展示

### 8. 实现在线点单模块
- 订单创建页面
- 订单列表与状态管理
- 订单详情与评价
- 实时消息通知

### 9. 实现问卷模块
- 问卷创建（支持多种题型）
- 问卷填写页面
- 问卷结果展示
- 数据导出功能

### 10. 实现消息通知模块
- 消息列表与详情
- 未读消息提醒
- 消息类型图标

### 11. 配置数据库连接
- 修改application.yml中的数据库地址
- 添加您提供的数据库服务器信息

## 技术栈
- **后端**: Spring Boot 3.x + MyBatis Plus
- **前端**: Vue 3 + TypeScript + Vant UI（腾讯风格的移动端UI库）
- **状态管理**: Pinia
- **HTTP客户端**: Axios