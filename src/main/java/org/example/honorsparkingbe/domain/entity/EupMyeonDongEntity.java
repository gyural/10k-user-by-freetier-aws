package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table( name = "eupMyeonDong")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EupMyeonDongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String name; // 읍면동 이름
}
