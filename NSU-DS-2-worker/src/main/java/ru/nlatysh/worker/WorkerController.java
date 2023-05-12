package ru.nlatysh.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nlatysh.common.models.CrackHashManagerRequest;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
public class WorkerController {
    @Autowired
    WorkerService workerService;
    @PostMapping(value = "/internal/api/worker/hash/crack/task", consumes = APPLICATION_XML_VALUE)
    public ResponseEntity<Void> doCrackHash(@RequestBody CrackHashManagerRequest crackHashRequest) {
        return workerService.doCrackHash(crackHashRequest);
    }
}
