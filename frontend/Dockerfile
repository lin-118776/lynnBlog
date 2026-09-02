# ===== 阶段一：构建前端静态资源 =====
FROM node:20-alpine AS builder
WORKDIR /build

COPY package*.json ./
RUN npm install --registry=https://registry.npmmirror.com

COPY . .
RUN npm run build

# ===== 阶段二：Nginx 托管 =====
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /build/dist /usr/share/nginx/html

EXPOSE 80
