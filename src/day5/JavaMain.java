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
package day5;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String OUTPUT_FORMAT = "Part %d: For input = %d, diagnostic output = %d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    int[] instructions = parse(path);
    System.out.printf(
        OUTPUT_FORMAT, 1, 1, process(Arrays.copyOf(instructions, instructions.length), 1));
    System.out.printf(
        OUTPUT_FORMAT, 2, 5, process(Arrays.copyOf(instructions, instructions.length), 5));
  }

  public static int[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(DELIMITER::splitAsStream)
          .mapToInt(Integer::parseInt)
          .toArray();
    }
  }

  private static int process(int[] instructions, int input) {
    int result = 0;
    int ip = 0;
    while (true) {
      int operation = instructions[ip++];
      int opcode = operation % 100;
      operation = operation / 100;
      int[] modes;
      int[] operands;
      switch (opcode) {
        case 1:
          operands = consume(instructions, ip, 3);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          instructions[operands[2]] = operands[0] + operands[1];
          ip += 3;
          break;
        case 2:
          operands = consume(instructions, ip, 3);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          instructions[operands[2]] = operands[0] * operands[1];
          ip += 3;
          break;
        case 3:
          operands = consume(instructions, ip, 1);
          instructions[operands[0]] = input;
          ip += 1;
          break;
        case 4:
          operands = consume(instructions, ip, 1);
          modes = modes(operation, 1);
          preprocess(operands, modes,instructions);
          if (result != 0) {
            throw new IllegalArgumentException();
          } else {
            result = operands[0];
          }
          ip += 1;
          break;
        case 5:
          operands = consume(instructions, ip, 2);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          if (operands[0] != 0) {
            ip = operands[1];
          } else {
            ip += 2;
          }
          break;
        case 6:
          operands = consume(instructions, ip, 2);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          if (operands[0] == 0) {
            ip = operands[1];
          } else {
            ip += 2;
          }
          break;
        case 7:
          operands = consume(instructions, ip, 3);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          instructions[operands[2]] = (operands[0] < operands[1]) ? 1 : 0;
          ip += 3;
          break;
        case 8:
          operands = consume(instructions, ip, 3);
          modes = modes(operation, 2);
          preprocess(operands, modes,instructions);
          instructions[operands[2]] = (operands[0] == operands[1]) ? 1 : 0;
          ip += 3;
          break;
        case 99:
          return result;
      }
    }
  }

  private static int[] modes(int operation, int length) {
    int[] modes = new int[length];
    for (int i = 0; i < modes.length; i++) {
      modes[i] = operation % 10;
      operation /= 10;
    }
    return modes;
  }

  private static int[] consume(int[] instructions, int ip, int length) {
    return Arrays.copyOfRange(instructions, ip, ip + length);
  }

  private static void preprocess(int[] operands, int[] modes, int[] instructions) {
    for (int i = 0; i < modes.length; i++) {
      if (modes[i] == 0) {
        operands[i] = instructions[operands[i]];
      }
    }
  }

}
