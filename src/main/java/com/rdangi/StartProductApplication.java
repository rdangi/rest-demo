package com.rdangi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@SpringBootApplication
public class StartProductApplication {

    // start everything
    public static void main(String[] args) {
        SpringApplication.run(StartProductApplication.class, args);
    }

    // run this only on profile 'demo', avoid run this in test
    @Profile("demo")
    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            repository.save(new Product("A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41")));
            repository.save(new Product("The Life-Changing Magic of Tidying Up", "Marie Kondo", new BigDecimal("9.69")));
            repository.save(new Product("Refactoring: Improving the Design of Existing Code", "Martin Fowler", new BigDecimal("47.99")));
        };
    }
}