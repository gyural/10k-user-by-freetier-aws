package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.ExpoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpoRepository extends JpaRepository<ExpoEntity, Long> {

    void deleteByUserId(String authId);

    Optional<ExpoEntity> findByUserId(String userId);
}
