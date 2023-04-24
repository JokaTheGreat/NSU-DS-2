package ru.nlatysh.worker;
import org.springframework.boot.SpringApplication;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        var app = new SpringApplication(Worker.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        app.run(args);
    }
}