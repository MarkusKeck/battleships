package com.example.battleships.database;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DatabasePopulator implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // The DatabasePopulator seems not to be that popular
    }

}
