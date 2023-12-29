package foo.bar.aoc2023.solutions;

import lombok.Builder;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Day5 implements Solution<Long> {

    private enum SeedParseMode { INDIVIDUAL, RANGE }
    private record Mapping(Range<Long> srcRange, Long mappingOffset){}

    @Builder
    private record CategoryMap(String source, String destination, List<Mapping> mappings){}

    @Builder
    private record Almanac(List<Range<Long>> seeds, List<CategoryMap> maps){}

    @Override
    public Long part1(Stream<String> input) {
        Almanac almanac = buildAlmanac(input, SeedParseMode.INDIVIDUAL);
        return almanac.seeds.stream()
                .flatMap(seeds -> getLocationRanges(seeds, almanac.maps).stream())
                .map(Range::getMinimum)
                .min(Long::compareTo)
                .orElse(0L);
    }

    @Override
    public Long part2(Stream<String> input) {
        Almanac almanac = buildAlmanac(input, SeedParseMode.RANGE);
        return almanac.seeds.stream()
                .flatMap(seeds -> getLocationRanges(seeds, almanac.maps).stream())
                .map(Range::getMinimum)
                .min(Long::compareTo)
                .orElse(0L);
    }

    private List<Range<Long>> getLocationRanges(Range<Long> seeds, List<CategoryMap> maps) {
        String currentComponent = "seed";
        List<Range<Long>> currentRanges = List.of(seeds);
        while (!"location".equals(currentComponent)) {
            for (CategoryMap map : maps) {
                if (map.source.equals(currentComponent)) {
                    currentComponent = map.destination;
                    currentRanges = getDestinationRanges(currentRanges, map);
                }
            }
        }
        return currentRanges;
    }

    private List<Range<Long>> getDestinationRanges(List<Range<Long>> sourceRanges, CategoryMap map) {
        List<Range<Long>> result = new ArrayList<>();
        Deque<Range<Long>> pending = new ArrayDeque<>(sourceRanges);
        while (!pending.isEmpty()) {
            var inputRange = pending.pop();
            if (!hasMapping(inputRange, map.mappings)) {
                result.add(inputRange);
            } else {
                for (Mapping mapping : map.mappings) {
                    Range<Long> mappingSrc = mapping.srcRange;
                    if (!mappingSrc.isOverlappedBy(inputRange)) {
                        continue;
                    } else if (mappingSrc.containsRange(inputRange)) {
                        long dstMin = inputRange.getMinimum() + mapping.mappingOffset;
                        long dstMax = inputRange.getMaximum() + mapping.mappingOffset;
                        result.add(Range.of(dstMin, dstMax));
                    } else if (inputRange.containsRange(mappingSrc)) {
                        long dstMin = mappingSrc.getMinimum() + mapping.mappingOffset;
                        long dstMax = mappingSrc.getMaximum() + mapping.mappingOffset;
                        result.add(Range.of(dstMin, dstMax));
                        pending.push(Range.of(inputRange.getMinimum(), mappingSrc.getMinimum() - 1));
                        pending.push(Range.of(mappingSrc.getMaximum() + 1, inputRange.getMaximum()));
                    } else if (inputRange.getMinimum() < mappingSrc.getMinimum()) {
                        long dstMin = mappingSrc.getMinimum() + mapping.mappingOffset;
                        long dstMax = inputRange.getMaximum() + mapping.mappingOffset;
                        result.add(Range.of(dstMin, dstMax));
                        pending.push(Range.of(inputRange.getMinimum(), mappingSrc.getMinimum() - 1));
                    } else {
                        long dstMin = inputRange.getMinimum() + mapping.mappingOffset;
                        long dstMax = mappingSrc.getMaximum() + mapping.mappingOffset;
                        result.add(Range.of(dstMin, dstMax));
                        pending.push(Range.of(mappingSrc.getMaximum() + 1, inputRange.getMaximum()));
                    }
                }
            }
        }
        return result;
    }

    private boolean hasMapping(Range<Long> input, List<Mapping> mappings) {
        return mappings.stream().map(Mapping::srcRange).anyMatch(r -> r.isOverlappedBy(input));
    }

    private Almanac buildAlmanac(Stream<String> lines, SeedParseMode seedParseMode) {
        Almanac.AlmanacBuilder almanacBuilder = Almanac.builder();
        String input = lines.collect(Collectors.joining("\n"));
        String[] lineArray = input.split("\n\n");
        almanacBuilder.seeds(buildSeedRanges(lineArray[0], seedParseMode));
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
        Range<Long> srcRange = Range.of(srcIni, srcIni + rngLen - 1);
        return new Mapping(srcRange, dstIni - srcIni);
    }

    private List<Range<Long>> buildSeedRanges(String input, SeedParseMode seedParseMode) {
        String[] split = input.split("\\s");
        return switch (seedParseMode) {
            case INDIVIDUAL -> Arrays.stream(split, 1, split.length)
                    .map(Long::parseLong)
                    .map(seed -> Range.of(seed, seed))
                    .toList();
            case RANGE -> {
                List<Range<Long>> result = new ArrayList<>();
                for (int i = 1; i < split.length; i += 2) {
                    long iniSeed = Long.parseLong(split[i]);
                    long count = Long.parseLong(split[i + 1]);
                    result.add(Range.of(iniSeed, iniSeed + count + 1));
                }
                yield result;
            }
        };
    }
}
