package foo.bar.aoc2023.solutions;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Day1 implements Solution<Long> {

    @Override
    public Long part1(Stream<String> calibrationDocument) {
        return calibrationDocument.map(line -> getCalibrationValue(line, getDigitMap()))
                .reduce(Long::sum)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> calibrationDocument) {
        return calibrationDocument.map(line -> getCalibrationValue(line, getCombinedMap()))
                .reduce(Long::sum)
                .orElse(0L);
    }

    private long getCalibrationValue(String line, Map<String, Integer> tokenMap) {
        var firstDigit = tokenMap.entrySet()
                .stream()
                .filter(entry -> line.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> line.indexOf(entry.getKey())
                ))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(tokenMap::get)
                .orElse(null);
        var lastDigit = tokenMap.entrySet()
                .stream()
                .filter(entry -> line.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> line.lastIndexOf(entry.getKey())
                )).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(tokenMap::get)
                .orElse(null);
        return Long.parseLong("%s%s".formatted(firstDigit, lastDigit));
    }

    private Map<String, Integer> getCombinedMap() {
        return Stream.concat(getDigitMap().entrySet().stream(), getSpelledMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Integer> getSpelledMap() {
        return Map.of(
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4,
                "five", 5,
                "six", 6,
                "seven", 7,
                "eight", 8,
                "nine", 9
        );
    }

    private Map<String, Integer> getDigitMap() {
        return Map.of(
                "1", 1,
                "2", 2,
                "3", 3,
                "4", 4,
                "5", 5,
                "6", 6,
                "7", 7,
                "8", 8,
                "9", 9,
                "0", 0
        );
    }
}
