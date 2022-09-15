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
public class ShipService {

    public Set<Coordinates> getAllCoordinatesFromShip(Ship ship) {
        Set<Coordinates> coordinates = new HashSet<>();

        final int x = ship.getCoordinates().getX();
        final int y = ship.getCoordinates().getY();

        for (int length = 0; length < ship.getShipType().length; length++) {
            if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
                coordinates.add(new Coordinates(x + length, y));
            } else {
                coordinates.add(new Coordinates(x, y + length));
            }
        }
        return coordinates;
    }

    public Set<Coordinates> getShipAndSurroundingCoordinates(Ship ship) {
        Set<Coordinates> surroundingCoordinates = new HashSet<>();

        for (Coordinates shipCoordinate : this.getAllCoordinatesFromShip(ship)) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    surroundingCoordinates.add(new Coordinates(shipCoordinate.getX() + x, shipCoordinate.getY() + y));
                }
            }
        }
        return surroundingCoordinates;
    }

    public Ship getShipFromCoordinatesAndPlayer(Coordinates coordinates, Game game, Player player) {
        Field field = player.equals(Player.PLAYER_ONE) ? game.getFieldPlayerOne() : game.getFieldPlayerTwo();

        for (Ship ship : field.getShips()) {
            Set<Coordinates> shipCoordinates = getAllCoordinatesFromShip(ship);

            if (shipCoordinates.contains(coordinates))
                return ship;
        }
        return null;
    }

}
