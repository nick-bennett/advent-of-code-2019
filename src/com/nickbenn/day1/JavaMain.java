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
package com.nickbenn.day1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "day1/input.txt";
  private static final String OUTPUT_FORMAT = "Part %d: %,d%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getClassLoader().getResource(INPUT_FILE).toURI());
    System.out.printf(OUTPUT_FORMAT, 1, compute(path, JavaMain::fuelNeeded));
    System.out.printf(OUTPUT_FORMAT, 2, compute(path, JavaMain::totalFuelNeeded));
  }

  private static int compute(Path path, IntUnaryOperator mapper) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .mapToInt(Integer::parseInt)
          .map(mapper)
          .sum();
    }
  }

  private static int fuelNeeded(int mass) {
    return Math.max(0, mass / 3 - 2);
  }

  private static int totalFuelNeeded(int mass) {
    if (mass <= 0) {
      return 0;
    }
    int fuel = fuelNeeded(mass);
    return fuel + totalFuelNeeded(fuel);
  }

}
