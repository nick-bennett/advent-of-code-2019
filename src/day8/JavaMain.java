package day8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final int WIDTH = 25;
  private static final int HEIGHT = 6;
  private static final String FORMAT_1 =
      "Part 1: (count of 1s) * (count of 2s) in layer with fewest 0s = %d.%n";
  private static final String FORMAT_2 = "Part 2: Decoded image:";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    char[] digits = parse(path);
    char[][] matrix = structure(digits, HEIGHT, WIDTH);
    System.out.printf(FORMAT_1, count(matrix));
    char[][] image = decode(matrix, HEIGHT, WIDTH);
    System.out.println(FORMAT_2);
    render(image);
  }

  private static char[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .map(String::trim)
          .filter(Predicate.not(String::isEmpty))
          .collect(Collectors.joining())
          .toCharArray();
    }
  }

  private static char[][] structure(char[] digits, int rows, int columns) {
    int depth = digits.length / columns / rows;
    char[][] matrix = new char[depth][rows * columns];
    for (int layer = 0; layer < depth; layer++) {
      System.arraycopy(digits, layer * (columns * rows), matrix[layer], 0, columns * rows);
    }
    return matrix;
  }

  private static long count(char[][] matrix) {
    return Arrays.stream(matrix)
        .map(layer ->
          new String(layer).codePoints()
              .map(val -> val - '0')
              .boxed()
              .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        )
        .min(Comparator.comparingLong(m -> m.get(0)))
        .map(m -> m.get(1) * m.get(2))
        .orElse(-1L);
  }

  private static char[][] decode(char[][] matrix, int rows, int columns) {
    char[][] image = new char[rows][];
    char[] decoded = new char[rows * columns];
    Arrays.fill(decoded, '2');
    for (int layer = 0; layer < matrix.length; layer++) {
      for (int i = 0; i < matrix[layer].length; i++) {
        if (decoded[i] == '2') {
          decoded[i] = matrix[layer][i];
        }
      }
    }
    for (int row = 0; row < rows; row++) {
      image[row] = Arrays.copyOfRange(decoded, row * columns, (row + 1) * columns);
    }
    return image;
  }

  private static void render(char[][] image) {
    Arrays.stream(image)
        .map(row -> new String(row).replace('0', ' ').replace('1', '\u2022'))
        .forEach(System.out::println);
  }

}
