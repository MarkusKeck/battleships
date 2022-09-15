package com.example.battleships.repository;

import com.example.battleships.entity.Turn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnRepository extends CrudRepository<Turn, Long> {
}