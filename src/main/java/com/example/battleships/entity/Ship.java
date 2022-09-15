package com.example.battleships.entity;

import com.example.battleships.enumeration.Orientation;
import com.example.battleships.enumeration.ShipType;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Orientation orientation;

    @Enumerated(value = EnumType.STRING)
    private ShipType shipType;

    private Coordinates coordinates;

    private Boolean sunk = false;

}
