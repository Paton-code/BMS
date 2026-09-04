
# 第一阶段：Maven 构建
FROM maven:3.8-eclipse-temurin-8 AS build
WORKDIR /app

# 安装 Git LFS 并拉取大文件
RUN apt-get update && apt-get install -y git-lfs && git lfs install

COPY .git .git
COPY .gitattributes .
RUN git lfs pull

COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]