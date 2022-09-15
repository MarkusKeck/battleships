package com.example.battleships.service;

import com.example.battleships.config.GameConfig;
import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Ship;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.ShipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ShipService shipService;

    public Boolean areCorrectAmountOfShipsPlaced(Set<Ship> ships) {
        final long AMOUNT_SUBMARINE        = GameConfig.ships.get(ShipType.SUBMARINE);
        final long AMOUNT_DESTROYER        = GameConfig.ships.get(ShipType.DESTROYER);
        final long AMOUNT_BATTLESHIP       = GameConfig.ships.get(ShipType.BATTLESHIP);
        final long AMOUNT_AIRCRAFT_CARRIER = GameConfig.ships.get(ShipType.AIRCRAFT_CARRIER);

        final long FIELD_SUBMARINE_COUNT        = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.SUBMARINE)).count();
        final long FIELD_DESTROYER_COUNT        = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.DESTROYER)).count();
        final long FIELD_BATTLESHIP_COUNT       = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.BATTLESHIP)).count();
        final long FIELD_AIRCRAFT_CARRIER_COUNT = ships.stream().filter(ship -> ship.getShipType().equals(ShipType.AIRCRAFT_CARRIER)).count();

        return (
                FIELD_SUBMARINE_COUNT == AMOUNT_SUBMARINE &&
                        FIELD_DESTROYER_COUNT == AMOUNT_DESTROYER &&
                        FIELD_BATTLESHIP_COUNT == AMOUNT_BATTLESHIP &&
                        FIELD_AIRCRAFT_CARRIER_COUNT == AMOUNT_AIRCRAFT_CARRIER
        );
    }

    public Boolean isShipPlacedEntirelyOnTheField(Ship ship) {
        final int x = ship.getCoordinates().getX();
        final int y = ship.getCoordinates().getY();
        final int length = ship.getShipType().length;

        if (x < 1 || x > GameConfig.width || y < 1 || y > GameConfig.height)
            return false;

        if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
            return (x + length - 1) <= GameConfig.width;
        } else {
            return (y + length - 1) <= GameConfig.height;
        }
    }

    public Boolean areShipsPlacedEntirelyOnTheField(Set<Ship> ships) {
        for (Ship ship : ships) {
            if (!isShipPlacedEntirelyOnTheField(ship))
                return false;
        }
        return true;
    }

    public Boolean haveShipsEnoughClearance(Set<Ship> ships) {
        for (Ship ship : ships) {
            Set<Ship> otherShips = new HashSet<>(ships);
            otherShips.remove(ship);

            if (!hasShipEnoughClearance(ship, otherShips))
                return false;
        }
        return true;
    }

    public Boolean hasShipEnoughClearance(Ship ship, Set<Ship> ships) {
        Set<Coordinates> clearanceCoordinates = shipService.getShipAndSurroundingCoordinates(ship);
        Set<Coordinates> otherCoordinates = ships.stream().map(shipService::getAllCoordinatesFromShip).flatMap(Collection::stream).collect(Collectors.toSet());

        if (!Collections.disjoint(clearanceCoordinates, otherCoordinates))
            return false;

        return true;
    }

    public Boolean isShipPlacementValid(Ship ship, Set<Ship> ships) {
        if (!isShipPlacedEntirelyOnTheField(ship))
            return false;

        return hasShipEnoughClearance(ship, ships);
    }

}
