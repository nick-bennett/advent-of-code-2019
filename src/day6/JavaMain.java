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
package day6;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final String DELIMITER = "\\)";
  private static final String ROOT = "COM";
  private static final String START = "YOU";
  private static final String FINISH = "SAN";
  private static final String FORMAT_1 = "Part 1: Total direct & indirect orbits = %d.%n";
  private static final String FORMAT_2 =
      "Part 2: Orbital transfers required from %s to %s = %d.%n";

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    Map<String, String> links = parse(path);
    System.out.printf(FORMAT_1, totalPathLength(links, ROOT));
    System.out.printf(FORMAT_2, START, FINISH, familyDistance(links, ROOT, START, FINISH));
  }

  public static Map<String, String> parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .map(s -> s.split(DELIMITER))
          .collect(Collectors.toMap((a) -> a[1], (a) -> a[0]));
    }
  }

  public static int totalPathLength(Map<String, String> links, String root) {
    int total = 0;
    for (String key : links.keySet()) {
      total += generationDistance(links, root, key);
    }
    return total;
  }

  private static int generationDistance(Map<String, String> links,
      String ancestor, String descendant) {
    int steps = 1;
    for (String parent = links.get(descendant); !parent.equals(ROOT); parent = links.get(parent)) {
      steps++;
    }
    return steps;
  }

  private static int familyDistance(Map<String, String> links,
      String root, String relative1, String relative2) {
    int generation1 = generationDistance(links, root, relative1);
    int generation2 = generationDistance(links, root, relative2);
    String ancestor1 = ancestor(links, relative1, Math.max(generation1 - generation2, 0));
    String ancestor2 = ancestor(links, relative2, Math.max(generation2 - generation1, 0));
    int distance = Math.abs(generation1 - generation2);
    while (!ancestor1.equals(ancestor2)) {
      distance += 2;
      ancestor1 = links.get(ancestor1);
      ancestor2 = links.get(ancestor2);
    }
    return distance - 2;
  }

  private static String ancestor(Map<String, String> links, String descendant, int generations) {
    String ancestor = descendant;
    for (int i = generations; i > 0; i--) {
      ancestor = links.get(ancestor);
    }
    return ancestor;
  }

}
