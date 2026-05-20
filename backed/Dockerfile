# 构建阶段
FROM gradle:8.5-jdk19 AS builder

WORKDIR /app

# 复制 Gradle 配置
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# 赋予执行权限
RUN chmod +x gradlew

# 下载依赖（这一步会被缓存）
RUN ./gradlew dependencies --no-daemon

# 复制源代码
COPY src ./src

# 构建应用
RUN ./gradlew bootJar --no-daemon

# 运行阶段
FROM openjdk:19-jdk-slim

WORKDIR /app

# 从构建阶段复制 jar 文件
COPY --from=builder /app/build/libs/*.jar app.jar

# 暴露端口（Render 推荐使用 10000）
EXPOSE 10000

# 设置 JVM 参数并运行
ENTRYPOINT ["java", "-jar", "app.jar"]
