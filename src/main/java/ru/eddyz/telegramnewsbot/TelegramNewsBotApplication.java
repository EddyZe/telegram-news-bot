package ru.eddyz.telegramnewsbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
public class TelegramNewsBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramNewsBotApplication.class, args);
    }

}
