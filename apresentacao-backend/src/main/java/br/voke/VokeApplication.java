package br.voke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VokeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VokeApplication.class, args);
    }
}
