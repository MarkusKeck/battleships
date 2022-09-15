package com.example.battleships.enumeration;

public enum ShipType {

    SUBMARINE(1),
    DESTROYER(2),
    BATTLESHIP(3),
    AIRCRAFT_CARRIER(4);

    public final Integer length;

    ShipType(Integer length) {
        this.length = length;
    }

}
