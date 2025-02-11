package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table( name = "district")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DistrictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String name; // 군구 이름
}
