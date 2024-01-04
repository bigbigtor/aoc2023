package foo.bar.aoc2023.solutions;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class Day9 implements Solution<Long> {

    @Override
    public Long part1(Stream<String> input) {
        return input.map(this::parseHistory)
                .map(this::getFirstAndLastValuesOfSequence)
                .map(Pair::getRight)
                .map(this::getNextValueInSequence)
                .reduce(Long::sum)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> input) {
        return input.map(this::parseHistory)
                .map(this::getFirstAndLastValuesOfSequence)
                .map(Pair::getLeft)
                .map(this::getPreviousValueInSequence)
                .reduce(Long::sum)
                .orElse(0L);
    }

    private long getPreviousValueInSequence(Deque<Integer> firstValues) {
        long result = 0;
        while (!firstValues.isEmpty()) {
            result = firstValues.pop() - result;
        }
        return result;
    }

    private long getNextValueInSequence(Deque<Integer> lastValues) {
        long result = 0;
        while(!lastValues.isEmpty()) {
            result += lastValues.pop();
        }
        return result;
    }

    private Pair<Deque<Integer>, Deque<Integer>> getFirstAndLastValuesOfSequence(List<Integer> sequence) {
        Deque<Integer> firstValues = new ArrayDeque<>();
        Deque<Integer> lastValues = new ArrayDeque<>();
        var currentSequence = sequence;
        while (!currentSequence.stream().allMatch(i -> i == 0)) {
            firstValues.push(currentSequence.get(0));
            lastValues.push(currentSequence.get(currentSequence.size() - 1));
            List<Integer> finalCurrentSequence = currentSequence;
            currentSequence = IntStream.range(0, currentSequence.size() - 1)
                    .map(i -> finalCurrentSequence.get(i + 1) - finalCurrentSequence.get(i))
                    .boxed()
                    .toList();
        }
        return Pair.of(firstValues, lastValues);
    }

    private List<Integer> parseHistory(String input) {
        return Arrays.stream(input.split(" ")).map(Integer::parseInt).toList();
    }
}
