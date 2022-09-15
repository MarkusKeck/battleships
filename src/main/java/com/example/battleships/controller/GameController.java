package com.example.battleships.controller;

import com.example.battleships.dto.CoordinatesDto;
import com.example.battleships.dto.ShipDto;
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

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public final class GameController {

    private final GameService gameService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<Game>> getAll() {
        return new ResponseEntity<>(gameService.getAllGames(), HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<Game> create() {
        return new ResponseEntity<>(gameService.createGame(), HttpStatus.CREATED);
    }


    @PutMapping("/{id}/placeShips")
    public ResponseEntity<Game> placeShips(@PathVariable("id") Long id, @Valid @RequestBody Set<ShipDto> shipDtoCreateSet) {
        Set<Ship> ships = modelMapper.map(shipDtoCreateSet, new TypeToken<Set<Ship>>() {}.getType());
        return new ResponseEntity<>(gameService.placeShips(id, ships), HttpStatus.OK);
    }


    @PutMapping("/{id}/shoot")
    public ResponseEntity<Game> fire(@PathVariable("id") Long id, @Valid @RequestBody CoordinatesDto coordinatesDto) {
        return new ResponseEntity<>(gameService.shoot(id, modelMapper.map(coordinatesDto, Coordinates.class)), HttpStatus.OK);
    }

}
