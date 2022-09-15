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
public final class ValidationService {

    private final ShipService shipService;


    /**
     * Validates if the player has set the correct amount of ships for each ShipType
     *
     * @param ships - All ships the player has placed
     *
     * @return When all placed ships with their ShipType match the expected amount ture, otherwise false
     */

    public Boolean areCorrectAmountOfShipsPlaced(Set<Ship> ships) {
        final int AMOUNT_SUBMARINE        = GameConfig.ships.get(ShipType.SUBMARINE);
        final int AMOUNT_DESTROYER        = GameConfig.ships.get(ShipType.DESTROYER);
        final int AMOUNT_BATTLESHIP       = GameConfig.ships.get(ShipType.BATTLESHIP);
        final int AMOUNT_AIRCRAFT_CARRIER = GameConfig.ships.get(ShipType.AIRCRAFT_CARRIER);

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


    /**
     * Checks if the ship is within bounds of the playfield
     *
     * @param ship  - The ship which gets checked
     *
     * @return If the ship is within bounds returns true, otherwise false
     */

    public Boolean isShipPlacedEntirelyOnTheField(Ship ship) {
        final int x = ship.getCoordinates().getX();
        final int y = ship.getCoordinates().getY();
        final int length = ship.getShipType().length;

        if (x < 1 || x > GameConfig.WIDTH || y < 1 || y > GameConfig.HEIGHT)
            return false;

        if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
            return (x + length - 1) <= GameConfig.WIDTH;
        } else {
            return (y + length - 1) <= GameConfig.HEIGHT;
        }
    }


    /**
     * Checks if all ships are placed within bounds of the playfield
     *
     * @param ships - The ships which should be checked
     *
     * @return If the ships are within bounds returns true, otherwise false
     */

    public Boolean areShipsPlacedEntirelyOnTheField(Set<Ship> ships) {
        for (Ship ship : ships) {
            if (!isShipPlacedEntirelyOnTheField(ship))
                return false;
        }
        return true;
    }


    /**
     * Checks if the ship has no other ship within its collision zone
     *
     * @param ship  - The ship which checks for clearance collisions
     * @param ships - All other ships placed on the field
     *
     * @return Returns if ship has a no clearance violation
     */

    public Boolean hasShipEnoughClearance(Ship ship, Set<Ship> ships) {
        final Set<Coordinates> SHIP_CLEARANCE_COORDINATES = shipService.getClearanceCoordinatesForShip(ship);
        final Set<Coordinates> OTHER_SHIPS_COORDINATES = ships.stream().map(shipService::getAllCoordinatesFromShip).flatMap(Collection::stream).collect(Collectors.toSet());

        return Collections.disjoint(SHIP_CLEARANCE_COORDINATES, OTHER_SHIPS_COORDINATES);
    }


    /**
     * Checks if the ships have no other ship within their collision zone
     *
     * @param ships - All ships who should be checked for clearance viiolations
     *
     * @return Returns if ships have no clearance violation
     */

    public Boolean haveShipsEnoughClearance(Set<Ship> ships) {
        for (Ship ship : ships) {
            final Set<Ship> otherShips = new HashSet<>(ships);
            otherShips.remove(ship);

            if (!hasShipEnoughClearance(ship, otherShips))
                return false;
        }
        return true;
    }


    /**
     * Checks if the Ship with the current coordinates can be placed on the field without any violations
     *
     * @param ship  - The ship which wants to be placed
     * @param ships - All other ships
     *
     * @return Returns if ship placement is valid
     */

    public Boolean isShipPlacementValid(Ship ship, Set<Ship> ships) {
        if (!isShipPlacedEntirelyOnTheField(ship))
            return false;

        return hasShipEnoughClearance(ship, ships);
    }

}
