# 个人数字中心

前后端分离的个人全栈数字中心系统，包含技术文章、面试复盘、私密日记、文件相册和 Lolita 衣橱管理。

## 技术栈

- 前端：Vue 3、Vite 5、Vue Router、Element Plus、Axios、md-editor-v3
- 后端：Spring Boot 3.3.4、MyBatis-Plus 3.5.7、MySQL 8、JWT、Lombok

## 目录结构

```
myboke/
├─ frontend/   # Vue 前端
├─ backend/    # Spring Boot 后端
│  ├─ src/main/java/com/example/personalcenter/
│  └─ src/main/resources/application.yml
└─ backend/sql/init.sql
```

## 环境要求

- JDK 17
- Maven 3.8+
- Node.js 18+
- MySQL 8

## 快速启动

1. 创建数据库并导入 `backend/sql/init.sql`
2. 启动后端
   ```
   cd backend
   mvn spring-boot:run
   ```
3. 启动前端
   ```
   cd frontend
   npm install
   npm run dev
   ```
4. 浏览器访问 `http://localhost:5173`

后端默认端口 `8080`，前端开发服务器默认端口 `5173`，并通过 Vite 代理 `/api` 和 `/uploads`。## 后端接口

| 模块 | 前缀 | 主要接口 |
| --- | --- | --- |
| 认证 | `/api/auth` | `POST /register`、`POST /login` |
| 文章 | `/api/article` | `GET /list`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}` |
| 日记 | `/api/diary` | `GET /list`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}` |
| 文件 | `/api/file` | `POST /upload`、`GET /list`、`DELETE /{id}` |
| 面试 | `/api/interview` | `GET /list`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}` |
| Lolita | `/api/lolita` | `GET /list`、`GET /{id}`、`POST`、`PUT /{id}`、`DELETE /{id}`、`PUT /wear/{id}` |

## 配置说明

后端配置在 `backend/src/main/resources/application.yml`，包括数据库连接、JWT、上传目录和文件大小限制。部署前应通过环境变量替换数据库密码和 JWT 密钥。

## 构建

```
cd frontend && npm run build
cd backend && mvn clean package
```

前端构建产物位于 `frontend/dist`，后端构建产物位于 `backend/target`。