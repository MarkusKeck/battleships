package com.example.battleships.controller;

import com.example.battleships.dto.CoordinatesDto;
import com.example.battleships.dto.GameDto;
import com.example.battleships.dto.ShipsDto;
import com.example.battleships.entity.Coordinates;
import com.example.battleships.entity.Game;
import com.example.battleships.entity.Ship;
import com.example.battleships.service.GameService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public final class GameController {

    private final GameService gameService;
    private final ModelMapper modelMapper;

    /**
     * Gets all battleship games which are currently played or have been playing
     *
     * @return A list of all games
     */

    @GetMapping
    public ResponseEntity<List<Game>> getAll() {
        return new ResponseEntity<>(gameService.getAllGames(), HttpStatus.OK);
    }


    /**
     * Create a new empty game without ships
     *
     * @return The persisted game
     */

    @PostMapping
    public ResponseEntity<Game> create(@Valid @RequestBody GameDto gameDto) {
        return new ResponseEntity<>(gameService.createGame(modelMapper.map(gameDto, Game.class)), HttpStatus.CREATED);
    }


    /**
     * Place the ships of a player
     *
     * @param id                - The id of the game
     * @param shipsDto          - The set of ships which the player wants to place
     *
     * @return The updated game
     */

    @PutMapping("/{id}/placeShips")
    public ResponseEntity<Game> placeShips(@PathVariable("id") Long id, @Valid @RequestBody ShipsDto shipsDto) {
        final Set<Ship> ships = modelMapper.map(shipsDto.getShips(), new TypeToken<Set<Ship>>() {}.getType());
        return new ResponseEntity<>(gameService.placeShips(id, ships), HttpStatus.OK);
    }


    /**
     * Fire at a given coordinate
     * If the player hit a ship the turn will be populated accordingly
     * If the shot results in a ship sinking the ship will be flagged as destroyed
     * Should all ships be sunk the winner will be populated
     *
     * @param id                - The game id of the current game
     * @param coordinatesDto    - The coordinate the player wants to shoot at
     *
     * @return The updated game entity
     */

    @PutMapping("/{id}/shoot")
    public ResponseEntity<Game> fire(@PathVariable("id") Long id, @Valid @RequestBody CoordinatesDto coordinatesDto) {
        return new ResponseEntity<>(gameService.shoot(id, modelMapper.map(coordinatesDto, Coordinates.class)), HttpStatus.OK);
    }

}
