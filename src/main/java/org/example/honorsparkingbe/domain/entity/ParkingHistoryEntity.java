package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.honorsparkingbe.domain.enums.PaymentType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name = "parkingHistory")
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class ParkingHistoryEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne  // 이것도 왜 1대1?
        @JoinColumn(name = "carId", nullable = false) // 왜 unique인지 모르겠어서 제거(그러면 기록을 저장 못하니까)
        private CarEntity carEntity;

        @ManyToOne
        @JoinColumn(name = "memberId", nullable = false)
        private MemberEntity memberEntity;

        @ManyToOne
        @JoinColumn(name = "parkingZoneId", nullable = false)
        private ParkingZoneEntity parkingZoneEntity;

        @ManyToOne
        @JoinColumn(name = "cardId")
        private CardEntity cardEntity;

        @Column(nullable = false)
        private LocalDateTime entranceTime;

        private LocalDateTime exitTime;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentType paymentType;

  @ManyToOne
  @JoinColumn(name = "payId")
  private PayEntity payEntity;

  private LocalDateTime deleteAt;
}
