package foo.bar.aoc2023.solutions;


import java.util.stream.Stream;

public interface Solution<T> {

    T part1(Stream<String> input);

    T part2(Stream<String> input);
}
