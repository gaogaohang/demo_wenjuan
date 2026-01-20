# 消息通知模块 API 对接文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **响应格式**: JSON

## 统一响应格式

所有接口统一返回以下格式：

```json
{
  "code": 200,
  "message": "成功",
  "data": {},
  "timestamp": 1700000000000
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 6001 | 消息不存在 |

---

## 1. 发送消息

### 接口地址
`POST /messages`

### 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
```

### 请求参数

```json
{
  "receiverId": 2,
  "type": "order",
  "title": "订单状态更新",
  "content": "您的订单已被接受",
  "data": "{\"orderId\":123}"
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| receiverId | Long | 是 | 接收者ID |
| type | String | 是 | 消息类型：system-系统，order-订单，survey-问卷，pair-配对 |
| title | String | 是 | 消息标题 |
| content | String | 否 | 消息内容 |
| data | String | 否 | 附加数据（JSON格式） |

### 响应示例

```json
{
  "code": 200,
  "message": "消息发送成功",
  "data": 1,
  "timestamp": 1700000000000
}
```

---

## 2. 获取我的消息列表

### 接口地址
`GET /messages`

### 请求头
```
Authorization: Bearer {token}
```

### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| isRead | Boolean | 否 | 是否已读（true-已读，false-未读） |
| type | String | 否 | 消息类型筛选 |

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "senderId": null,
      "senderUsername": null,
      "senderNickname": null,
      "senderAvatar": null,
      "receiverId": 2,
      "type": "system",
      "title": "系统通知",
      "content": "欢迎使用情侣互动平台",
      "data": null,
      "isRead": false,
      "readTime": null,
      "createdTime": "2024-01-20T10:00:00"
    },
    {
      "id": 2,
      "senderId": 1,
      "senderUsername": "xiaoming",
      "senderNickname": "小明",
      "senderAvatar": "http://example.com/avatar1.jpg",
      "receiverId": 2,
      "type": "order",
      "title": "订单状态更新",
      "content": "您的订单已被接受",
      "data": "{\"orderId\":123}",
      "isRead": true,
      "readTime": "2024-01-20T10:05:00",
      "createdTime": "2024-01-20T10:00:00"
    }
  ],
  "timestamp": 1700000000000
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| senderId | Long | 发送者ID（系统消息为null） |
| senderUsername | String | 发送者用户名 |
| senderNickname | String | 发送者昵称 |
| senderAvatar | String | 发送者头像URL |
| receiverId | Long | 接收者ID |
| type | String | 消息类型 |
| title | String | 消息标题 |
| content | String | 消息内容 |
| data | String | 附加数据（JSON格式） |
| isRead | Boolean | 是否已读 |
| readTime | DateTime | 阅读时间 |
| createdTime | DateTime | 创建时间 |

---

## 3. 获取消息详情

### 接口地址
`GET /messages/{id}`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 消息ID |

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "senderId": 1,
    "senderUsername": "xiaoming",
    "senderNickname": "小明",
    "senderAvatar": "http://example.com/avatar1.jpg",
    "receiverId": 2,
    "type": "order",
    "title": "订单状态更新",
    "content": "您的订单已被接受",
    "data": "{\"orderId\":123}",
    "isRead": false,
    "readTime": null,
    "createdTime": "2024-01-20T10:00:00"
  },
  "timestamp": 1700000000000
}
```

---

## 4. 标记消息为已读

### 接口地址
`POST /messages/{id}/read`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 消息ID |

### 响应示例

```json
{
  "code": 200,
  "message": "消息已标记为已读",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 5. 标记所有消息为已读

### 接口地址
`POST /messages/read-all`

### 请求头
```
Authorization: Bearer {token}
```

### 响应示例

```json
{
  "code": 200,
  "message": "所有消息已标记为已读",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 6. 删除消息

### 接口地址
`DELETE /messages/{id}`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 消息ID |

### 响应示例

```json
{
  "code": 200,
  "message": "消息删除成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 7. 获取未读消息数量

### 接口地址
`GET /messages/unread-count`

### 请求头
```
Authorization: Bearer {token}
```

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": 5,
  "timestamp": 1700000000000
}
```

---

## 业务规则说明

### 消息类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| system | 系统消息 | 平台通知、系统公告 |
| order | 订单消息 | 订单创建、状态变更、完成等 |
| survey | 问卷消息 | 问卷发布、回答等 |
| pair | 配对消息 | 配对成功、邀请等 |

### 权限控制

- 用户只能查看和操作自己接收的消息
- 用户不能给自己发送消息
- 只有消息的接收者可以标记已读和删除

### 消息状态

- 未读消息：`isRead` 为 `false`，`readTime` 为 `null`
- 已读消息：`isRead` 为 `true`，`readTime` 为阅读时间

---

## 前端对接建议

### 1. 消息列表展示

- 按时间倒序排列（最新消息在前）
- 未读消息显示红色角标或特殊样式
- 不同类型消息使用不同图标或颜色区分
- 显示发送者头像、昵称和消息内容摘要

### 2. 未读消息提醒

- 在首页或消息入口显示未读数量
- 使用红色数字角标显示
- 点击消息后自动标记为已读
- 实时轮询或WebSocket更新未读数量

### 3. 消息详情页

- 完整显示消息标题和内容
- 显示发送者信息和时间
- 提供"标记为已读"按钮
- 提供"删除"按钮

### 4. 消息类型处理

```javascript
// 根据消息类型显示不同图标
const getMessageIcon = (type) => {
  switch(type) {
    case 'system':
      return 'icon-system';
    case 'order':
      return 'icon-order';
    case 'survey':
      return 'icon-survey';
    case 'pair':
      return 'icon-pair';
    default:
      return 'icon-default';
  }
};
```

### 5. 消息轮询

建议在前端实现消息轮询，定期获取未读消息数量：

```javascript
// 每30秒轮询一次未读消息数量
setInterval(async () => {
  const response = await fetchUnreadCount();
  updateUnreadBadge(response.data);
}, 30000);
```

### 6. 订单和问卷自动通知

在订单状态变更和问卷发布时，后端会自动发送通知，前端需要：

1. 监听消息更新
2. 弹出通知提示
3. 引导用户查看对应订单或问卷

---

## 示例场景

### 场景1：订单状态变更通知

当订单状态从 `pending` 变更为 `accepted` 时：

```json
{
  "receiverId": 2,
  "type": "order",
  "title": "订单状态更新",
  "content": "您的订单 ORD20240120100001 已被接受",
  "data": "{\"orderId\":123}"
}
```

前端处理：点击消息跳转到订单详情页

### 场景2：问卷发布通知

当问卷发布时：

```json
{
  "receiverId": 2,
  "type": "survey",
  "title": "新问卷待填写",
  "content": "小明发送了问卷《情侣日常问答》，快来填写吧",
  "data": "{\"surveyId\":1}"
}
```

前端处理：点击消息跳转到问卷详情页

### 场景3：系统通知

```json
{
  "receiverId": 2,
  "type": "system",
  "title": "系统通知",
  "content": "欢迎来到情侣互动平台",
  "data": null
}
```

前端处理：显示系统通知内容

---

## Swagger 文档

访问 `http://localhost:8080/api/swagger-ui.html` 查看在线 API 文档
