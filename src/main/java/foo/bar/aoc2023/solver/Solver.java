package foo.bar.aoc2023.solver;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import foo.bar.aoc2023.solutions.Solution;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@Log
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Solver {

    private final List<Solution<?>> solutions;

    @PostConstruct
    public void init() {
        solve();
    }

    private void solve() {
        solutions.forEach(sol -> {
            String solutionClassName = sol.getClass().getSimpleName();
            log.info("%n-----%s solutions:-----%nPart 1: [%s]%nPart 2: [%s]".formatted(
                    solutionClassName,
                    sol.part1(getInput(solutionClassName)),
                    sol.part2(getInput(solutionClassName))
            ));
        });
    }

    @SneakyThrows
    private Stream<String> getInput(String className) {
        return new ClassPathResource("%s.txt".formatted(className.toLowerCase()))
                .getContentAsString(StandardCharsets.UTF_8)
                .lines();
    }
}
