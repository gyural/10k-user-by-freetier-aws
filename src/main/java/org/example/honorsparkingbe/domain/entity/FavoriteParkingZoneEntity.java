package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "favoriteParkingZone")
@Builder

public class FavoriteParkingZoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ParkingZone 연관 관계
    @ManyToOne
    @JoinColumn(name = "parkingZoneId", nullable = false)
    private ParkingZoneEntity parkingZoneEntity;

    // Member 연관 관계
    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private MemberEntity memberEntity;
}
