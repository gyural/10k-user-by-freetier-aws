package org.example.honorsparkingbe.unit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;


@DataJpaTest
@TestPropertySource(locations = "classpath:config/application-test.yml")
public class ParkingZoneRepositoryTest {



    @Test
    void testPropertyLoad() {
        System.out.println("DB URL from test properties: ");
    }
}
