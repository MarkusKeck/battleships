package com.example.battleships.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class Coordinates {

    private Integer x;

    private Integer y;

}
