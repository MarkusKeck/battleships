package com.example.battleships.service;

import com.example.battleships.entity.*;
import com.example.battleships.enumeration.Player;
import com.example.battleships.repository.GameRepository;
import com.example.battleships.util.DrawUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class GameService {

    private final GameRepository gameRepository;

    private final ValidationService validationService;

    private final KIService kiService;

    private final ShipService shipService;


    /**
     * Searches for all Game objects inside the Game repository
     *
     * @return All persisted Game objects
     */

    public List<Game> getAllGames() {
        return (List<Game>) gameRepository.findAll();
    }


    /**
     * Creates a new Game object and saves it to the Game repository
     *
     * @return The persisted Game object with all initialized attributes
     */

    public Game createGame() {
        return gameRepository.save(new Game());
    }


    /**
     * Places the ships on the given game in field one
     *
     * @param id    - The id of the game
     * @param ships - The ships which should be placed
     *
     * @return The game which is now also populated with the ships
     */

    public Game placeShips(Long id, Set<Ship> ships) {
        if (
                validationService.areCorrectAmountOfShipsPlaced(ships) &&
                validationService.areShipsPlacedEntirelyOnTheField(ships) &&
                validationService.haveShipsEnoughClearance(ships)
        ) {
            final Game game = gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
            game.getFieldPlayerOne().setShips(ships);
            game.getFieldPlayerTwo().setShips(kiService.placeShips());

            DrawUtil.drawFields(game);

            return gameRepository.save(game);
        }
        return null;
    }


    /**
     * Lets the human player (always PLAYER_ONE) shoot at the ships of the bot (always PLAYER_TWO)
     *
     * @param id          - The id of the currently played game
     * @param coordinates - The coordinates where the player wants to shoot at
     *
     * @return The current game object
     */

    public Game shoot(Long id, Coordinates coordinates) {
        final Game game = gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        // is game already over
        if (game.getWinner() != null)
            return null;

        final Player SHOOTING_PLAYER    = getCurrentlyShootingPlayer(game);
        final Player SHOOTING_AT_PLAYER = SHOOTING_PLAYER.equals(Player.PLAYER_ONE) ? Player.PLAYER_TWO : Player.PLAYER_ONE;

        // check if coordinates already got shot at
        if (didCoordinatesAlreadyGotShotAt(coordinates, game))
            return null;

        // allow shooting - if hit get ship
        final Ship ship = shipService.getShipFromCoordinatesAndPlayer(coordinates, game, SHOOTING_AT_PLAYER);

        final Turn HUMAN_TURN = new Turn(null, game.getTurns().size() + 1, SHOOTING_PLAYER, coordinates, ship != null);
        game.getTurns().add(HUMAN_TURN);

        // additional checks if ship got hit
        if (ship != null) {
            if (isShipSinking(ship, game)) {
                ship.setIsDestroyed(true);

                if (isGameWon(game))
                    game.setWinner(SHOOTING_PLAYER);
            }
            return gameRepository.save(game);
        }

        // missed - changing to ki
        boolean hasKiHit;
        do {
            final Coordinates KI_SHOOT_AT_COORDINATES = kiService.shoot(game);
            Ship kiShip = shipService.getShipFromCoordinatesAndPlayer(KI_SHOOT_AT_COORDINATES, game, Player.PLAYER_ONE);

            final Turn KI_TURN = new Turn(null, game.getTurns().size() + 1, Player.PLAYER_TWO, KI_SHOOT_AT_COORDINATES, kiShip != null);
            game.getTurns().add(KI_TURN);

            if (kiShip != null) {
                if (isShipSinking(kiShip, game)) {
                    kiShip.setIsDestroyed(true);

                    if (isGameWon(game)) {
                        game.setWinner(Player.PLAYER_TWO);
                        return gameRepository.save(game);
                    }
                }
                hasKiHit = true;
            } else {
                hasKiHit = false;
            }
        } while (hasKiHit);
        return gameRepository.save(game);
    }


    /**
     * Decides based on the turns played in the game who is allowed to make the next move
     *
     * @param game - The game which is played at the moment
     *
     * @return The Player who is allowed to move next
     */

    public Player getCurrentlyShootingPlayer(Game game) {
        final Turn LAST_TURN = game.getTurns().stream().filter(turn -> turn.getTurn().equals(game.getTurns().size())).findFirst().orElse(null);

        // no turns -> PLAYER_ONE begins
        if (LAST_TURN == null)
            return Player.PLAYER_ONE;

        // player of last turn hit a ship -> shoot again
        if (LAST_TURN.getHit())
            return LAST_TURN.getPlayer();

        // last player missed -> switch players
        return LAST_TURN.getPlayer().equals(Player.PLAYER_ONE) ? Player.PLAYER_TWO : Player.PLAYER_ONE;
    }


    /**
     * Checks if the coordinates have already received a shot from the currently shooting player
     *
     * @param coordinates - The coordinates which the player is shooting at
     * @param game        - The current game
     *
     * @return Coordinates already got shot at
     */

    public Boolean didCoordinatesAlreadyGotShotAt(Coordinates coordinates, Game game) {
        final Player SHOOTING_PLAYER = getCurrentlyShootingPlayer(game);
        final Set<Coordinates> COORDINATES_PLAYER_ALREADY_SHOT_AT = getCoordinatesPlayerAlreadyShotAt(SHOOTING_PLAYER, game);

        return COORDINATES_PLAYER_ALREADY_SHOT_AT.contains(coordinates);
    }


    /**
     * Gets all coordinates which a player has already shot at
     *
     * @param player - The shooting player
     * @param game   - The current game
     *
     * @return The coordinates which were shot at
     */

    public Set<Coordinates> getCoordinatesPlayerAlreadyShotAt(Player player, Game game) {
        return game.getTurns().stream().filter(turn -> turn.getPlayer().equals(player)).map(Turn::getCoordinates).collect(Collectors.toSet());
    }


    /**
     * Checks if the ship is sinking
     *
     * @param ship - The ship which should get checked
     * @param game - The current game
     *
     * @return Is the ship sinking
     */
    public Boolean isShipSinking(Ship ship, Game game) {
        final Player SHOOTING_PLAYER = getCurrentlyShootingPlayer(game);

        final Set<Coordinates> ALL_COORDINATES_OF_SHIP = shipService.getAllCoordinatesFromShip(ship);
        final Set<Coordinates> COORDINATES_PLAYER_ALREADY_SHOT_AT = getCoordinatesPlayerAlreadyShotAt(SHOOTING_PLAYER, game);

        return COORDINATES_PLAYER_ALREADY_SHOT_AT.containsAll(ALL_COORDINATES_OF_SHIP);
    }


    /**
     * Checks if the game is won
     *
     * @param game - The current game
     *
     * @return Is the game won
     */

    public Boolean isGameWon(Game game) {
        final Player SHOOTING_PLAYER = getCurrentlyShootingPlayer(game);
        final Field SHOOT_AT_FIELD = SHOOTING_PLAYER.equals(Player.PLAYER_ONE) ? game.getFieldPlayerTwo() : game.getFieldPlayerOne();

        for (Ship ship : SHOOT_AT_FIELD.getShips()) {
            if (!ship.getIsDestroyed())
                return false;
        }
        return true;
    }

}
