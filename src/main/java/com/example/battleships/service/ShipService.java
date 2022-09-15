package com.example.battleships.service;

import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Field;
import com.example.battleships.entity.Game;
import com.example.battleships.entity.Ship;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public final class ShipService {

    /**
     * Finds all coordinates for the given ship
     *
     * @param ship - The ship for which all coordinates should be calculated
     *
     * @return All coordinates of the given ship
     */

    public Set<Coordinates> getAllCoordinatesFromShip(Ship ship) {
        final Set<Coordinates> coordinates = new HashSet<>();

        final int X = ship.getCoordinates().getX();
        final int Y = ship.getCoordinates().getY();

        for (int length = 0; length < ship.getShipType().length; length++) {
            if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
                coordinates.add(new Coordinates(X + length, Y));
            } else {
                coordinates.add(new Coordinates(X, Y + length));
            }
        }
        return coordinates;
    }


    /**
     * Gets all clearance coordinates for ship in which no other ships are allowed
     *
     * @param ship  - The ship the coordinates should be calculated for
     *
     * @return All clearance coordinates for the ship
     */

    public Set<Coordinates> getClearanceCoordinatesForShip(Ship ship) {
        final Set<Coordinates> surroundingCoordinates = new HashSet<>();

        for (Coordinates shipCoordinate : this.getAllCoordinatesFromShip(ship)) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    surroundingCoordinates.add(new Coordinates(shipCoordinate.getX() + x, shipCoordinate.getY() + y));
                }
            }
        }
        return surroundingCoordinates;
    }


    /**
     * Gets the ship of the given player which coordinates also contain the provided one
     * If no ship gets found null will be returned
     *
     * @param coordinates   - The coordinate a ship gets looked for
     * @param game          - The current game
     * @param player        - The player on which side we look for a ship
     *
     * @return If a ship gets found it will be returned otherwise null
     */

    public Ship getShipFromPlayerAndCoordinates(Coordinates coordinates, Game game, Player player) {
        final Field FIELD = player.equals(Player.PLAYER_ONE) ? game.getFieldPlayerOne() : game.getFieldPlayerTwo();

        for (Ship ship : FIELD.getShips()) {
            final Set<Coordinates> shipCoordinates = getAllCoordinatesFromShip(ship);

            if (shipCoordinates.contains(coordinates))
                return ship;
        }
        return null;
    }

}
