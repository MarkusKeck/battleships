package com.example.battleships.service;

import com.example.battleships.config.GameConfig;
import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Field;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final ShipService shipService;


    /**
     * Gets all the coordinates of every ships which is placed on the given field
     *
     * @param field Field - The field which the ship coordinates should get extract from
     * @return Set<Coordinates> - The coordinates which are occupied by ships
     */

    public Set<Coordinates> getCoordinatesWithShips(Field field) {
        return field.getShips().stream().map(shipService::getAllCoordinatesFromShip).flatMap(Collection::stream).collect(Collectors.toSet());
    }


    /**
     * Gets the coordinates of every quadrant which is empty and not occupied by a ship and is therefore water
     * This is the inverse function of getCoordinatesWithShip()
     *
     * @param field Field - The field which should be searched for water quadrants
     * @return Set<Coordinates> - The coordinates which are occupied by water
     */

    public Set<Coordinates> getCoordinatesWithWater(Field field) {
        Set<Coordinates> shipCoordinates = getCoordinatesWithShips(field);
        Set<Coordinates> waterCoordinates = new HashSet<>();

        for (int x = 1; x <= GameConfig.width; x++) {
            for (int y = 1; y <= GameConfig.height; y++) {
                waterCoordinates.add(new Coordinates(x, y));
            }
        }
        waterCoordinates.removeAll(shipCoordinates);
        return waterCoordinates;
    }

}
