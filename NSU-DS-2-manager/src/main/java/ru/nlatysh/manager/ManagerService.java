package ru.nlatysh.manager;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.nlatysh.common.models.CrackHashManagerRequest;
import ru.nlatysh.common.models.CrackHashWorkerResponse;
import ru.nlatysh.manager.models.CrackHash;
import ru.nlatysh.manager.models.CrackHashStatus;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ManagerService {
    private final int TIMEOUT = 30;
    private final ConcurrentMap<String, CrackHashStatus> crackHashJobs;
    private final CrackHashManagerRequest.Alphabet alphabet;
    private static final int workersCount = 1;

    public ManagerService() {
        crackHashJobs = new ConcurrentHashMap<>();

        alphabet = new CrackHashManagerRequest.Alphabet();
        List<String> alphabetList = Arrays.asList("abcdefghijklmnopqrstuvwxyz0123456789".split(""));
        alphabet.getSymbols().addAll(alphabetList);
    }

    public ResponseEntity<String> crackHash(CrackHash crackHash) {
        if (crackHash.maxLength < 1) {
            return ResponseEntity.badRequest().build();
        }

        String requestId = UUID.randomUUID().toString();
        crackHashJobs.put(requestId, new CrackHashStatus());

        sendJobToWorker(crackHash, requestId);

        return ResponseEntity.ok("{\"requestId\":\"" + requestId + "\"}");
    }

    public ResponseEntity<CrackHashStatus> GetCrackHashStatus(String requestId) {
        if (!crackHashJobs.containsKey(requestId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(crackHashJobs.get(requestId));
    }

    public ResponseEntity<Void> patchCrackHashData(CrackHashWorkerResponse crackHashResponse) {
        var currentCrackHashJob = crackHashJobs.get(crackHashResponse.getRequestId());
        if (currentCrackHashJob.status == CrackHashStatus.Status.ERROR) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

        currentCrackHashJob.status = CrackHashStatus.Status.READY;
        currentCrackHashJob.data = crackHashResponse.getAnswers().getWords();
        return ResponseEntity.ok().build();
    }

    private void sendJobToWorker(CrackHash crackHash, String requestId) {
        crackHashJobs.get(requestId).status = CrackHashStatus.Status.IN_PROGRESS;

        CrackHashManagerRequest crackHashRequest = new CrackHashManagerRequest();
        crackHashRequest.setRequestId(requestId);
        crackHashRequest.setHash(crackHash.hash);
        crackHashRequest.setMaxLength(crackHash.maxLength);
        crackHashRequest.setPartCount(workersCount);
        crackHashRequest.setPartNumber(workersCount);
        crackHashRequest.setAlphabet(alphabet);

        WebClient.create("http://worker:8080/internal/api/worker/hash/crack/task")
                .method(HttpMethod.POST)
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(crackHashRequest)
                .retrieve()
                .bodyToMono(ResponseEntity.class)
                .timeout(Duration.ofSeconds(TIMEOUT))
                .onErrorResume(error -> {
                    crackHashJobs.get(requestId).status = CrackHashStatus.Status.ERROR;
                    return Mono.just(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
                })
                .toFuture();
    }
}
