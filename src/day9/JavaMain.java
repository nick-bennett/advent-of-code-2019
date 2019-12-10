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
package day9;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String FORMAT_1 = "Part 1: BOOST keycode = %d.%n";
  private static final String FORMAT_2 = "Part 2: Distress signal coordinates = %d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    long[] instructions = parse(path);
    System.out.printf(FORMAT_1, process(Arrays.copyOf(instructions, instructions.length + 1000),
        new LinkedList<Long>(List.of(1L))).get(0));
    System.out.printf(FORMAT_2, process(Arrays.copyOf(instructions, instructions.length + 1000),
        new LinkedList<Long>(List.of(2L))).get(0));
  }

  public static long[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(DELIMITER::splitAsStream)
          .mapToLong(Long::parseLong)
          .toArray();
    }
  }

  private static List<Long> process(long[] instructions, Deque<Long> input) {
    int ip = 0;
    int relativeBase = 0;
    List<Long> output = new LinkedList<>();
    while (true) {
      long operation = instructions[ip++];
      long[] operands;
      int quotient = (int) (operation / 100);
      switch ((int) (operation % 100)) {
        case 1:
          operands = consume(instructions, ip, relativeBase, 3, quotient, true);
          instructions[(int) operands[2]] = operands[0] + operands[1];
          ip += 3;
          break;
        case 2:
          operands = consume(instructions, ip, relativeBase, 3, quotient, true);
          instructions[(int) operands[2]] = operands[0] * operands[1];
          ip += 3;
          break;
        case 3:
          operands = consume(instructions, ip, relativeBase, 1, quotient, true);
          instructions[(int) operands[0]] = input.pop();
          ip += 1;
          break;
        case 4:
          operands = consume(instructions, ip, relativeBase, 1, quotient, false);
          output.add(operands[0]);
          ip += 1;
          break;
        case 5:
          operands = consume(instructions, ip, relativeBase, 2, quotient, false);
          if (operands[0] != 0) {
            ip = (int) operands[1];
          } else {
            ip += 2;
          }
          break;
        case 6:
          operands = consume(instructions, ip, relativeBase, 2, quotient, false);
          if (operands[0] == 0) {
            ip = (int) operands[1];
          } else {
            ip += 2;
          }
          break;
        case 7:
          operands = consume(instructions, ip, relativeBase, 3, quotient, true);
          instructions[(int) operands[2]] = (operands[0] < operands[1]) ? 1 : 0;
          ip += 3;
          break;
        case 8:
          operands = consume(instructions, ip, relativeBase, 3, quotient, true);
          instructions[(int) operands[2]] = (operands[0] == operands[1]) ? 1 : 0;
          ip += 3;
          break;
        case 9:
          operands = consume(instructions, ip, relativeBase, 1, quotient, false);
          relativeBase += operands[0];
          ip += 1;
          break;
        case 99:
          return output;
      }
    }
  }

  private static long[] consume(long[] instructions, int ip, int relativeBase,
      int length, int quotient, boolean store) {
    long[] operands =  Arrays.copyOfRange(instructions, ip, ip + length);
    for (int i = 0; i < length; i++) {
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
    return operands;
  }

}
