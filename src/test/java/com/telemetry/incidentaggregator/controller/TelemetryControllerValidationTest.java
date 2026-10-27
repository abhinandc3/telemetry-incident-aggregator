package com.telemetry.incidentaggregator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rejectsInvalidTelemetryRequest() throws Exception {
        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "service": " ",
                                  "timestamp": "2026-10-26T16:30:00Z",
                                  "cpu": 82.5,
                                  "memory": 71.2,
                                  "latencyMs": 340
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.service").exists());
    }
}
