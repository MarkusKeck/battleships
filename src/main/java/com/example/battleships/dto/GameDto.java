package com.example.battleships.dto;

import com.example.battleships.enumeration.Difficulty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public final class GameDto {

    @NotNull
    private Difficulty difficulty;

}
