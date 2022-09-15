package com.example.battleships.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public final class CoordinatesDto {

    @NotNull
    @Min(value = 1, message = "lowest possible x value is 1")
    @Max(value = 10, message = "highest possible x value is {GameConfig.WIDTH}")
    private Integer x;

    @NotNull
    @Min(value = 1, message = "lowest possible y value is 1")
    @Max(value = 10, message = "highest possible y value is {GameConfig.HEIGHT}")
    private Integer y;

}
