package com.example.battleships.database;

import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Game;
import com.example.battleships.entity.Ship;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.ShipType;
import com.example.battleships.repository.GameRepository;
import com.example.battleships.repository.TurnRepository;
import com.example.battleships.service.KIService;
import com.example.battleships.util.DrawUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Populator implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final KIService kiService;

    private final TurnRepository turnRepository;


    @Override
    public void run(String... args) {

        // create game
        Game game1 = new Game();
        game1.getFieldPlayerOne().setShips(kiService.placeShips());
        game1.getFieldPlayerTwo().setShips(kiService.placeShips());

        gameRepository.save(game1);

        kiService.shoot(game1);

        DrawUtil.drawFields(game1);

    }
}
