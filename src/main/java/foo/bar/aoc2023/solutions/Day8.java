package foo.bar.aoc2023.solutions;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class Day8 implements Solution<Long> {

    @Override
    public Long part1(Stream<String> input) {
        var lines = input.toList();
        var directions = lines.get(0);
        var map = parseMap(lines.subList(2, lines.size()));
        long steps = 0;
        var currentLocation = "AAA";
        while(!Objects.equals(currentLocation, "ZZZ")) {
            Function<Pair<String, String>, String> nextDir = directions.charAt((int) steps % directions.length()) == 'L' ? Pair::getLeft : Pair::getRight;
            currentLocation = nextDir.apply(map.get(currentLocation));
            steps++;
        }
        return steps;
    }

    @Override
    public Long part2(Stream<String> input) {
        var lines = input.toList();
        var directions = lines.get(0);
        var map = parseMap(lines.subList(2, lines.size()));
        var locations = map.keySet().stream().filter(k -> k.endsWith("A")).toList();
        List<Long> steps = new ArrayList<>();
        for (String currentLocation: locations) {
            long currentSteps = 0;
            while(!currentLocation.endsWith("Z")) {
                Function<Pair<String, String>, String> nextDir = directions.charAt((int) currentSteps % directions.length()) == 'L' ? Pair::getLeft : Pair::getRight;
                currentLocation = nextDir.apply(map.get(currentLocation));
                currentSteps++;
            }
            steps.add(currentSteps);
        }
        return findLCM(steps);
    }

    private Map<String, Pair<String, String>> parseMap(List<String> input) {
        Map<String, Pair<String, String>> result = new HashMap<>();
        Pattern mapCapturePattern = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
        input.stream()
                .map(mapCapturePattern::matcher)
                .forEach(matcher -> {
                    matcher.find();
                    result.put(matcher.group(1), Pair.of(matcher.group(2), matcher.group(3)));
                });
        return result;
    }

    private static long findLCM(long a, long b) {
        return (a * b) / findGCD(a, b);
    }

    private static long findGCD(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static long findLCM(List<Long> numbers) {
        long lcmResult = numbers.get(0);

        for (int i = 1; i < numbers.size(); i++) {
            lcmResult = findLCM(lcmResult, numbers.get(i));
        }

        return lcmResult;
    }
}
