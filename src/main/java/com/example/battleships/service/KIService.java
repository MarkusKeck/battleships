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
public final class KIService {

    private final FieldService fieldService;
    private final ValidationService validationService;


    /**
     * The KI places its ships to be later populated on the field
     *
     * @return The set of the ships
     */

    public Set<Ship> placeShips() {
        final Set<Ship> ships = new HashSet<>();
        final int AMOUNT_SHIPS_TOTAL = GameConfig.ships.values().stream().mapToInt(Integer::intValue).sum();

        while (ships.size() < AMOUNT_SHIPS_TOTAL) {
            final ShipType    SHIP_TYPE   = this.getShipTypeToPlace(ships);
            final Orientation ORIENTATION = this.getRandomOrientation();

            ships.add(
                getValidPlacementForShip(
                    new Ship(null, ORIENTATION, SHIP_TYPE, new Coordinates(), false), ships
                )
            );
        }
        return ships;
    }


    /**
     * Decides which ship should be placed next
     * AIRCRAFT_CARRIER -> BATTLESHIP -> DESTROYER -> SUBMARINE
     *
     * @param ships - The current set of ships
     *
     * @return The ShipType which should be placed next
     */

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


    /**
     * Get a random orientation for the ship
     *
     * @return The random orientation
     */

    private Orientation getRandomOrientation() {
        return Orientation.values()[new Random().nextInt(Orientation.values().length)];
    }


    /**
     * Finds a valid placement for the given ship and avoids collision with other ships
     *
     * @param ship  - The ship which should be placed
     * @param ships - The other ships which are already planned for placement
     *
     * @return The given ship stored with the new valid coordinates
     */

    private Ship getValidPlacementForShip(Ship ship, Set<Ship> ships) {

        // calculate max x and y based on orientation
        int maxX = GameConfig.WIDTH;
        int maxY = GameConfig.HEIGHT;

        if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
            maxX -= ship.getShipType().length - 1;
        } else {
            maxY -= ship.getShipType().length - 1;
        }

        // find random position
        final int RANDOM_X = new Random().nextInt(maxX) + 1;
        final int RANDOM_Y = new Random().nextInt(maxY) + 1;

        // move ship until it found a valid position
        for (int ix = 0; ix < maxX; ix++) {
            final int X = ((RANDOM_X + ix) % maxX) + 1;

            for (int iy = 0; iy < maxY; iy++) {
                final int Y = ((RANDOM_Y + iy) % maxY) + 1;

                ship.setCoordinates(new Coordinates(X, Y));
                if (validationService.isShipPlacementValid(ship, ships))
                    return ship;
            }
        }
        return null;
    }


    /**
     * The KI shoots at the enemy field based on the set difficulty according to its accuracy
     * It only shoots at valid coordinates
     *
     * @param game  - The game which is beeing played
     *
     * @return The coordinates which the ki decided to shoot at
     */

    public Coordinates shoot(Game game) {
        final Set<Coordinates> SHIP_COORDINATES  = fieldService.getCoordinatesWithShips(game.getFieldPlayerOne());
        final Set<Coordinates> WATER_COORDINATES = fieldService.getCoordinatesWithWater(game.getFieldPlayerOne());

        final Set<Coordinates> shotAt = getAllShotCoordinatesByPlayer(game, Player.PLAYER_TWO);

        final Set<Coordinates> unharmedShipCoordinates  = new HashSet<>(SHIP_COORDINATES);
        final Set<Coordinates> unharmedWaterCoordinates = new HashSet<>(WATER_COORDINATES);

        unharmedShipCoordinates.removeAll(shotAt);
        unharmedWaterCoordinates.removeAll(shotAt);

        final int SUM_COORDINATES = GameConfig.WIDTH * GameConfig.HEIGHT;

        final int ACCURACY = game.getDifficulty().accuracy;
        final int RANDOM = new Random().nextInt(SUM_COORDINATES) + 1;

        // shoot at ships if RANDOM <= ACCURACY and there are still ships to be shot at
        if (RANDOM <= ACCURACY && unharmedShipCoordinates.size() > 0) {
            return unharmedShipCoordinates.stream().toList().get(new Random().nextInt(unharmedShipCoordinates.size()));
        } else {
            return unharmedWaterCoordinates.stream().toList().get(new Random().nextInt(unharmedWaterCoordinates.size()));
        }
    }


    /**
     * Gets all coordinates the given player already shot at
     *
     * @param game      - The game which is currently played
     * @param player    - The player which shots should be collected
     *
     * @return The coordinates the player already shot at
     */

    public Set<Coordinates> getAllShotCoordinatesByPlayer(Game game, Player player) {
        return game.getTurns().stream().filter(turn -> turn.getPlayer().equals(player)).map(Turn::getCoordinates).collect(Collectors.toSet());
    }

}
