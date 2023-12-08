package foo.bar.aoc2023.solutions;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class Day4 implements Solution<Integer> {

    @Override
    public Integer part1(Stream<String> input) {
        return input.map(this::parseCardNumbers)
                .map(this::getMatches)
                .filter(size -> size > 0)
                .map(size -> 1 << (size - 1))
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public Integer part2(Stream<String> input) {
        Integer[] matchesByCard = input.map(this::parseCardNumbers)
                .map(this::getMatches)
                .toArray(Integer[]::new);
        Deque<Integer> cardsToProcess = IntStream.range(0, matchesByCard.length)
                .boxed()
                .collect(Collectors.toCollection(ArrayDeque::new));
        var numCards = new int[matchesByCard.length];
        while (!cardsToProcess.isEmpty()) {
            int i = cardsToProcess.pop();
            numCards[i]++;
            IntStream.rangeClosed(i + 1, i + matchesByCard[i]).forEach(cardsToProcess::push);
        }
        return Arrays.stream(numCards).sum();
    }

    private Integer getMatches(Triple<Integer, Set<Integer>, Set<Integer>> card) {
        card.getMiddle().retainAll(card.getRight());
        return card.getMiddle().size();
    }

    private Triple<Integer, Set<Integer>, Set<Integer>> parseCardNumbers(String card) {
        var cardAndLists = card.split(":\\s+");
        var cardNumber = Integer.parseInt(cardAndLists[0].split("\\s+")[1]);
        var lists = cardAndLists[1].split("\\s+\\|\\s+");
        return Triple.of(cardNumber, parseList(lists[0]), parseList(lists[1]));
    }

    private Set<Integer> parseList(String list) {
        return Arrays.stream(list.split("\\s+")).map(Integer::parseInt).collect(Collectors.toSet());
    }
}
