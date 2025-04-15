package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.ExpoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpoRepository extends JpaRepository<ExpoEntity, Long> {

    void deleteByUserId(String authId);
}
