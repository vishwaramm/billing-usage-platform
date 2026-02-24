package com.vishwaram.bilingusageplatform.api.controller;

import com.vishwaram.bilingusageplatform.api.dto.CreateUsageEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ImportTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsageEventIdempotencyIT {
    static final PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        pg.start();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pg::getJdbcUrl);
        registry.add("spring.datasource.username", pg::getUsername);
        registry.add("spring.datasource.password", pg::getPassword);
        registry.add("spring.jpahibernate.ddl-auto", () -> "validate");
    }

    @LocalServerPort int port;
    private final RestTemplate rest = new RestTemplate();

    @Test
    void concurrentRequests_sameIdempotencyKey_returnsSameId() throws Exception {
        String url = "http://localhost:" + port + "/v1/usage-events";
        UUID tenantId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID subscriptionId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        CreateUsageEventRequest body = new CreateUsageEventRequest(
                "evt_concurrent",
                subscriptionId,
                "api_calls",
                1,
                Instant.parse("2026-02-21T12:00:00Z")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-Id", tenantId.toString());
        HttpEntity<CreateUsageEventRequest> request = new HttpEntity<>(body, headers);

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        List<Callable<ResponseEntity<Map>>> calls = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            calls.add(() -> rest.exchange(url, HttpMethod.POST, request, Map.class));
        }

        List<Future<ResponseEntity<Map>>> results = pool.invokeAll(calls);
        pool.shutdown();

        Set<String> ids = new HashSet<>();
        for (Future<ResponseEntity<Map>> f : results) {
            ResponseEntity<Map> resp = f.get();
            assert(resp.getStatusCode() == HttpStatus.OK);
            Object id = resp.getBody().get("id");
            ids.add(String.valueOf(id));
        }

        assert(ids.size() == 1);
    }
}
