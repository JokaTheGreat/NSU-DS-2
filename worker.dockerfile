FROM gradle:jdk17

EXPOSE 8080

COPY . .

RUN ./gradlew NSU-DS-2-worker:build

CMD ["./gradlew", "NSU-DS-2-worker:bootRun"]
