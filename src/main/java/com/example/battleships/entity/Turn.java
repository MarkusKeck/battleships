package com.example.battleships.entity;

import com.example.battleships.enumeration.Player;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Turn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer turn;

    @Enumerated(value = EnumType.STRING)
    private Player player;

    private Coordinates coordinates;

    private Boolean hit;

}
