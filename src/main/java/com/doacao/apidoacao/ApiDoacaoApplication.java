package com.doacao.apidoacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiDoacaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiDoacaoApplication.class, args);
    }
}