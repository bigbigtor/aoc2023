package foo.bar.aoc2023.solutions;

import lombok.Builder;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Component
public class Day5 implements Solution<Long> {

    private enum SeedParseMode { INDIVIDUAL, RANGE }
    private record Mapping(long srcIni, long dstIni, long count){}

    @Builder
    private record CategoryMap(String source, String destination, List<Mapping> mappings){}

    @Builder
    private record Almanac(Set<Long> seeds, List<CategoryMap> maps){}

    @Override
    public Long part1(Stream<String> input) {
        Almanac almanac = buildAlmanac(input, SeedParseMode.INDIVIDUAL);
        return almanac.seeds.stream()
                .map(seed -> getLocation(seed, almanac.maps))
                .min(Long::compareTo)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> input) {
        Almanac almanac = buildAlmanac(input, SeedParseMode.RANGE);
        return almanac.seeds.stream()
                .map(seed -> getLocation(seed, almanac.maps))
                .min(Long::compareTo)
                .orElse(0L);
    }

    private Long getLocation(Long seed, List<CategoryMap> maps) {
        String currentComponent = "seed";
        long currentValue = seed;
        while (!"location".equals(currentComponent)) {
            for (CategoryMap map : maps) {
                if (map.source.equals(currentComponent)) {
                    currentComponent = map.destination;
                    currentValue = getDestinationValue(currentValue, map);
                }
            }
        }
        return currentValue;
    }

    private Long getDestinationValue(Long sourceValue, CategoryMap map) {
        for (Mapping mapping : map.mappings) {
            var range = Range.of(mapping.srcIni, mapping.srcIni + mapping.count - 1);
            if (range.contains(sourceValue)) {
                return (sourceValue + (mapping.dstIni - mapping.srcIni));
            }
        }
        return sourceValue;
    }

    private Almanac buildAlmanac(Stream<String> lines, SeedParseMode seedParseMode) {
        Almanac.AlmanacBuilder almanacBuilder = Almanac.builder();
        String input = lines.collect(Collectors.joining("\n"));
        String[] lineArray = input.split("\n\n");
        almanacBuilder.seeds(buildSeeds(lineArray[0], seedParseMode));
        almanacBuilder.maps(
                Arrays.stream(lineArray, 1, lineArray.length)
                        .map(line -> line.split("\n"))
                        .map(this::buildCategoryMap)
                        .toList()
        );
        return almanacBuilder.build();
    }

    private CategoryMap buildCategoryMap(String[] lines) {
        Pattern mapCapturePattern = Pattern.compile("(\\w+)-to-(\\w+)");
        CategoryMap.CategoryMapBuilder mapBuilder = CategoryMap.builder();
        var matcher = mapCapturePattern.matcher(lines[0]);
        matcher.find();
        mapBuilder.source(matcher.group(1));
        mapBuilder.destination(matcher.group(2));
        var mappings = Arrays.stream(lines, 1, lines.length)
                .map(this::buildMapping)
                .toList();
        mapBuilder.mappings(mappings);
        return  mapBuilder.build();
    }

    private Mapping buildMapping(String line) {
        var split = line.split("\\s");
        long srcIni = Long.parseLong(split[1]);
        long dstIni = Long.parseLong(split[0]);
        long rngLen = Long.parseLong(split[2]);
        return new Mapping(srcIni, dstIni, rngLen);
    }

    private Set<Long> buildSeeds(String input, SeedParseMode seedParseMode) {
        String[] split = input.split("\\s");
        return switch (seedParseMode) {
            case INDIVIDUAL -> Arrays.stream(split, 1, split.length)
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            case RANGE -> {
                Set<Long> result = new HashSet<>();
                for (int i = 1; i < split.length; i += 2) {
                    long iniSeed = Long.parseLong(split[i]);
                    long count = Long.parseLong(split[i + 1]);
                    result.addAll(LongStream.range(iniSeed, iniSeed + count).boxed().collect(Collectors.toSet()));
                }
                yield result;
            }
        };
    }
}
