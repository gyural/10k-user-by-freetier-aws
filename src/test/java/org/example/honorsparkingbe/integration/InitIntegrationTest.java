package org.example.honorsparkingbe.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:config/application-integration-test.yml")
public class InitIntegrationTest {

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

  @AfterAll
  static void tearDown() {
    System.clearProperty("spring.data.redis.host");
    System.clearProperty("spring.data.redis.port");
    System.clearProperty("spring.datasource.url");
    System.clearProperty("spring.datasource.username");
    System.clearProperty("spring.datasource.password");
  }

}
