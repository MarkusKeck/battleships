package com.example.battleships.repository;

import com.example.battleships.entity.Field;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends CrudRepository<Field, Long> {
}
