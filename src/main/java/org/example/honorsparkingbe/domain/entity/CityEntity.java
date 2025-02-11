package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Table( name = "city")


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(unique = true, nullable = false)
    private String name; // 시도 이름
}
