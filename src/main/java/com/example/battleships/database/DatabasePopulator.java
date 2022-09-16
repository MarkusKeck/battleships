package com.example.battleships.database;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DatabasePopulator implements CommandLineRunner {

    @Value("${battleship.eigene-property}")
    private String meineEigeneProperty;

    @Override
    public void run(String... args) {

        System.out.println(meineEigeneProperty);

        // The DatabasePopulator seems not to be that popular
    }

}
