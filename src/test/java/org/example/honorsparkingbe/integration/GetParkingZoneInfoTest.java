package org.example.honorsparkingbe.integration;

import org.example.honorsparkingbe.unit.controller.ControllerAdviceTest.TestController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
public class GetParkingZoneInfoTest {

  private static final String MYSQL_IMAGE = "mysql:latest";
  private static final String REDIS_IMAGE = "redis:latest";
  private static final int REDIS_PORT = 6379;

  @Container
  @ServiceConnection
  static public GenericContainer<?> redis = new GenericContainer<>(
      DockerImageName.parse(REDIS_IMAGE))
      .withExposedPorts(REDIS_PORT)
      .waitingFor(Wait.forListeningPort())
      .waitingFor(Wait.defaultWaitStrategy());

  @Container
  static public MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
      .waitingFor(Wait.forListeningPort());

  @BeforeAll
  static void setUp() {
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getFirstMappedPort().toString());
    System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
    System.setProperty("spring.datasource.username", mysql.getUsername());
    System.setProperty("spring.datasource.password", mysql.getPassword());
  }

  WebTestClient client =
      WebTestClient.bindToController(new TestController()).build();

  @Test
  @DisplayName("주차 구역 정보를 성공적으로 조회한다.")
  void getParkingZoneInfo_Success() {
    // given

    System.out.println("success");
  }

}
