package org.example;

import org.example.Models.CrackHash;
import org.example.Models.CrackHashStatus;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class Manager {
    private Map<String, CrackHashStatus> crackHashJobs;

    public Manager() {
        crackHashJobs = new HashMap<>();
    }
    @PostMapping(value = "/api/hash/crack", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crackHash(@RequestBody CrackHash crackHash) {
        String requestId = UUID.randomUUID().toString();
        crackHashJobs.put(requestId, new CrackHashStatus());

        return ResponseEntity.ok("{\"requestId\":\"" + requestId + "\"}");
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<CrackHashStatus> GetCrackHashStatus(@RequestParam String requestId) {
        if (!crackHashJobs.containsKey(requestId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(crackHashJobs.get(requestId));
    }
}
