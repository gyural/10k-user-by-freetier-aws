package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.CarType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car")
@Builder
public class CarEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 입차 시간
  private LocalDateTime entranceTime;

  // 차량 번호
  @Column(nullable = false)
  private String carNumber;

  // 차종 (Enum)
  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private CarType carType;

  // 전기차 여부
  private Boolean isElectric;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }

  public CarEntity(Long id, LocalDateTime entranceTime, String carNumber, CarType carType, Boolean isElectric) {
    this.id = id;
    this.entranceTime = entranceTime;
    this.carNumber = carNumber;
    this.carType = carType;
    this.isElectric = isElectric;
  }
}
