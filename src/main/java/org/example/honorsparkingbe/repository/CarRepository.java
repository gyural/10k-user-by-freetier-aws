package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {
}
