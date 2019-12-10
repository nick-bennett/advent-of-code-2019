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
package day7;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import scala.Array;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final Pattern DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String FORMAT_1 = "Part 1: Highest signal possible w/ phases %s, w/o feedback loop = %d.%n";
  private static final String FORMAT_2 = "Part 1: Highest signal possible w/ phases %s, w/ feedback loop = %d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    int[] instructions = parse(path);
    Integer[] phases;
    phases = new Integer[]{0, 1, 2, 3, 4};
    System.out.printf(FORMAT_1, Arrays.toString(phases),
        getBestPermutationValue(instructions, phases, false));
    phases = new Integer[]{5, 6, 7, 8, 9};
    System.out.printf(FORMAT_2, Arrays.toString(phases),
        getBestPermutationValue(instructions, phases, true));
  }

  private static int[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .flatMap(DELIMITER::splitAsStream)
          .mapToInt(Integer::parseInt)
          .toArray();
    }
  }

  private static <T> List<T[]> permutations(T[] source) {
    List<T[]> work = new LinkedList<>();
    permutations(source, source.length, work);
    return work;
  }

  private static <T> void permutations(T[] source, int length, List<T[]> work) {
    T[] permutation = Arrays.copyOf(source, source.length);
    if (length == 1) {
      work.add(permutation);
      return;
    }
    for (int i = 0; i < length - 1; i++) {
      permutations(source, length - 1, work);
      T temp = source[length - 1];
      int target = (length % 2 == 0) ? i : 0;
      source[length - 1] = source[target];
      source[target] = temp;
    }
    permutations(source, length - 1, work);
  }

  private static int getBestPermutationValue(int[] instructions, Integer[] phases, boolean looped) {
    List<Integer[]> phasePermutations = permutations(phases);
    int best = Integer.MIN_VALUE;
    for (Integer[] permutation : phasePermutations) {
      int input = 0;
      Deque<Integer> inputBuffer = new LinkedList<>(List.of(input));
      Deque<Integer> outputBuffer;
      LinkedList<Amplifier> amplifiers = new LinkedList<>();
      for (int phase : permutation) {
        inputBuffer.push(phase);
        outputBuffer = (looped && phase == permutation[permutation.length - 1])
            ? amplifiers.get(0).getInput() : new LinkedList<>();
        amplifiers.add(new Amplifier(instructions, inputBuffer, outputBuffer));
        inputBuffer = outputBuffer;
      }
      while (amplifiers.stream().anyMatch(Predicate.not(Amplifier::isHalted))) {
        for (Amplifier amp : amplifiers) {
          amp.process();
        }
      }
      best = Math.max(amplifiers.peekLast().getOutput().peekLast(), best);
    }
    return best;
  }

  private static class Amplifier {

    private final int[] instructions;
    private final Deque<Integer> input;
    private final Deque<Integer> output;
    private int ip;
    private boolean halted;

    public Amplifier(int[] instructions, Deque<Integer> input, Deque<Integer> output) {
      this.instructions = Arrays.copyOf(instructions, instructions.length + 1000);
      this.input = input;
      this.output = output;
    }

    public void process() {
      if (!halted) {
        int operation = instructions[ip++];
        int[] operands;
        int quotient = operation / 100;
        switch (operation % 100) {
          case 1:
            operands = consume(3, quotient, true);
            instructions[operands[2]] = operands[0] + operands[1];
            break;
          case 2:
            operands = consume(3, quotient, true);
            instructions[operands[2]] = operands[0] * operands[1];
            break;
          case 3:
            if (!input.isEmpty()) {
              operands = consume(1, quotient, true);
              instructions[operands[0]] = input.removeFirst();
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
              ip = operands[1];
            }
            break;
          case 6:
            operands = consume(2, quotient, false);
            if (operands[0] == 0) {
              ip = operands[1];
            }
            break;
          case 7:
            operands = consume(3, quotient, true);
            instructions[operands[2]] = (operands[0] < operands[1]) ? 1 : 0;
            break;
          case 8:
            operands = consume(3, quotient, true);
            instructions[operands[2]] = (operands[0] == operands[1]) ? 1 : 0;
            break;
          case 99:
            halted = true;
            break;
        }
      }
    }

    public Deque<Integer> getInput() {
      return input;
    }

    public Deque<Integer> getOutput() {
      return output;
    }

    public boolean isHalted() {
      return halted;
    }

    private int[] consume(int length, int quotient, boolean store) {
      int[] operands = Arrays.copyOfRange(instructions, ip, ip + length);
      for (int i = 0; i < length - (store ? 1 : 0); i++) {
        if (quotient % 10 == 0) {
          operands[i] = instructions[operands[i]];
        }
        quotient /= 10;
      }
      ip += length;
      return operands;
    }

  }

}
