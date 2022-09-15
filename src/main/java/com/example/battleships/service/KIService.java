package com.example.battleships.service;

import com.example.battleships.config.GameConfig;
import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Game;
import com.example.battleships.entity.Ship;
import com.example.battleships.entity.Turn;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.Player;
import com.example.battleships.enumeration.ShipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KIService {

    private final FieldService fieldService;
    private final ValidationService validationService;


    public Set<Ship> placeShips() {
        Set<Ship> ships = new HashSet<>();
        final int AMOUNT_SHIPS_TOTAL = GameConfig.ships.values().stream().mapToInt(Integer::intValue).sum();

        while (ships.size() < AMOUNT_SHIPS_TOTAL) {
            ShipType shipType = this.getShipTypeToPlace(ships);
            Orientation orientation = this.getRandomOrientation();

            ships.add(getValidPlacementForShip(new Ship(null, orientation, shipType, new Coordinates(), false), ships));
        }
        return ships;
    }


    // ship placement order: AIRCRAFT_CARRIER -> BATTLESHIP -> DESTROYER -> SUBMARINE
    private ShipType getShipTypeToPlace(Set<Ship> ships) {
        final int AMOUNT_DESTROYER        = GameConfig.ships.get(ShipType.DESTROYER);
        final int AMOUNT_BATTLESHIP       = GameConfig.ships.get(ShipType.BATTLESHIP);
        final int AMOUNT_AIRCRAFT_CARRIER = GameConfig.ships.get(ShipType.AIRCRAFT_CARRIER);

        final long PLACED_DESTROYER_COUNT        = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.DESTROYER)).count();
        final long PLACED_BATTLESHIP_COUNT       = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.BATTLESHIP)).count();
        final long PLACED_AIRCRAFT_CARRIER_COUNT = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.AIRCRAFT_CARRIER)).count();

        if (PLACED_AIRCRAFT_CARRIER_COUNT < AMOUNT_AIRCRAFT_CARRIER) {
            return ShipType.AIRCRAFT_CARRIER;
        } else if (PLACED_BATTLESHIP_COUNT < AMOUNT_BATTLESHIP) {
            return ShipType.BATTLESHIP;
        } else if (PLACED_DESTROYER_COUNT < AMOUNT_DESTROYER) {
            return ShipType.DESTROYER;
        } else {
            return ShipType.SUBMARINE;
        }
    }

    private Orientation getRandomOrientation() {
        return Orientation.values()[new Random().nextInt(Orientation.values().length)];
    }

    private Ship getValidPlacementForShip(Ship ship, Set<Ship> ships) {
        // calculate max x and y based on orientation
        int maxX = GameConfig.width;
        int maxY = GameConfig.height;

        if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
            maxX -= ship.getShipType().length - 1;
        } else {
            maxY -= ship.getShipType().length - 1;
        }

        // find random position
        int randX = new Random().nextInt(maxX) + 1;
        int randY = new Random().nextInt(maxY) + 1;

        // move ship until it found a valid position
        for (int ix = 0; ix < maxX; ix++) {
            int x = ((randX + ix) % maxX) + 1;

            for (int iy = 0; iy < maxY; iy++) {
                int y = ((randY + iy) % maxY) + 1;

                ship.setCoordinates(new Coordinates(x, y));
                if (validationService.isShipPlacementValid(ship, ships)) {
                    return ship;
                }
            }
        }
        return null;
    }


    public Coordinates shoot(Game game) {
        Set<Coordinates> shipCoordinates  = fieldService.getCoordinatesWithShips(game.getFieldPlayerOne());
        Set<Coordinates> waterCoordinates = fieldService.getCoordinatesWithWater(game.getFieldPlayerOne());

        Set<Coordinates> shotAt = getShotCoordinatesFromPlayerField(game, Player.PLAYER_ONE);

        Set<Coordinates> unharmedShipCoordinates  = new HashSet<>(shipCoordinates);
        Set<Coordinates> unharmedWaterCoordinates = new HashSet<>(waterCoordinates);

        unharmedShipCoordinates.removeAll(shotAt);
        unharmedWaterCoordinates.removeAll(shotAt);

        int accuracy = game.getDifficulty().accuracy;
        int random = new Random().nextInt(100) + 1;

        if (random <= accuracy && unharmedShipCoordinates.size() > 0) {
            return unharmedShipCoordinates.stream().toList().get(new Random().nextInt(unharmedShipCoordinates.size()));
        } else {
            return unharmedWaterCoordinates.stream().toList().get(new Random().nextInt(unharmedWaterCoordinates.size()));
        }
    }

    public Set<Coordinates> getShotCoordinatesFromPlayerField(Game game, Player player) {
        return game.getTurns().stream().filter(turn -> !turn.getPlayer().equals(player)).map(Turn::getCoordinates).collect(Collectors.toSet());
    }

}
