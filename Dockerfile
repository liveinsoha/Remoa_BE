FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /usr/src/app

# Spring Boot JAR 파일을 이미지에 복사
COPY ./build/libs/*.jar app.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=deploy

# Spring Boot 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
