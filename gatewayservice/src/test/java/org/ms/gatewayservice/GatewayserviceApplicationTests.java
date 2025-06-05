package org.ms.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.config.discovery.enabled=false",
    "spring.cloud.config.fail-fast=false"
})
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
