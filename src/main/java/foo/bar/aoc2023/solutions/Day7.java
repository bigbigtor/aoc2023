package foo.bar.aoc2023.solutions;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Component
public class Day7 implements Solution<Long> {

    private record Hand(Character[] cards, int bid){}

    private enum CardComparisonMode {POKER, JOKER}
    private enum HandType {FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD}

    private static final String CARD_STRENGTH_POKER = "AKQJT98765432";
    private static final String CARD_STRENGTH_JOKER = "AKQT98765432J";

    private final Comparator<Hand> pokerhandTypeComparator = Comparator.comparing(o -> Day7.getHandType(o, CardComparisonMode.POKER));
    private final Comparator<Hand> jokerhandTypeComparator = Comparator.comparing(o -> Day7.getHandType(o, CardComparisonMode.JOKER));

    private final Comparator<Hand> pokerCardComparator = (o1, o2) -> compare(o1, o2, CardComparisonMode.POKER);
    private final Comparator<Hand> jokerCardComparator = (o1, o2) -> compare(o1, o2, CardComparisonMode.JOKER);

    private final Comparator<Hand> pokerComparator = pokerhandTypeComparator.thenComparing(pokerCardComparator);
    private final Comparator<Hand> jokerComparator = jokerhandTypeComparator.thenComparing(jokerCardComparator);

    @Override
    public Long part1(Stream<String> input) {
        var sortedHands = input.map(this::parseHand)
                .sorted(pokerComparator)
                .toList();
        return LongStream.range(0, sortedHands.size())
                .map(i -> sortedHands.get((int) i).bid * (sortedHands.size() - i))
                .reduce(Long::sum)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> input) {
        var sortedHands = input.map(this::parseHand)
                .sorted(jokerComparator)
                .toList();
        return LongStream.range(0, sortedHands.size())
                .map(i -> sortedHands.get((int) i).bid * (sortedHands.size() - i))
                .reduce(Long::sum)
                .orElse(0L);
    }

    private Hand parseHand(String input) {
        var split = input.split(" ");
        var cardChars = split[0].toCharArray();
        var cards = IntStream.range(0, cardChars.length).mapToObj(i -> cardChars[i]).toArray(Character[]::new);
        var bid = Integer.parseInt(split[1]);
        return new Hand(cards, bid);
    }

    private static int compare(Hand o1, Hand o2, CardComparisonMode mode) {
        var strength = CardComparisonMode.POKER.equals(mode) ? CARD_STRENGTH_POKER : CARD_STRENGTH_JOKER;
        return IntStream.range(0, o1.cards.length)
                .map(i -> Integer.compare(strength.indexOf(o1.cards[i]), strength.indexOf(o2.cards[i])))
                .filter(comp -> comp != 0)
                .findFirst()
                .orElse(0);
    }

    private static HandType getHandType(Hand hand, CardComparisonMode mode) {
        var groups = getCardCounts(hand, mode)
                .values()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if (groups.containsKey(5L)) {
            return HandType.FIVE_OF_A_KIND;
        } else if (groups.containsKey(4L)) {
            return HandType.FOUR_OF_A_KIND;
        } else if (groups.keySet().containsAll(Set.of(3L, 2L))) {
            return HandType.FULL_HOUSE;
        } else if (groups.containsKey(3L)) {
            return HandType.THREE_OF_A_KIND;
        } else if (groups.containsKey(2L) && groups.get(2L) == 2L) {
            return HandType.TWO_PAIR;
        } else if (groups.containsKey(2L)) {
            return HandType.ONE_PAIR;
        } else {
            return HandType.HIGH_CARD;
        }
    }

    private static Map<Character, Long> getCardCounts(Hand hand, CardComparisonMode mode) {
        var cardCounts = Arrays.stream(hand.cards)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return switch (mode) {
            case POKER -> cardCounts;
            case JOKER -> getStrongestCardCount(cardCounts);
        };
    }

    private static Map<Character, Long> getStrongestCardCount(Map<Character, Long> cardCounts) {
        if (cardCounts.containsKey('J')) {
            var jokerCounts = new HashMap<>(cardCounts);
            char highestCountCard = jokerCounts.entrySet()
                    .stream()
                    .filter(e -> e.getKey() != 'J')
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse('J');
            long highestCount = jokerCounts.get(highestCountCard);
            if (highestCountCard != 'J') {
                jokerCounts.replace(highestCountCard, highestCount + jokerCounts.remove('J'));
            }
            return jokerCounts;
        } else {
            return cardCounts;
        }
    }
}
