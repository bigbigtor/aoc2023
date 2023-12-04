package foo.bar.aoc2023.solutions;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class Day2 implements Solution<Integer> {

    @Builder
    private record Color(int r, int g, int b){}

    private static final Color max = new Color(12, 13, 14);

    @Override
    public Integer part1(Stream<String> input) {
        Color[][] cubeSets = input.map(this::parseSets).toArray(Color[][]::new);
        IntPredicate redCheck = i -> getComponentMax(cubeSets[i], Color::r) <= max.r;
        IntPredicate greenCheck = i -> getComponentMax(cubeSets[i], Color::g) <= max.g;
        IntPredicate blueCheck = i -> getComponentMax(cubeSets[i], Color::b) <= max.b;
        return IntStream.range(0, cubeSets.length)
                .filter(redCheck.and(greenCheck).and(blueCheck))
                .map(i -> i+1)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public Integer part2(Stream<String> input) {
        return input.map(this::parseSets)
                .map(cubeSet -> getComponentMax(cubeSet, Color::r)
                                * getComponentMax(cubeSet, Color::g)
                                * getComponentMax(cubeSet, Color::b))
                .reduce(Integer::sum)
                .orElse(0);
    }

    private int getComponentMax(Color[] cubeSet, Function<Color, Integer> colorExtractor) {
        return Arrays.stream(cubeSet).map(colorExtractor).max(Integer::compare).orElse(0);
    }

    private Color[] parseSets(String line) {
        return Arrays.stream(line.split(": ")[1].split("; "))
                .map(this::parseSet)
                .toArray(Color[]::new);
    }

    private Color parseSet(String components) {
        Color.ColorBuilder builder = Color.builder();
        String[] colorComponents = components.split(", ");
        for (String component: colorComponents) {
            String[] qtyToComponent = component.split(" ");
            int qty = Integer.parseInt(qtyToComponent[0]);
            switch (qtyToComponent[1]) {
                case "red" -> builder.r(qty);
                case "green" -> builder.g(qty);
                case "blue" -> builder.b(qty);
                default -> throw new IllegalStateException("Unexpected value: " + qtyToComponent[1]);
            }
        }
        return builder.build();
    }
}
