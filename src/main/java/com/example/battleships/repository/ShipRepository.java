package com.example.battleships.repository;

import com.example.battleships.entity.Ship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends CrudRepository<Ship, Long> {
}
