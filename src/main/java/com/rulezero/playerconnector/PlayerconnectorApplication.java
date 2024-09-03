package com.rulezero.playerconnector;

import jakarta.xml.bind.SchemaOutputResolver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;
// TODO: Remove all Store-Level and Store-Facing Files on an attempt to Scale down and resolve circular dependencies
@SpringBootApplication
public class PlayerconnectorApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PlayerconnectorApplication.class, args);

        MenuCLI menuCLI = context.getBean(MenuCLI.class);
        menuCLI.processUserSelections();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            MenuCLI runnerCLI = context.getBean(MenuCLI.class);
            runnerCLI.processUserSelections();
        };
    }

}
