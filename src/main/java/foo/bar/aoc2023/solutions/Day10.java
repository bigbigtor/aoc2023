package foo.bar.aoc2023.solutions;

import org.springframework.stereotype.Component;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class Day10 implements Solution<Integer> {

    private enum Tile { VERTICAL_PIPE, HORIZONTAL_PIPE, NE_BEND, NW_BEND, SW_BEND, SE_BEND, GROUND, STARTING_POS }

    private record Position(int x, int y){}

    private enum Direction { N, S, W, E }

    @Override
    public Integer part1(Stream<String> input) {
        var area = parseArea(input);
        var visitedPos = getVisitedPositions(area);
        return visitedPos.size() / 2;
    }

    @Override
    public Integer part2(Stream<String> input) {
        var area = parseArea(input);
        var visitedPos = getVisitedPositions(area);
        var enclosedTiles = getEnclosedTiles(area, visitedPos);
        return enclosedTiles.size();
    }

    private Set<Position> getEnclosedTiles(Tile[][] area, List<Position> visitedPos) {
        Set<Position> enclosedTiles = new HashSet<>();
        Polygon loop = new Polygon();
        visitedPos.forEach(pos -> loop.addPoint(pos.x, pos.y));
        Set<Position> visitedPosSet = new HashSet<>(visitedPos);
        for (int i = 0; i < area[0].length; i++) {
            for (int j = 0; j < area.length; j++) {
                Position pos = new Position(i, j);
                if (loop.contains(i, j) && !visitedPosSet.contains(pos)) {
                    enclosedTiles.add(pos);
                }
            }
        }
        return enclosedTiles;
    }

    private List<Position> getVisitedPositions(Tile[][] area) {
        List<Position> visited = new ArrayList<>();
        var startingPos = getStartingPosition(area);
        var currentPos = startingPos;
        var direction = getNextDirection(area, currentPos, null);
        do {
            visited.add(currentPos);
            direction = getNextDirection(area, currentPos, direction);
            currentPos = getNextPosition(currentPos, direction);
        } while (!currentPos.equals(startingPos));
        return visited;
    }

    private Position getNextPosition(Position currentPos, Direction dir) {
        return switch (dir) {
            case N -> new Position(currentPos.x, currentPos.y - 1);
            case S -> new Position(currentPos.x, currentPos.y + 1);
            case W -> new Position(currentPos.x - 1, currentPos.y);
            case E -> new Position(currentPos.x + 1, currentPos.y);
        };
    }

    private Direction getNextDirection(Tile[][] area, Position currentPos, Direction previousDir) {
        return switch (area[currentPos.y][currentPos.x]) {
            case VERTICAL_PIPE -> previousDir == Direction.N ? Direction.N : Direction.S;
            case HORIZONTAL_PIPE -> previousDir == Direction.W ? Direction.W : Direction.E;
            case NE_BEND -> previousDir == Direction.S ? Direction.E : Direction.N;
            case NW_BEND -> previousDir == Direction.S ? Direction.W : Direction.N;
            case SW_BEND -> previousDir == Direction.N ? Direction.W : Direction.S;
            case SE_BEND -> previousDir == Direction.N ? Direction.E : Direction.S;
            case GROUND -> throw new IllegalStateException("Unexpected value: " + area[currentPos.y][currentPos.x]);
            case STARTING_POS -> Arrays.stream(Direction.values())
                    .filter(d -> {
                        var nextPos = getNextPosition(currentPos, d);
                        var nextTile = area[nextPos.y][nextPos.x];
                        return switch (nextTile) {
                            case VERTICAL_PIPE -> Set.of(Direction.N, Direction.S).contains(d);
                            case HORIZONTAL_PIPE -> Set.of(Direction.W, Direction.E).contains(d);
                            case NE_BEND -> Set.of(Direction.S, Direction.W).contains(d);
                            case NW_BEND -> Set.of(Direction.S, Direction.E).contains(d);
                            case SW_BEND -> Set.of(Direction.N, Direction.E).contains(d);
                            case SE_BEND -> Set.of(Direction.N, Direction.W).contains(d);
                            default -> false;
                        };
                    })
                    .findFirst()
                    .orElse(Direction.N);
        };
    }

    private Position getStartingPosition(Tile[][] area) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < area[0].length; i++) {
            for (int j = 0; j < area.length; j++) {
                if (area[j][i] == Tile.STARTING_POS) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }
        return new Position(x, y);
    }

    private Tile[][] parseArea(Stream<String> input) {
        return input.map(this::parseRow).toArray(Tile[][]::new);
    }

    private Tile[] parseRow(String row) {
        return Arrays.stream(row.split("")).map(this::parseTile).toArray(Tile[]::new);
    }

    private Tile parseTile(String tile) {
        return switch (tile) {
            case "|" -> Tile.VERTICAL_PIPE;
            case "-" -> Tile.HORIZONTAL_PIPE;
            case "L" -> Tile.NE_BEND;
            case "J" -> Tile.NW_BEND;
            case "7" -> Tile.SW_BEND;
            case "F" -> Tile.SE_BEND;
            case "." -> Tile.GROUND;
            case "S" -> Tile.STARTING_POS;
            default -> throw new IllegalStateException("Unexpected value: " + tile);
        };
    }
}
