package ru.nlatysh.worker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nlatysh.common.models.CrackHashManagerRequest;

@RestController
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<Void> doCrackHash(@RequestBody CrackHashManagerRequest crackHashRequest) {
        return workerService.doCrackHash(crackHashRequest);
    }
}
