# Lynn's Blog 部署指南

一套「个人博客」的完整交付：**Vue3 前端 + Spring Boot 3 后端 + MySQL 8**，已提供 Docker Compose 一键编排（`docker-compose.yml`）。

```
访客 ──> Nginx(frontend 容器, :80) ──┬── 静态资源 /  SPA 路由
                                    ├── /api/*      ──> Spring Boot(backend 容器, :8080)
                                    └── /uploads/*  ──> backend 磁盘卷(默认) / OSS(可选)
backend ──> MySQL(mysql 容器, :3306)  ← 首次启动自动执行 init.sql 建表
```

---

## 🚀 方式一：Docker Compose（推荐）

### 1. 准备环境

- 一台 Linux 服务器（或本机安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/) 先演练）
- 已装 Docker Engine 20.10+ 与 Compose v2：`docker -v && docker compose version`

### 2. 获取代码并配置

```bash
# 服务器上拉代码（或从本机把整个 myboke 目录上传到服务器）
git clone https://github.com/lin-118776/lynnBlog.git
cd lynnBlog

# 生成配置：改数据库密码 & JWT 密钥（重要！）
cp .env.example .env
#   vi .env  → MYSQL_ROOT_PASSWORD=改成强密码 / JWT_SECRET=改成长随机串
```

> 可选：配置阿里云 OSS 图片托管（`.env` 里填 OSS_* 并 `OSS_ENABLED=true`）；不填则图片存本地卷 `uploads-data`。

### 3. 一键启动

```bash
docker compose up -d --build
```

- 首次构建需拉镜像/下载依赖，约 3~10 分钟
- MySQL 首次启动会自动执行 `backend/sql/init.sql`（建 14 张表 + 默认分类/联系方式）

### 4. 访问与验证

```bash
# 浏览器打开
#   http://<服务器IP>:8080
curl -s http://localhost:8080/api/article/list | head -c 200   # 应返回 code:200
docker compose ps                                              # 三个容器均应为 running
```

首次使用：右上角「登录 → 去注册」创建你自己的账号 → 控制台写第一篇文章。

### 日常运维速查

```bash
docker compose ps                                  # 状态
docker compose logs -f backend                    # 看后端日志
docker compose up -d --build                      # 更新代码后重建
docker compose down                               # 停止（保留数据卷）
docker compose down -v                            # 停止并删除数据卷（⚠️ 数据全没）

# 备份数据库（数据在 mysql-data 卷里，dump 到服务器本地）
docker compose exec mysql sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" personalcenter' > backup.sql
```

---

## 🖥 方式二：传统手动部署（不用 Docker）

1. **MySQL**：服务器装 MySQL 8，执行 `backend/sql/init.sql`
2. **后端**：
   ```bash
   cd backend && mvn -DskipTests clean package
   # 生产配置用环境变量覆盖数据库与密钥：
   DB_HOST=127.0.0.1 DB_USERNAME=root DB_PASSWORD=xxx \
   JWT_SECRET=xxx nohup java -jar target/personalcenter-1.0.0.jar --server.port=8080 &
   ```
3. **前端**：`cd frontend && npm run build`，把 `dist/` 用 Nginx 托管，并按 `frontend/nginx.conf` 配置 `/api`、`/uploads` 反代（把 `proxy_pass http://backend:8080` 改成你的后端地址）

---

## 🌐 上线公网提醒

- **国内云服务器**：`80/443` 端口 + 域名需 ICP 备案。**不备案也能用**：直接访问 `http://IP:8080`（非标端口无需备案）
- 想用 `lynnblog.com` 这种域名：买域名 → 备案（约 2~3 周）→ 域名解析到服务器 IP → 用 Nginx/Caddy 反代 + HTTPS（免费证书 Let's Encrypt / 各家云有免费证书）
- **务必修改默认密码与 JWT 密钥**：项目内置弱默认仅用于本地开发
- 后台上传/留言等写接口都带登录鉴权与作者校验，公开接口为只读——可放心对外开放

---

## ❓ 常见问题

| 现象 | 处理 |
| --- | --- |
| 拉镜像很慢 | 服务器配置国内镜像加速（腾讯云/阿里云控制台有引导），或用境外 VPS |
| 后端反复重启 | `docker compose logs backend` 看报错：最常见是 MySQL 还没健康（等 20s 自动恢复）或 .env 密码不一致 |
| 8080 被占用 | 改 `.env` 的 `HTTP_PORT=18080` 再 `docker compose up -d` |
| 作品页 GitHub 图空白 | 服务器能直连 GitHub 则无需处理；若处于加速/代理 MITM 环境，给 backend 加环境变量 `GITHUB_INSECURE_TLS=true`（生产直连环境请保持 false） |
| 图片上传 413 | 上传文件超过 20MB；前端 Nginx 与后端限制均已设为 20MB |
| MySQL 卷初始化只执行一次 | 以后要改表结构需手动执行对应 SQL，或 `docker compose down -v` 重建（会清空数据，慎用） |

---

## 🤔 还没服务器？先这样

1. **本机装 Docker Desktop**（Windows）→ 按方式一在本机跑通整站（访问 `http://localhost:8080`），先玩起来
2. 确定要上线再买：**阿里云/腾讯云「轻量应用服务器」2C2G/2C4G** 就够（新用户常年几十~一百出头/年，选 Ubuntu 22.04 镜像），拿到 IP 后把上面步骤照做即可
3. 想在简历里放链接：买域名 + 备案或用境外免备案 VPS + HTTPS，效果最好
