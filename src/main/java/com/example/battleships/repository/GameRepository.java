package com.example.battleships.repository;

import com.example.battleships.entity.Game;
import com.example.battleships.entity.Turn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {}
