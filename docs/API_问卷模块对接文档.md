# 问卷模块 API 对接文档

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
| 4001 | 问卷不存在 |
| 4002 | 问卷已发布 |
| 4003 | 问卷已关闭 |
| 4004 | 问卷问题不能为空 |
| 4005 | 问卷回复不能为空 |
| 4006 | 问卷已回复 |
| 4007 | 问题类型无效 |

---

## 1. 创建问卷

### 接口地址
`POST /surveys`

### 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
```

### 请求参数

```json
{
  "title": "情侣日常问答",
  "description": "了解彼此的日常喜好",
  "targetId": 2,
  "type": "custom",
  "isAnonymous": false,
  "allowMultiple": false,
  "startTime": "2024-01-20T10:00:00",
  "endTime": "2024-01-25T10:00:00",
  "maxResponses": 10,
  "settings": "{}",
  "questions": [
    {
      "questionText": "你最喜欢的颜色是什么？",
      "questionType": "single_choice",
      "isRequired": true,
      "sortOrder": 1,
      "options": "[{\"label\":\"红色\",\"value\":\"red\"},{\"label\":\"蓝色\",\"value\":\"blue\"},{\"label\":\"绿色\",\"value\":\"green\"}]",
      "validationRules": "{}",
      "description": "单选题",
      "imageUrl": ""
    },
    {
      "questionText": "你喜欢的水果有哪些？",
      "questionType": "multiple_choice",
      "isRequired": true,
      "sortOrder": 2,
      "options": "[{\"label\":\"苹果\",\"value\":\"apple\"},{\"label\":\"香蕉\",\"value\":\"banana\"},{\"label\":\"橙子\",\"value\":\"orange\"}]",
      "validationRules": "{\"min\":1,\"max\":3}",
      "description": "多选题，最多选择3项",
      "imageUrl": ""
    },
    {
      "questionText": "请描述一下你最难忘的一次约会",
      "questionType": "text",
      "isRequired": true,
      "sortOrder": 3,
      "options": "",
      "validationRules": "{\"minLength\":10,\"maxLength\":500}",
      "description": "填空题，10-500字",
      "imageUrl": ""
    },
    {
      "questionText": "你对我们的关系满意度打分",
      "questionType": "rating",
      "isRequired": true,
      "sortOrder": 4,
      "options": "{\"min\":1,\"max\":5,\"step\":1}",
      "validationRules": "{}",
      "description": "评分题，1-5分",
      "imageUrl": ""
    }
  ]
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 问卷标题 |
| description | String | 否 | 问卷描述 |
| targetId | Long | 是 | 目标用户ID（配对对象） |
| type | String | 否 | 问卷类型：custom-自定义，template-模板 |
| isAnonymous | Boolean | 否 | 是否匿名 |
| allowMultiple | Boolean | 否 | 是否允许多次提交 |
| startTime | DateTime | 否 | 开始时间 |
| endTime | DateTime | 否 | 结束时间 |
| maxResponses | Integer | 否 | 最大回复数 |
| settings | String | 否 | 问卷设置（JSON字符串） |
| questions | Array | 是 | 问题列表 |

#### 问题类型 (questionType)

| 类型 | 说明 |
|------|------|
| single_choice | 单选题 |
| multiple_choice | 多选题 |
| text | 填空题 |
| rating | 评分题 |
| date | 日期题 |

#### options 字段格式

```json
// 单选题/多选题
[
  {"label": "选项1", "value": "option1"},
  {"label": "选项2", "value": "option2"}
]

// 评分题
{"min": 1, "max": 5, "step": 1}
```

### 响应示例

```json
{
  "code": 200,
  "message": "问卷创建成功",
  "data": 1,
  "timestamp": 1700000000000
}
```

---

## 2. 更新问卷

### 接口地址
`PUT /surveys/{id}`

### 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 请求参数

```json
{
  "title": "情侣日常问答（更新版）",
  "description": "了解彼此的日常喜好",
  "targetId": 2,
  "type": "custom",
  "isAnonymous": false,
  "allowMultiple": false,
  "startTime": "2024-01-20T10:00:00",
  "endTime": "2024-01-25T10:00:00",
  "maxResponses": 10,
  "settings": "{}",
  "questions": []
}
```

### 响应示例

```json
{
  "code": 200,
  "message": "问卷更新成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 3. 发布问卷

### 接口地址
`POST /surveys/{id}/publish`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 响应示例

```json
{
  "code": 200,
  "message": "问卷发布成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 4. 关闭问卷

### 接口地址
`POST /surveys/{id}/close`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 响应示例

```json
{
  "code": 200,
  "message": "问卷关闭成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 5. 删除问卷

### 接口地址
`DELETE /surveys/{id}`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 响应示例

```json
{
  "code": 200,
  "message": "问卷删除成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 6. 获取问卷详情

### 接口地址
`GET /surveys/{id}`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "title": "情侣日常问答",
    "description": "了解彼此的日常喜好",
    "creatorId": 1,
    "creatorName": "小明",
    "creatorAvatar": "http://example.com/avatar1.jpg",
    "targetId": 2,
    "targetName": "小红",
    "targetAvatar": "http://example.com/avatar2.jpg",
    "type": "custom",
    "status": "published",
    "isAnonymous": false,
    "allowMultiple": false,
    "startTime": "2024-01-20T10:00:00",
    "endTime": "2024-01-25T10:00:00",
    "maxResponses": 10,
    "currentResponses": 3,
    "settings": "{}",
    "questions": [
      {
        "id": 1,
        "surveyId": 1,
        "questionText": "你最喜欢的颜色是什么？",
        "questionType": "single_choice",
        "isRequired": true,
        "sortOrder": 1,
        "options": "[{\"label\":\"红色\",\"value\":\"red\"}]",
        "validationRules": "{}",
        "description": "单选题",
        "imageUrl": "",
        "createdTime": "2024-01-20T10:00:00",
        "updatedTime": "2024-01-20T10:00:00"
      }
    ],
    "createdTime": "2024-01-20T10:00:00",
    "updatedTime": "2024-01-20T10:00:00"
  },
  "timestamp": 1700000000000
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| status | String | 问卷状态：draft-草稿，published-已发布，closed-已关闭 |
| currentResponses | Integer | 当前回复数 |

---

## 7. 获取我创建的问卷列表

### 接口地址
`GET /surveys/created`

### 请求头
```
Authorization: Bearer {token}
```

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "title": "情侣日常问答",
      "description": "了解彼此的日常喜好",
      "creatorId": 1,
      "creatorName": "小明",
      "creatorAvatar": "http://example.com/avatar1.jpg",
      "targetId": 2,
      "targetName": "小红",
      "targetAvatar": "http://example.com/avatar2.jpg",
      "type": "custom",
      "status": "published",
      "isAnonymous": false,
      "allowMultiple": false,
      "startTime": "2024-01-20T10:00:00",
      "endTime": "2024-01-25T10:00:00",
      "maxResponses": 10,
      "currentResponses": 3,
      "settings": "{}",
      "questions": [],
      "createdTime": "2024-01-20T10:00:00",
      "updatedTime": "2024-01-20T10:00:00"
    }
  ],
  "timestamp": 1700000000000
}
```

---

## 8. 获取我的目标问卷列表

### 接口地址
`GET /surveys/target`

### 请求头
```
Authorization: Bearer {token}
```

### 说明
此接口只返回状态为 `published` 的问卷

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "title": "情侣日常问答",
      "description": "了解彼此的日常喜好",
      "creatorId": 1,
      "creatorName": "小明",
      "creatorAvatar": "http://example.com/avatar1.jpg",
      "targetId": 2,
      "targetName": "小红",
      "targetAvatar": "http://example.com/avatar2.jpg",
      "type": "custom",
      "status": "published",
      "isAnonymous": false,
      "allowMultiple": false,
      "startTime": "2024-01-20T10:00:00",
      "endTime": "2024-01-25T10:00:00",
      "maxResponses": 10,
      "currentResponses": 3,
      "settings": "{}",
      "questions": [],
      "createdTime": "2024-01-20T10:00:00",
      "updatedTime": "2024-01-20T10:00:00"
    }
  ],
  "timestamp": 1700000000000
}
```

---

## 9. 提交问卷回答

### 接口地址
`POST /surveys/{id}/responses`

### 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 请求参数

```json
{
  "surveyId": 1,
  "responseData": "{\"1\":\"red\",\"2\":[\"apple\",\"banana\"],\"3\":\"那是一个晴朗的下午，我们一起去公园野餐\",\"4\":5}",
  "completionTime": 300
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| surveyId | Long | 是 | 问卷ID（与路径参数保持一致） |
| responseData | String | 是 | 回答数据（JSON字符串，key为问题ID，value为答案） |
| completionTime | Integer | 否 | 完成耗时（秒） |

### responseData 格式说明

```json
{
  "问题ID": "答案值"
}
```

- 单选题/填空题/日期题：值为字符串
- 多选题：值为数组
- 评分题：值为数字

### 响应示例

```json
{
  "code": 200,
  "message": "问卷回答提交成功",
  "data": null,
  "timestamp": 1700000000000
}
```

---

## 10. 获取问卷回复列表

### 接口地址
`GET /surveys/{id}/responses`

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 问卷ID |

### 说明
只有问卷的创建者可以查看回复列表

### 响应示例

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "surveyId": 1,
      "respondentId": 2,
      "responseData": "{\"1\":\"red\",\"2\":[\"apple\",\"banana\"]}",
      "completionTime": 300,
      "ipAddress": "127.0.0.1",
      "userAgent": "Mozilla/5.0...",
      "isCompleted": true,
      "submittedTime": "2024-01-20T10:00:00"
    }
  ],
  "timestamp": 1700000000000
}
```

---

## 业务规则说明

### 问卷状态流转

```
draft（草稿） → published（已发布） → closed（已关闭）
```

- 只有 `draft` 状态的问卷可以编辑和删除
- 只有包含问题的问卷才能发布
- 只有已发布的问卷可以关闭
- 只有 `published` 状态的问卷可以回答

### 权限控制

- 只有创建者可以修改、删除、发布、关闭问卷
- 只有目标用户可以回答问卷
- 只有创建者可以查看回复列表

### 回答限制

- 如果 `allowMultiple` 为 false，每个用户只能回答一次
- 如果设置了 `maxResponses`，回复数达到上限后无法继续回答
- 如果设置了 `endTime`，过期后无法继续回答

---

## 前端对接建议

### 1. 创建问卷流程

1. 用户选择目标用户（从配对对象中选择）
2. 填写问卷基本信息（标题、描述等）
3. 添加问题（支持多种问题类型）
4. 设置时间和限制（可选）
5. 保存为草稿或直接发布

### 2. 回答问卷流程

1. 用户从"我的目标问卷"列表中选择要回答的问卷
2. 逐题填写答案
3. 提交回答

### 3. 数据展示建议

- 问卷列表卡片展示：标题、创建者、状态、回复数
- 问卷详情页：完整展示所有问题和当前状态
- 回答统计：可以展示每个问题的答案分布（单选/多选题）

### 4. 错误处理

- 问卷已发布时提示用户无法编辑
- 问卷已关闭时提示用户无法回答
- 回答次数限制时给出友好提示

---

## Swagger 文档

访问 `http://localhost:8080/api/swagger-ui.html` 查看在线 API 文档
