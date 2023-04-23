package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.ArrayList;
import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.*;

public class Worker {
    private static String hash = "bd1d7b0809e4b4ee9ca307aa5308ea6f";
    private static String[] alphabet = {"a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
            "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static int maxLength = 4;
    private static int partNumber = 1;
    private static int partCount = 1;

    public static void main(String[] args) {
        List<String> answer = new ArrayList<>();
        ICombinatoricsVector<String> alphabetVector = createVector(alphabet);
        for (int currentLength = 1; currentLength <= maxLength; currentLength++) {
            Generator<String> generator = createPermutationWithRepetitionGenerator(alphabetVector, currentLength);
            for (ICombinatoricsVector<String> currentVector : generator) {
                String possibleWord = String.join("", currentVector.getVector());
                String currentHash = DigestUtils.md5Hex(possibleWord);
                if (currentHash.equals(hash)) {
                    System.out.println(possibleWord);
                    answer.add(possibleWord);
                }
            }
        }
    }
}
