package com.example.battleships.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Getter
@NoArgsConstructor
public final class ShipsDto {

    @NotNull
    private Set<ShipDto> ships;

}
