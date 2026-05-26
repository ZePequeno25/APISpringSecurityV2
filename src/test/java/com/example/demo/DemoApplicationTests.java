package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")   // ← Isso é o mais importante
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }
}