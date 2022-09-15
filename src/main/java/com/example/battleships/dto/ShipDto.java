package com.example.battleships.dto;

import com.example.battleships.entity.Coordinates;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.ShipType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;


@Getter
@RequiredArgsConstructor
public class ShipDto {

    @NotNull
    private final Orientation orientation;

    @NotNull
    private final ShipType shipType;

    @NotNull
    private final Coordinates coordinates;

}
