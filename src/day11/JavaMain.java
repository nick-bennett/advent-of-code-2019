package day11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    long[] instructions = parse(path);
    Map<Patch, Integer> paintedPatches = paint(instructions, 0);
    System.out.println(paintedPatches.size());
    paintedPatches = paint(instructions, 1);
    List<Patch> whitePatches = paintedPatches.entrySet().stream()
        .filter(entry -> entry.getValue() == 1)
        .map(Entry::getKey)
        .sorted(Comparator.comparing(Patch::getRow))
        .collect(Collectors.toList());
    int minRow = whitePatches.get(0).getRow();
    int maxRow = whitePatches.get(whitePatches.size() - 1).getRow();
    whitePatches.sort(Comparator.comparing(Patch::getColumn));
    int minCol = whitePatches.get(0).getRow();
    int maxCol = whitePatches.get(whitePatches.size() - 1).getRow();
    boolean[][] canvas = new boolean[maxRow - minRow + 1][maxCol - minCol + 1];
    whitePatches.forEach(p -> canvas[p.getRow() - minRow][p.getColumn() - minCol] = true);
    for (boolean[] row : canvas) {
      for (boolean pixel : row) {
        System.out.print(pixel ? '\u2588' : ' ');
      }
      System.out.println();
    }
  }

  private static long[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(DELIMITER::splitAsStream)
          .mapToLong(Long::parseLong)
          .toArray();
    }
  }

  private static Map<Patch, Integer> paint(long[] instructions, int startColor) {
    Map<Patch, Integer> painted = new HashMap<>();
    Direction direction = Direction.NORTH;
    Patch current = new Patch(0, 0);
    Deque<Long> input = new LinkedList<>(List.of((long) startColor));
    Deque<Long> output = new LinkedList<>();
    Intcode computer = new Intcode(instructions, input, output);
    while (true) {
      while (!computer.isHalted() && output.isEmpty()) {
        computer.process();
      }
      if (computer.isHalted()) {
        break;
      }
      int color = output.removeFirst().intValue();
      painted.put(current, color);
      while (!computer.isHalted() && output.isEmpty()) {
        computer.process();
      }
      if (computer.isHalted()) {
        break;
      }
      switch (output.removeFirst().intValue()) {
        case 0:
          direction = direction.turnLeft();
          break;
        case 1:
          direction = direction.turnRight();
          break;
      }
      current = current.move(direction);
      color = (int) painted.getOrDefault(current, 0).longValue();
      input.addLast((long) color);
    }
    return painted;
  }

  private static class Patch {

    private static final String FORMAT = "%s(row: %d, col: %d)";

    private final int row;
    private final int column;
    private int hash;

    private Patch(int row, int column) {
      this.row = row;
      this.column = column;
      hash = Objects.hash(row, column);
    }

    public Patch move(Direction direction) {
      return new Patch(row + direction.getRowOffset(), column + direction.getColumnOffset());
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return column;
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == this
          || (obj instanceof Patch
              && ((Patch) obj).row == row
              && ((Patch) obj).column == column);
    }

    @Override
    public String toString() {
      return String.format(FORMAT, getClass().getSimpleName(), getRow(), getColumn());
    }
  }

  private enum Direction {

    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    private final int rowOffset;
    private final int columnOffset;

    Direction(int rowOffset, int columnOffset) {
      this.rowOffset = rowOffset;
      this.columnOffset = columnOffset;
    }

    public int getRowOffset() {
      return rowOffset;
    }

    public int getColumnOffset() {
      return columnOffset;
    }

    public Direction turnRight() {
      Direction[] values = Direction.values();
      return values[(ordinal() + 1) % values.length];
    }

    public Direction turnLeft() {
      Direction[] values = Direction.values();
      return values[(ordinal() - 1 + values.length) % values.length];
    }

  }

  private static class Intcode {

    private final long[] instructions;
    private final Deque<Long> input;
    private final Deque<Long> output;
    private int ip;
    private int relativeBase;
    private boolean halted;

    public Intcode(long[] instructions, Deque<Long> input, Deque<Long> output) {
      this.instructions = Arrays.copyOf(instructions, instructions.length + 1000);
      this.input = input;
      this.output = output;
    }

    public void process() {
      if (!halted) {
        long operation = instructions[ip++];
        long[] operands;
        int quotient = (int) (operation / 100);
        switch ((int) (operation % 100)) {
          case 1:
            operands = consume(3, quotient, true);
            instructions[(int) operands[2]] = operands[0] + operands[1];
            break;
          case 2:
            operands = consume(3, quotient, true);
            instructions[(int) operands[2]] = operands[0] * operands[1];
            break;
          case 3:
            if (!input.isEmpty()) {
              operands = consume(1, quotient, true);
              instructions[(int) operands[0]] = input.removeFirst();
            } else {
              ip--;
            }
            break;
          case 4:
            operands = consume(1, quotient, false);
            output.addLast(operands[0]);
            break;
          case 5:
            operands = consume(2, quotient, false);
            if (operands[0] != 0) {
              ip = (int) operands[1];
            }
            break;
          case 6:
            operands = consume(2, quotient, false);
            if (operands[0] == 0) {
              ip = (int) operands[1];
            }
            break;
          case 7:
            operands = consume(3, quotient, true);
            instructions[(int) operands[2]] = (operands[0] < operands[1]) ? 1 : 0;
            break;
          case 8:
            operands = consume(3, quotient, true);
            instructions[(int) operands[2]] = (operands[0] == operands[1]) ? 1 : 0;
            break;
          case 9:
            operands = consume(1, quotient, false);
            relativeBase += operands[0];
            break;
          case 99:
            halted = true;
            break;
        }
      }
    }

    public Deque<Long> getInput() {
      return input;
    }

    public Deque<Long> getOutput() {
      return output;
    }

    public boolean isHalted() {
      return halted;
    }

    private long[] consume(int length, int quotient, boolean store) {
      long[] operands = Arrays.copyOfRange(instructions, ip, ip + length);
      for (int i = 0; i < length - (store ? 1 : 0); i++) {
        int mode = quotient % 10;
        if (mode == 0 && (i < length - 1 || !store)) {
          operands[i] = instructions[(int) operands[i]];
        } else if (mode == 2) {
          if (i < length - 1 || !store) {
            operands[i] = instructions[(int) (operands[i] + relativeBase)];
          } else {
            operands[i] += relativeBase;
          }
        }
        quotient /= 10;
      }
      ip += length;
      return operands;
    }

  }

}
