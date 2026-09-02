# ===== 阶段一：Maven 构建可执行 jar =====
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先拷贝 pom 预拉依赖，利用 Docker 层缓存
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

COPY src ./src
RUN mvn -B -DskipTests clean package

# ===== 阶段二：精简 JRE 运行 =====
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Shanghai \
    JAVA_OPTS=""

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
