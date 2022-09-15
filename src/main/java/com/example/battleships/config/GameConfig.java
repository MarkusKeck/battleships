package com.example.battleships.config;

import com.example.battleships.enumeration.ShipType;

import java.util.HashMap;
import java.util.Map;

public class GameConfig {

    public static final Integer width = 10;
    public static final Integer height = 10;

    public static Map<ShipType, Integer> ships = new HashMap<>(Map.of(
        ShipType.SUBMARINE, 4,
        ShipType.DESTROYER, 3,
        ShipType.BATTLESHIP, 2,
        ShipType.AIRCRAFT_CARRIER, 1
    ));

}
