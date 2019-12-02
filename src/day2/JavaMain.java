/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding/CNM Ingenuity
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
package day2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final int NOUN = 12;
  private static final int VERB = 2;
  private static final int TARGET = 19_690_720;
  private static final int UPPER_BOUND = 100;
  private static final String FORMAT_1 = "Part 1: noun %d; verb = %d; resulting code[0] = %,d.%n";
  private static final String FORMAT_2 = "Part 2: target code[0] = %,d; %d * noun + verb = %,d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    int[] code = parse(path);
    System.out.printf(FORMAT_1, NOUN, VERB, process(code, NOUN, VERB));
    System.out.printf(FORMAT_2, TARGET, UPPER_BOUND, reverse(code, TARGET, UPPER_BOUND));
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

  private static int reverse(int[] code, int target, int upperBound) {
    for (int noun = 0; noun < upperBound; noun++) {
      for (int verb = 0; verb < upperBound; verb++) {
        if (process(code, noun, verb) == target) {
          return upperBound * noun + verb;
        }
      }
    }
    throw new IllegalArgumentException();
  }

}
