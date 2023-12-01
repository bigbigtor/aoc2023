package foo.bar.aoc2023.solutions;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AOCTest<T> {

    protected final Solution<T> solution;

    protected final T solution1;

    protected final T solution2;

    @Test
    void testPart1() {
        assertEquals(solution1, solution.part1(getInput(1)));
    }

    @Test
    void testPart2() {
        assertEquals(solution2, solution.part2(getInput(2)));
    }

    @SneakyThrows
    private Stream<String> getInput(int part) {
        String className = solution.getClass().getSimpleName();
        return new ClassPathResource(getResourceName(className, part))
                .getContentAsString(StandardCharsets.UTF_8)
                .lines();
    }

    private String getResourceName(String className, int part) {
        return "%s-%s.txt".formatted(className.toLowerCase(), part);
    }
}
