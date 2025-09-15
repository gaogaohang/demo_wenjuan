FROM openjdk:17-jdk-alpine

LABEL maintainer="CoupleTeam <admin@couple-platform.com>"

# 安装时区数据
RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai

# 创建应用目录
WORKDIR /app

# 复制Maven包装器和依赖文件
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 赋予执行权限
RUN chmod +x mvnw

# 下载依赖（利用Docker层缓存）
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 构建应用
RUN ./mvnw package -DskipTests

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "target/couple-platform-1.0.0.jar"]