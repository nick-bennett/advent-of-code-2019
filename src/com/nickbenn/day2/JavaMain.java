package com.nickbenn.day2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class JavaMain {

  private static final String INPUT_FILE = "day2/input.txt";
  private static final String OUTPUT_FORMAT = "Part %d: %,d%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getClassLoader().getResource(INPUT_FILE).toURI());
    System.out.printf(OUTPUT_FORMAT, 1, compute(path));
    System.out.printf(OUTPUT_FORMAT, 2, compute(path));
  }

  private static int compute(Path path) {
    return 0;
  }

}
