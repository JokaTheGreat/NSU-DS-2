package org.example;

import org.example.Models.CrackHash;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Manager {
    @PostMapping("/api/hash/crack")
    public CrackHash crackHash(@RequestBody CrackHash crackHash) {
        return crackHash;
    }
}
