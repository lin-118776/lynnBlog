# ===== 阶段一：Maven 构建可执行 jar =====
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先拷贝 pom 预拉依赖，利用 Docker 层缓存（走阿里云镜像，容器内用不了宿主机代理）
COPY pom.xml docker-settings.xml ./
RUN mvn -B -q -s docker-settings.xml dependency:go-offline || true

COPY src ./src
RUN mvn -B -s docker-settings.xml -DskipTests clean package

# ===== 阶段二：精简 JRE 运行 =====
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Shanghai \
    JAVA_OPTS=""

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
