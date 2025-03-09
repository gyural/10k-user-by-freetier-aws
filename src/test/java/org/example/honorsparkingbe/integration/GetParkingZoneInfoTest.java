package org.example.honorsparkingbe.integration;

import com.redis.testcontainers.RedisContainer;
import org.example.honorsparkingbe.config.RedisConfig;
import org.example.honorsparkingbe.config.SecurityConfig;
import org.example.honorsparkingbe.unit.controller.ControllerAdviceTest.TestController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextHierarchy({
    @ContextConfiguration(classes = SecurityConfig.class),
    @ContextConfiguration(classes = RedisConfig.class)
})
@Testcontainers
public class GetParkingZoneInfoTest {

  private static final String MYSQL_IMAGE = "mysql:latest";

  @Container
  static public RedisContainer redis = new RedisContainer()
      .waitingFor(Wait.forListeningPort())
      .waitingFor(Wait.defaultWaitStrategy());

  @Container
  static public MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
      .waitingFor(Wait.forListeningPort());


  @BeforeAll
  static void setUp() {
    System.setProperty("spring.data.redis.host", "123");
    System.setProperty("spring.data.redis.port", redis.getExposedPorts().get(0).toString());
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
