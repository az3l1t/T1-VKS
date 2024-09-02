# Базовый образ Java
FROM openjdk:17-jdk-slim

# Установка необходимых пакетов, включая netcat
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Копирование скрипта wait-for-it.sh в контейнер
COPY wait-for-it.sh /wait-for-it.sh

# Копирование jar файла приложения в контейнер
COPY target/slots-server-0.0.1-SNAPSHOT.jar /app/slots-server-0.0.1-SNAPSHOT.jar

COPY src/main/resources/app/credentials.json /app/credentials.json

COPY tokens /tokens

# Добавление прав на выполнение скрипта
RUN chmod +x /wait-for-it.sh

# Команда по умолчанию, ожидающая зависимость
CMD ["/wait-for-it.sh", "postgres", "5432", "--", "java", "-jar", "/app/slots-server-0.0.1-SNAPSHOT.jar"]
