package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.models.CrackHashManagerRequest;
import org.example.models.CrackHashWorkerResponse;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.*;

@SpringBootApplication
@RestController
public class Worker {
    @PostMapping("/internal/api/worker/hash/crack/task")
    public void doCrackHash(@RequestBody CrackHashManagerRequest crackHashRequest) throws URISyntaxException {
        List<String> answers = crackHash(
                crackHashRequest.getHash(),
                crackHashRequest.getMaxLength(),
                crackHashRequest.getAlphabet().getSymbols().toArray(new String[0])
        );

        patchCrackHashManager(crackHashRequest.getRequestId(), crackHashRequest.getPartNumber(), answers);
    }

    private List<String> crackHash(String hash, int maxLength, String[] alphabet) {
        List<String> answer = new ArrayList<>();

        ICombinatoricsVector<String> alphabetVector = createVector(alphabet);
        for (int currentLength = 1; currentLength <= maxLength; currentLength++) {
            Generator<String> generator = createPermutationWithRepetitionGenerator(alphabetVector, currentLength);
            for (ICombinatoricsVector<String> currentVector : generator) {
                String possibleWord = String.join("", currentVector.getVector());
                String currentHash = DigestUtils.md5Hex(possibleWord);
                if (currentHash.equals(hash)) {
                    answer.add(possibleWord);
                }
            }
        }

        return answer;
    }

    private void patchCrackHashManager(String requestId, int partNumber, List<String> answers) throws URISyntaxException {
        CrackHashWorkerResponse crackHashResponse = new CrackHashWorkerResponse();
        crackHashResponse.setRequestId(requestId);
        crackHashResponse.setPartNumber(partNumber);
        CrackHashWorkerResponse.Answers answersObject = new CrackHashWorkerResponse.Answers();
        answersObject.getWords().addAll(answers);
        crackHashResponse.setAnswers(answersObject);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(
                new URI("http://localhost:8080/internal/api/manager/hash/crack/request"),
                HttpMethod.PATCH,
                new HttpEntity<>(crackHashResponse),
                ResponseEntity.class
        );
    }
}
