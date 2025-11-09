package br.com.fiap.Bytecode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        System.out.println("==================================================");
        System.out.println("API DE TELECONSULTA (SPRING BOOT) INICIADA!");
        System.out.println("Acesse os endpoints em http://localhost:8080");
        System.out.println("==================================================");
    }
}