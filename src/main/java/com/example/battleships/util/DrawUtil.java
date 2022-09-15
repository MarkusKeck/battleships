package com.example.battleships.util;

import com.example.battleships.config.GameConfig;
import com.example.battleships.entity.Field;
import com.example.battleships.entity.Game;
import com.example.battleships.entity.Ship;
import com.example.battleships.enumeration.Orientation;

public class DrawUtil {

    /**
     * Draws the squares of the two players with all the corresponding ships.
     * Ships are shown as full spaces (■) while unoccupied spaces are shown as empty spaces (□).
     *
     * @param game Game - The game which fields should be drawn
     */

    public static void drawFields(Game game) {
        System.out.println();
        DrawUtil.drawField(game.getFieldPlayerOne());
        System.out.println();
        DrawUtil.drawField(game.getFieldPlayerTwo());
    }


    /**
     * Draws the squares of one field with all placed ships.
     * Ships are shown as full spaces (■) while unoccupied spaces are shown as empty spaces (□).
     *
     * @param field Field - The field which should be drawn
     */

    public static void drawField(Field field) {
        final String FIELD_EMPTY  = "□";
        final String FIELD_FILLED = "■";

        String[][] ocean = new String[GameConfig.width + 1][GameConfig.height + 1];

        // populate water
        for (int x = 0; x <= GameConfig.width; x++) {
            for (int y = 0; y <= GameConfig.height; y++) {
                ocean[x][y] = FIELD_EMPTY;
                if (x == 0 || y == 0) // populate coordinates axes
                    ocean[x][y] = String.valueOf(x + y);
            }
        }

        // populate ships
        for (Ship ship : field.getShips()) {
            final int x = ship.getCoordinates().getX();
            final int y = ship.getCoordinates().getY();

            for (int length = 0; length < ship.getShipType().length; length++) {
                if (ship.getOrientation().equals(Orientation.HORIZONTAL)) {
                    ocean[x + length][y] = FIELD_FILLED;
                } else {
                    ocean[x][y + length] = FIELD_FILLED;
                }
            }
        }

        // draw
        for (int y = 0; y <= GameConfig.height; y++) {
            for (int x = 0; x <= GameConfig.width; x++)
                System.out.printf("%1$3s", ocean[x][y]);
            System.out.println();
        }
    }

}
