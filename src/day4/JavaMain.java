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
package day4;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JavaMain {

  private static final int MIN_VALUE = 193651;
  private static final int MAX_VALUE = 649729;
  private static final String HEADER =
      "Potential code values between %d and %d, with non-descending digits, and with ...%n";
  private static final String FORMAT_1 =
      "(Part 1) ... at least 1 run of at least 2 repeating digits = %d.%n";
  private static final String FORMAT_2 =
      "(Part 2) ... at least 1 run of exactly 2 repeating digits = %d.%n";

  public static void main(String[] args) {
    System.out.printf(HEADER, MIN_VALUE, MAX_VALUE);
    List<Integer> candidates = generate(0, MIN_VALUE, MAX_VALUE);
    System.out.printf(FORMAT_1, filterAndCount(candidates, (entry) -> entry.getValue().size() >= 2));
    System.out.printf(FORMAT_2, filterAndCount(candidates, (entry) -> entry.getValue().size() == 2));
  }

  private static long filterAndCount(List<Integer> rawCandidates,
      Predicate<Map.Entry<Integer, List<Integer>>> predicate) {
    return rawCandidates.parallelStream()
        .filter(value ->
            value.toString().codePoints()
                .boxed()
                .collect(Collectors.groupingBy(d -> d))
                .entrySet()
                .stream()
                .anyMatch(predicate)
        )
        .count();
  }

  private static List<Integer> generate(int seed, int minimum, int maximum) {
    List<Integer> candidates = new LinkedList<>();
    int lastDigit = (seed > 0) ? seed % 10 : 1;
    int newSeed = seed * 10;
    if (newSeed <= maximum) {
      for (int i = newSeed + lastDigit; i < newSeed + 10; i++) {
        if (i <= maximum) {
          if (i >= minimum) {
            candidates.add(i);
          }
          candidates.addAll(generate(i, minimum, maximum));
        }
      }
    }
    return candidates;
  }

}
