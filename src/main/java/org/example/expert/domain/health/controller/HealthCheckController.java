package org.example.expert.domain.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> getHealthCheck(){
        String response = "Spring Server가 성공적으로 동작했습니다.";
        return ResponseEntity.ok(response);
    }
}
