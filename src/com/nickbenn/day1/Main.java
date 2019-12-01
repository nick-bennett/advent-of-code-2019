package com.nickbenn.day1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class Main {

  private static final String INPUT_FILE = "day1/input.txt";
  private static final String OUTPUT_FORMAT = "Part %d: %,d%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(Main.class.getClassLoader().getResource(INPUT_FILE).toURI());
    System.out.printf(OUTPUT_FORMAT, 1, compute(path, Main::fuelNeeded));
    System.out.printf(OUTPUT_FORMAT, 2, compute(path, Main::totalFuelNeeded));
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
