package com.example.battleships.entity;

import com.example.battleships.enumeration.Difficulty;
import com.example.battleships.enumeration.Player;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Field fieldPlayerOne = new Field();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Field fieldPlayerTwo = new Field();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Turn> turns = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty = Difficulty.UNBEATABLE;

    @Enumerated(value = EnumType.STRING)
    private Player winner;

}
