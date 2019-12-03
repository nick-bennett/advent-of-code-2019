/*
 *  Copyright 2019 Nicholas Bennett
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package day3;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String CELL_FORMAT = "%1$s(row=%2$d, col=%3$d)";
  private static final String LEG_FORMAT = "%s %d";
  private static final String FORMAT_1 =
      "Part 1: Minimum Manhattan distance from origin to intersection is %,d.%n";
  private static final String FORMAT_2 =
      "Part 2: Minimum combined travel distance from origin tointersection is %,d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    List<Wire> wires = parse(path);
    System.out.printf(FORMAT_1, process(wires, Cell::getManhattan));
    System.out.printf(FORMAT_2, process(wires, Cell::getTravel));
  }

  public static List<Wire> parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .map(Wire::parse)
          .collect(Collectors.toList());
    }
  }

  private static int process(List<Wire> wires, ToIntFunction<Cell> function) {
    Map<Cell, Cell> traces = new HashMap<>();
    Cell best = null;
    for (Wire wire : wires) {
      int row = 0;
      int column = 0;
      int travel = 0;
      for (Leg leg : wire.getLegs()) {
        Direction direction = leg.getDirection();
        int length = leg.getLength();
        int rowOffset = direction.getRowOffset();
        int columnOffset = direction.getColumnOffset();
        for (int step = 0; step < length; step++) {
          row += rowOffset;
          column += columnOffset;
          Cell cell = new Cell(wire, row, column, ++travel);
          Cell previous = traces.get(cell);
          if (previous == null) {
            traces.put(cell, cell);
          } else if (previous.getWire() != wire) {
            Cell augmented = new Cell(wire, row, column, travel + previous.getTravel());
            if (best == null || function.applyAsInt(best) > function.applyAsInt(augmented)) {
              best = augmented;
            }
          }
        }
      }
    }
    return function.applyAsInt(best);
  }

  private enum Direction {

    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    private final int rowOffset;
    private final int columnOffset;

    Direction(int rowOffset, int columnOffset) {
      this.rowOffset = rowOffset;
      this.columnOffset = columnOffset;
    }

    public static Direction fromCode(char code) {
      switch (code) {
        case 'U':
          return UP;
        case 'R':
          return RIGHT;
        case 'D':
          return DOWN;
        case 'L':
          return LEFT;
      }
      return null;
    }

    public int getRowOffset() {
      return rowOffset;
    }

    public int getColumnOffset() {
      return columnOffset;
    }

  }

  private static final class Leg {

    private final Direction direction;
    private final int length;
    private final String str;

    private Leg(Direction direction, int length) {
      this.direction = direction;
      this.length = length;
      str = String.format(LEG_FORMAT, direction, length);
    }

    public static Leg parse(String input) {
      Direction direction = Direction.fromCode(input.charAt(0));
      int length = Integer.parseInt(input.substring(1));
      return new Leg(direction, length);
    }

    public Direction getDirection() {
      return direction;
    }

    public int getLength() {
      return length;
    }

    @Override
    public String toString() {
      return str;
    }

  }

  private static final class Wire {

    private final List<Leg> legs;

    private Wire(List<Leg> legs) {
      this.legs = Collections.unmodifiableList(legs);
    }

    public static Wire parse(String input) {
      return new Wire(DELIMITER.splitAsStream(input)
          .map(Leg::parse)
          .collect(Collectors.toList()));
    }

    public List<Leg> getLegs() {
      return legs;
    }

  }

  private static final class Cell {

    private final Wire wire;
    private final int row;
    private final int column;
    private final int travel;
    private final int manhattan;
    private final int hash;
    private final String str;

    public Cell(Wire wire, int row, int column, int travel) {
      this.wire = wire;
      this.row = row;
      this.column = column;
      this.travel = travel;
      this.manhattan = Math.abs(row) + Math.abs(column);
      hash = Objects.hash(row, column);
      str = String.format(CELL_FORMAT, getClass().getSimpleName(), row, column);
    }

    public Wire getWire() {
      return wire;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return column;
    }

    public int getTravel() {
      return travel;
    }

    public int getManhattan() {
      return manhattan;
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == this
          || (obj instanceof Cell
          && ((Cell) obj).row == row
          && ((Cell) obj).column == column);
    }

    @Override
    public String toString() {
      return str;
    }

  }

}
