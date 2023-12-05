package foo.bar.aoc2023.solutions;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Component
public class Day3 implements Solution<Integer> {

    @Builder
    private record Range(int a, int b){}

    @Override
    public Integer part1(Stream<String> input) {
        List<String> lines = input.toList();
        char[] chars = String.join("", lines).toCharArray();
        int lineLength = chars.length / lines.size();
        return getNumberRanges(chars, lineLength).stream()
                .filter(r -> isPartNumber(r, chars, lineLength))
                .map(r -> getPartNumber(r, chars))
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public Integer part2(Stream<String> input) {
        List<String> lines = input.toList();
        char[] chars = String.join("", lines).toCharArray();
        int lineLength = chars.length / lines.size();
        List<Range> numberRanges = getNumberRanges(chars, lineLength);
        return getSumOfGearRatios(numberRanges, chars, lineLength);
    }

    private List<Range> getNumberRanges(char[] chars, int lineLength) {
        List<Range> result = new ArrayList<>();
        boolean inRange = false;
        Range.RangeBuilder builder = Range.builder();
        for (int i = 0; i < chars.length; i++) {
            if (!inRange && Character.isDigit(chars[i])) {
                builder.a(i);
                inRange = true;
            }
            if (inRange && (!Character.isDigit(chars[i+1]) || (i+1) % lineLength == 0)) {
                builder.b(i);
                result.add(builder.build());
                inRange = false;
            }
        }
        return result;
    }

    private boolean isPartNumber(Range range, char[] chars, int lineLength) {
        return getAdjacentParts(range, chars, lineLength).anyMatch(pos -> !Character.isDigit(chars[pos]) && (chars[pos] != '.'));
    }

    private int getSumOfGearRatios(List<Range> numberRanges, char[] chars, int lineLength) {
        Map<Integer, List<Range>> gearToRangeMap = new HashMap<>();
        for(Range range: numberRanges) {
            getAdjacentParts(range, chars, lineLength)
                    .filter(pos -> chars[pos] == '*')
                    .forEach(gear -> gearToRangeMap.computeIfAbsent(gear, ArrayList::new).add(range));
        }
        return gearToRangeMap.values()
                .stream()
                .filter(ranges -> ranges.size() == 2)
                .map(ranges -> getPartNumber(ranges.get(0), chars) * getPartNumber(ranges.get(1), chars))
                .reduce(Integer::sum)
                .orElse(0);
    }

    private Stream<Integer> getAdjacentParts(Range range, char[] chars, int lineLength) {
        boolean onLeftEdge = (range.a % lineLength) == 0;
        boolean onRightEdge = (range.b % lineLength) == (lineLength - 1);
        boolean onTopEdge = range.a < lineLength;
        boolean onBottomEdge = range.a > (chars.length - lineLength);
        int maxLeft =  onLeftEdge ? range.a : range.a - 1;
        int maxRight = onRightEdge ? range.b : range.b + 1;
        IntStream allPositions = IntStream.empty();
        if (!onLeftEdge) allPositions = IntStream.concat(allPositions, IntStream.of(maxLeft));
        if (!onRightEdge) allPositions = IntStream.concat(allPositions, IntStream.of(maxRight));
        if (!onTopEdge) allPositions = IntStream.concat(allPositions, IntStream.rangeClosed(maxLeft - lineLength, maxRight - lineLength));
        if (!onBottomEdge) allPositions = IntStream.concat(allPositions, IntStream.rangeClosed(maxLeft + lineLength, maxRight + lineLength));
        return allPositions.boxed();
    }

    private int getPartNumber(Range range, char[] chars) {
        char[] partNumberChars = Arrays.copyOfRange(chars, range.a, range.b + 1);
        String partNumber = String.valueOf(partNumberChars);
        return Integer.parseInt(partNumber);
    }
}
