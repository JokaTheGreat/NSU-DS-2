FROM gradle:jdk17

EXPOSE 8080

COPY . .

RUN ./gradlew NSU-DS-2-manager:build

CMD ["./gradlew", "NSU-DS-2-manager:bootRun"]
