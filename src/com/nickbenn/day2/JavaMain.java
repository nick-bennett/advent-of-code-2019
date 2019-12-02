package com.nickbenn.day2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "day2/input.txt";
  private static final String PART1_FORMAT = "Part 1: noun %d; verb = %d; code[0] = %d.%n";
  private static final String PART2_FORMAT = "Part 2: target code[0] = %d; 100 * noun + verb = %d.%n";
  private static final Pattern delimiter = Pattern.compile("\\s*,\\s*");

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getClassLoader().getResource(INPUT_FILE).toURI());
    int[] code = parse(path);
    int noun = 12;
    int verb = 2;
    System.out.printf(PART1_FORMAT, noun, verb, process(code, noun, verb));
    int target = 19690720;
    System.out.printf(PART2_FORMAT, target, reverse(code, target));
  }

  public static int[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(delimiter::splitAsStream)
          .mapToInt(Integer::parseInt)
          .toArray();
    }
  }

  private static int process(int[] code, int noun, int verb) {
    int[] work = Arrays.copyOf(code, code.length);
    work[1] = noun;
    work[2] = verb;
    loop:
    for (int position = 0; position < work.length; position += 4) {
      int operator = work[position];
      int operand1 = work[position + 1];
      int operand2 = work[position + 2];
      int operand3 = work[position + 3];
      switch (operator) {
        case 1:
          work[operand3] = work[operand1] + work[operand2];
          break;
        case 2:
          work[operand3] = work[operand1] * work[operand2];
          break;
        case 99:
          break loop;
        default:
          throw new IllegalArgumentException();
      }
    }
    return work[0];
  }

  private static int reverse(int[] code, int target) {
    for (int noun = 0; noun < 100; noun++) {
      for (int verb = 0; verb < 100; verb++) {
        if (process(code, noun, verb) == target) {
          return 100 * noun + verb;
        }
      }
    }
    throw new IllegalArgumentException();
  }

}
