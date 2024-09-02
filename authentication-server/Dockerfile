# Базовый образ Java
FROM openjdk:17-jdk-slim

# Установка необходимых пакетов, включая netcat
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Копирование скрипта wait-for-it.sh в контейнер
COPY wait-for-it.sh /wait-for-it.sh

# Копирование jar файла приложения в контейнер
COPY target/authentication-server-0.0.1-SNAPSHOT.jar /app/authentication-server-0.0.1-SNAPSHOT.jar

# Добавление прав на выполнение скрипта
RUN chmod +x /wait-for-it.sh

# Команда по умолчанию, ожидающая зависимость
CMD ["/wait-for-it.sh", "config-server", "8888", "--", "java", "-jar", "/app/authentication-server-0.0.1-SNAPSHOT.jar"]
