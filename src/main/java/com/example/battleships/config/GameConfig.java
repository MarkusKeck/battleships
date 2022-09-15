package com.example.battleships.config;

import com.example.battleships.enumeration.ShipType;

import java.util.HashMap;
import java.util.Map;

public final class GameConfig {

    public final static Integer WIDTH = 10;
    public final static Integer HEIGHT = 10;

    public final static Map<ShipType, Integer> ships = new HashMap<>();

    static {
        ships.put(ShipType.SUBMARINE, 4);
        ships.put(ShipType.DESTROYER, 3);
        ships.put(ShipType.BATTLESHIP, 2);
        ships.put(ShipType.AIRCRAFT_CARRIER, 1);
    }

}