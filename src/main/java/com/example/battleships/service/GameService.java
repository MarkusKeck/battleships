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
public class GameService {

    private final GameRepository gameRepository;

    private final ValidationService validationService;

    private final KIService kiService;

    private final ShipService shipService;


    /**
     * Searches for all Game objects inside the Game repository
     *
     * @return all persisted Game objects
     */

    public List<Game> getAllGames() {
        return (List<Game>) gameRepository.findAll();
    }


    /**
     * Creates a new Game object and saves it to the Game repository
     *
     * @return the persisted Game object with all initialized attributes
     */

    public Game createGame() {
        return gameRepository.save(new Game());
    }

    /**
     * Places the ships on the given game in field one
     *
     * @param id    - The id of the game
     * @param ships - The ships which should be placed
     * @return      - The game which is now also populated with the ships
     */
    public Game placeShips(Long id, Set<Ship> ships) {
        if (
                validationService.areCorrectAmountOfShipsPlaced(ships) &&
                validationService.areShipsPlacedEntirelyOnTheField(ships) &&
                validationService.haveShipsEnoughClearance(ships)
        ) {
            Game game = gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
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
     * @param id    - The id of the currently played game
     * @param turn  - The turn of the player with the given coordinates
     * @return      - The current game object
     */

    public Game shoot(Long id, Turn turn) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        // is game already over
        if (game.getWinner() != null)
            return null;

        Coordinates shootAtCoordinates = turn.getCoordinates();

        Player shootingPlayer   = getCurrentlyShootingPlayer(game);
        Player shootingAtPlayer = shootingPlayer.equals(Player.PLAYER_ONE) ? Player.PLAYER_TWO : Player.PLAYER_ONE;

        // check if coordinates already got shot at
        if (didCoordinatesAlreadyGotShotAt(shootAtCoordinates, game))
            return null;

        // allow shooting - if hit get ship
        Ship ship = shipService.getShipFromCoordinatesAndPlayer(shootAtCoordinates, game, shootingAtPlayer);

        turn.setPlayer(shootingPlayer);
        turn.setTurn(game.getTurns().size() + 1);
        turn.setHit(ship != null);

        game.getTurns().add(turn);

        // additional checks if ship got hit
        if (ship != null) {
            if (isShipSinking(ship, game)) {
                ship.setSunk(true);

                if (isGameWon(game))
                    game.setWinner(shootingPlayer);

            }
            return gameRepository.save(game);
        }

        // missed - changing to ki
        boolean kiShootAgain;
        do {
            Coordinates kiShootAt = kiService.shoot(game);
            Ship kiShip = shipService.getShipFromCoordinatesAndPlayer(kiShootAt, game, Player.PLAYER_ONE);

            Turn kiTurn = new Turn(null, game.getTurns().size() + 1, Player.PLAYER_TWO, kiShootAt, kiShip != null);
            game.getTurns().add(kiTurn);

            if (kiShip != null) {
                if (isShipSinking(kiShip, game)) {
                    kiShip.setSunk(true);

                    if (isGameWon(game)) {
                        game.setWinner(Player.PLAYER_TWO);
                        return gameRepository.save(game);
                    }
                }
                kiShootAgain = true;
            } else {
                kiShootAgain = false;
            }
        } while (kiShootAgain);
        return gameRepository.save(game);
    }


    /**
     * Decides based on the turns played in the game who is allowed to make the next move
     *
     * @param game - The game which is played at the moment
     * @return     - The Player who is allowed to move next
     */

    public Player getCurrentlyShootingPlayer(Game game) {
        Turn lastTurn = game.getTurns().stream().filter(turn -> turn.getTurn().equals(game.getTurns().size())).findFirst().orElse(null);

        // no turns -> PLAYER_ONE begins
        if (lastTurn == null)
            return Player.PLAYER_ONE;

        // player of last turn hit a ship -> shoot again
        if (lastTurn.getHit())
            return lastTurn.getPlayer();

        // last player missed -> switch players
        return lastTurn.getPlayer().equals(Player.PLAYER_ONE) ? Player.PLAYER_TWO : Player.PLAYER_ONE;
    }


    /**
     * Checks if the coordinates have already received a shot from the currently shooting player
     *
     * @param coordinates - The coordinates which the player is shooting at
     * @param game        - The current game
     * @return            - Coordinates already got shot at
     */

    public Boolean didCoordinatesAlreadyGotShotAt(Coordinates coordinates, Game game) {
        Player shootingPlayer = getCurrentlyShootingPlayer(game);
        Set<Coordinates> alreadyShotQuadrants = getCoordinatesPlayerAlreadyShotAt(shootingPlayer, game);

        return alreadyShotQuadrants.contains(coordinates);
    }


    /**
     * Gets all coordinates which a player has already shot at
     *
     * @param player - The shooting player
     * @param game   - The current game
     * @return       - The coordinates which were shot at
     */

    public Set<Coordinates> getCoordinatesPlayerAlreadyShotAt(Player player, Game game) {
        return game.getTurns().stream().filter(turn -> turn.getPlayer().equals(player)).map(Turn::getCoordinates).collect(Collectors.toSet());
    }


    /**
     * Checks if the ship is sinking
     *
     * @param ship - The ship which should get checked
     * @param game - The current game
     * @return     - Is the ship sinking
     */
    public Boolean isShipSinking(Ship ship, Game game) {
        Player shootingPlayer = getCurrentlyShootingPlayer(game);

        Set<Coordinates> allCoordinatesOfShip = shipService.getAllCoordinatesFromShip(ship);
        Set<Coordinates> playerShotAtCoordinates = getCoordinatesPlayerAlreadyShotAt(shootingPlayer, game);

        return playerShotAtCoordinates.containsAll(allCoordinatesOfShip);
    }


    /**
     * Checks if the game is won
     *
     * @param game - The current game
     * @return     - Is the game won
     */

    public Boolean isGameWon(Game game) {
        final Player shootingPlayer = getCurrentlyShootingPlayer(game);
        final Field fieldWhichGetsShotAt = shootingPlayer.equals(Player.PLAYER_ONE) ? game.getFieldPlayerTwo() : game.getFieldPlayerOne();

        for (Ship ship : fieldWhichGetsShotAt.getShips()) {
            if (!ship.getSunk())
                return false;
        }
        return true;
    }

}
