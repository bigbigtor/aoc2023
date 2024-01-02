package foo.bar.aoc2023.solutions;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Component
public class Day6 implements Solution<Long> {

    private record Race(long time, long distance){}

    @Override
    public Long part1(Stream<String> input) {
        return parseSeparateRaces(input)
                .map(this::getNumWinningWays)
                .reduce((a, b) -> a * b)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> input) {
        return getNumWinningWays(parseOneRace(input));
    }

    private long getNumWinningWays(Race race) {
        long winFirst = LongStream.rangeClosed(0, race.time)
                .dropWhile(wait -> losesRace(wait, race))
                .findFirst()
                .getAsLong();
        long winLast = LongStream.rangeClosed(0, race.time)
                .map(reminder -> race.time - reminder)
                .dropWhile(wait -> losesRace(wait, race))
                .findFirst()
                .getAsLong();
        return winLast - winFirst + 1;
    }

    private boolean losesRace(long wait, Race race) {
        return ((race.time - wait) * wait) <= race.distance;
    }

    private Stream<Race> parseSeparateRaces(Stream<String> input) {
        var inputs = input.map(line -> line.split(":")[1].trim())
                .map(digits -> digits.split("\\s+"))
                .map(digits -> Arrays.stream(digits).map(Long::parseLong).toArray(Long[]::new))
                .toList();
        var times = inputs.get(0);
        var distances = inputs.get(1);
        return IntStream.range(0, times.length)
                .boxed()
                .map(i -> new Race(times[i], distances[i]));
    }

    private Race parseOneRace(Stream<String> input) {
        var lines = input.toList();
        return new Race(parseLineAsLong(lines.get(0)), parseLineAsLong(lines.get(1)));
    }

    private Long parseLineAsLong(String input) {
        var digits = input.split(":")[1];
        return Long.parseLong(digits.replace(" ", ""));
    }
}
