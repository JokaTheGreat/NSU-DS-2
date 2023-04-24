package ru.nlatysh.manager;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.nlatysh.common.models.CrackHashManagerRequest;
import ru.nlatysh.common.models.CrackHashWorkerResponse;
import ru.nlatysh.manager.models.CrackHash;
import ru.nlatysh.manager.models.CrackHashStatus;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// TODO: растащить функционал по файлам
// TODO: разобраться с докером
// TODO: разобраться с респонсами, ошибкам и тд
@SpringBootApplication
@RestController
public class Manager {
    private ConcurrentMap<String, CrackHashStatus> crackHashJobs;
    private CrackHashManagerRequest.Alphabet alphabet;
    private static final int workersCount = 1;

    public Manager() {
        crackHashJobs = new ConcurrentHashMap<>();

        alphabet = new CrackHashManagerRequest.Alphabet();
        List<String> alphabetList = Arrays.asList("a", "b", "c", "d",
                "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        alphabet.getSymbols().addAll(alphabetList);
    }

    @PostMapping(value = "/api/hash/crack", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crackHash(@RequestBody CrackHash crackHash) throws URISyntaxException {
        String requestId = UUID.randomUUID().toString();
        crackHashJobs.put(requestId, new CrackHashStatus());

        sendJobToWorker(crackHash, requestId);

        return ResponseEntity.ok("{\"requestId\":\"" + requestId + "\"}");
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<CrackHashStatus> GetCrackHashStatus(@RequestParam String requestId) {
        if (!crackHashJobs.containsKey(requestId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(crackHashJobs.get(requestId));
    }

    private void sendJobToWorker(CrackHash crackHash, String requestId) throws URISyntaxException {
        crackHashJobs.get(requestId).status = CrackHashStatus.Status.IN_PROGRESS;

        CrackHashManagerRequest crackHashRequest = new CrackHashManagerRequest();
        crackHashRequest.setRequestId(requestId);
        crackHashRequest.setHash(crackHash.hash);
        crackHashRequest.setMaxLength(crackHash.maxLength);
        crackHashRequest.setPartCount(workersCount);
        crackHashRequest.setPartNumber(workersCount);
        crackHashRequest.setAlphabet(alphabet);

        // TODO: поменять на webClient
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(
                new URI("http://localhost:8081/internal/api/worker/hash/crack/task"),
                HttpMethod.POST,
                new HttpEntity<>(crackHashRequest),
                ResponseEntity.class
        );
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public void patchCrackHashData(@RequestBody CrackHashWorkerResponse crackHashResponse) {
        var currentCrackHashJob = crackHashJobs.get(crackHashResponse.getRequestId());
        System.out.println(crackHashResponse);
        currentCrackHashJob.status = CrackHashStatus.Status.READY;
        currentCrackHashJob.data = crackHashResponse.getAnswers().getWords();
    }
}
