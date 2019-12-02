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
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final int NOUN = 12;
  private static final int VERB = 2;
  private static final int TARGET = 19690720;
  private static final int UPPER_BOUND = 100;
  private static final String FORMAT_1 = "Part 1: noun %d; verb = %d; resulting code[0] = %,d.%n";
  private static final String FORMAT_2 = "Part 2: target code[0] = %,d; 100 * noun + verb = %d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getClassLoader().getResource(INPUT_FILE).toURI());
    int[] code = parse(path);
    System.out.printf(FORMAT_1, NOUN, VERB, process(code, NOUN, VERB));
    System.out.printf(FORMAT_2, TARGET, reverse(code, TARGET));
  }

  public static int[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(DELIMITER::splitAsStream)
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
      int opcode = work[position];
      int operand1 = work[position + 1];
      int operand2 = work[position + 2];
      int dest = work[position + 3];
      switch (opcode) {
        case 1:
          work[dest] = work[operand1] + work[operand2];
          break;
        case 2:
          work[dest] = work[operand1] * work[operand2];
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
    for (int noun = 0; noun < UPPER_BOUND; noun++) {
      for (int verb = 0; verb < UPPER_BOUND; verb++) {
        if (process(code, noun, verb) == target) {
          return UPPER_BOUND * noun + verb;
        }
      }
    }
    throw new IllegalArgumentException();
  }

}
