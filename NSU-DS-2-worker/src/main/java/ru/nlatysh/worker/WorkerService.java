package ru.nlatysh.worker;

import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nlatysh.common.models.CrackHashManagerRequest;
import ru.nlatysh.common.models.CrackHashWorkerResponse;

import java.util.ArrayList;
import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

@Service
public class WorkerService {
    public ResponseEntity<Void> doCrackHash(CrackHashManagerRequest crackHashRequest) {
        List<String> answers = crackHash(
                crackHashRequest.getHash(),
                crackHashRequest.getMaxLength(),
                crackHashRequest.getAlphabet().getSymbols().toArray(new String[0])
        );

        patchCrackHashManager(crackHashRequest.getRequestId(), crackHashRequest.getPartNumber(), answers);

        return ResponseEntity.ok().build();
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

    private void patchCrackHashManager(String requestId, int partNumber, List<String> answers) {
        CrackHashWorkerResponse crackHashResponse = new CrackHashWorkerResponse();
        CrackHashWorkerResponse.Answers answersObject = new CrackHashWorkerResponse.Answers();
        answersObject.getWords().addAll(answers);

        crackHashResponse.setRequestId(requestId);
        crackHashResponse.setPartNumber(partNumber);
        crackHashResponse.setAnswers(answersObject);

        WebClient.create("http://manager:8080/internal/api/manager/hash/crack/request")
                .method(HttpMethod.PATCH)
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(crackHashResponse)
                .retrieve()
                .bodyToMono(ResponseEntity.class)
                .toFuture();
    }
}
