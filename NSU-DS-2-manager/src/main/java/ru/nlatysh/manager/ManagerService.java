package ru.nlatysh.manager;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nlatysh.common.models.CrackHashManagerRequest;
import ru.nlatysh.common.models.CrackHashWorkerResponse;
import ru.nlatysh.manager.models.CrackHash;
import ru.nlatysh.manager.models.CrackHashStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ManagerService {
    private final ConcurrentMap<String, CrackHashStatus> crackHashJobs;
    private final CrackHashManagerRequest.Alphabet alphabet;
    private static final int workersCount = 1;

    public ManagerService() {
        crackHashJobs = new ConcurrentHashMap<>();

        alphabet = new CrackHashManagerRequest.Alphabet();
        List<String> alphabetList = Arrays.asList("a", "b", "c", "d",
                "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        alphabet.getSymbols().addAll(alphabetList);
    }

    public ResponseEntity<String> crackHash(CrackHash crackHash) {
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

        WebClient.create("http://localhost:8081/internal/api/worker/hash/crack/task")
                .method(HttpMethod.POST)
                .bodyValue(crackHashRequest)
                .retrieve()
                .bodyToMono(ResponseEntity.class)
                .toFuture();
    }
}
