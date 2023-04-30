package ru.nlatysh.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.nlatysh.common.models.CrackHashWorkerResponse;
import ru.nlatysh.manager.models.CrackHash;
import ru.nlatysh.manager.models.CrackHashStatus;

@RestController
public class ManagerController {
    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    
    @PostMapping(value = "/api/hash/crack", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crackHash(@RequestBody CrackHash crackHash) {
        return managerService.crackHash(crackHash);
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<CrackHashStatus> getCrackHashStatus(@RequestParam String requestId) {
        return managerService.GetCrackHashStatus(requestId);
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public ResponseEntity<Void> patchCrackHashData(@RequestBody CrackHashWorkerResponse crackHashResponse) {
        return managerService.patchCrackHashData(crackHashResponse);
    }
}
