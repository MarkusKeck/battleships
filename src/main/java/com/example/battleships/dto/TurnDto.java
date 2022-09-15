package com.example.battleships.dto;

import com.example.battleships.entity.Coordinates;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class TurnDto {

    @NotNull
    private Coordinates coordinates;

}
