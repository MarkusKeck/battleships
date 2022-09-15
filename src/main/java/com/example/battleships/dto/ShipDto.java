package com.example.battleships.dto;

import com.example.battleships.entity.Coordinates;
import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.ShipType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
public final class ShipDto {

    @NotNull
    private Orientation orientation;

    @NotNull
    private ShipType shipType;

    @NotNull
    private Coordinates coordinates;

}
