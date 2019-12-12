package day12;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final int STEPS = 1000;

  public static void main(String[] args) throws URISyntaxException, IOException {
    Path path = Path.of(JavaMain.class.getResource(INPUT_FILE).toURI());
    Body[] bodies = parse(path);
    NBodySystem system = new NBodySystem(bodies);
    system.advance(STEPS);
    System.out.println(system.getEnergy());
    System.out.println(system.getPeriod());
  }

  private static Body[] parse(Path path) throws IOException {
    try (Stream<String> stream = Files.lines(path)) {
      return stream
          .map((line) -> new Body(Vector.parse(line)))
          .toArray(Body[]::new);
    }
  }

  private static class NBodySystem {

    private final Body[] bodies;

    public NBodySystem(Body[] bodies) {
      this.bodies = bodies;
    }

    private void advance(int steps) {
      for (int i = 0; i < steps; i++) {
        for (int j = 0; j < bodies.length - 1; j++) {
          for (int k = j + 1; k < bodies.length; k++) {
            bodies[j].attract(bodies[k]);
          }
          bodies[j].move();
        }
        bodies[bodies.length - 1].move();
      }
    }

    public long getEnergy() {
      return Arrays.stream(bodies)
          .mapToLong(body -> body.getKineticEnergy() * body.getPotentialEnergy())
          .sum();
    }

    public long getPeriod() {
      long xPeriod = 0;
      long yPeriod = 0;
      long zPeriod = 0;
      int[] position;
      int[] velocity;
      int[] testPosition;
      int[] testVelocity;
      position = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getX()).toArray();
      velocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getX()).toArray();
      do {
        advance(1);
        xPeriod++;
        testPosition = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getX()).toArray();
        testVelocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getX()).toArray();
      } while (!(Arrays.equals(position, testPosition) && Arrays.equals(velocity, testVelocity)));
      position = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getY()).toArray();
      velocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getY()).toArray();
      do {
        advance(1);
        yPeriod++;
        testPosition = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getY()).toArray();
        testVelocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getY()).toArray();
      } while (!(Arrays.equals(position, testPosition) && Arrays.equals(velocity, testVelocity)));
      position = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getZ()).toArray();
      velocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getZ()).toArray();
      do {
        advance(1);
        zPeriod++;
        testPosition = Arrays.stream(bodies).mapToInt(body -> body.getPosition().getZ()).toArray();
        testVelocity = Arrays.stream(bodies).mapToInt(body -> body.getVelocity().getZ()).toArray();
      } while (!(Arrays.equals(position, testPosition) && Arrays.equals(velocity, testVelocity)));
      return lcm(xPeriod, yPeriod, zPeriod);
    }

    private long gcd(long a, long b) {
      if (a == 0) {
        if (b == 0) {
          throw new IllegalArgumentException();
        }
        return b;
      }
      a = Math.abs(a);
      b = Math.abs(b);
      while (b > 0) {
        long temp = b;
        b = a % b;
        a = temp;
      }
      return a;
    }

    private long gcd(long... values) {
      return Arrays.stream(Arrays.copyOfRange(values, 1, values.length))
          .reduce(values[0], this::gcd);
    }

    private long lcm(long a, long b) {
      return a * b / gcd(a, b);
    }

    private long lcm(long... values) {
      return Arrays.stream(Arrays.copyOfRange(values, 1, values.length))
          .reduce(values[0], this::lcm);
    }

  }

  private static class Body {

    private static final String FORMAT = "Body[p=%s, v=%s]";

    private Vector position;
    private Vector velocity;

    public Body(Vector position) {
      this.position = position;
      velocity = new Vector(0, 0, 0);
    }

    public Body(int x, int y, int z) {
      this(new Vector(x, y, z));
    }

    public void attract(Body other) {
      Vector otherPosition = other.position;
      int deltaX = (int) Math.signum(otherPosition.getX() - position.getX());
      int deltaY = (int) Math.signum(otherPosition.getY() - position.getY());
      int deltaZ = (int) Math.signum(otherPosition.getZ() - position.getZ());
      Vector acceleration = new Vector(deltaX, deltaY, deltaZ);
      velocity = velocity.add(acceleration);
      other.velocity = other.velocity.subtract(acceleration);
    }

    public void move() {
      position = position.add(velocity);
    }

    public Vector getPosition() {
      return position;
    }

    public Vector getVelocity() {
      return velocity;
    }

    public int getPotentialEnergy() {
      return Math.abs(position.getX()) + Math.abs(position.getY()) + Math.abs(position.getZ());
    }

    public int getKineticEnergy() {
      return Math.abs(velocity.getX()) + Math.abs(velocity.getY()) + Math.abs(velocity.getZ());
    }

    @Override
    public String toString() {
      return String.format(FORMAT, position, velocity);
    }
  }

  private static class Vector {

    private static final Pattern PARSE_PATTERN =
        Pattern.compile("<x=([-+]?\\d+),y=([-+]?\\d+),z=([-+]?\\d+)>");
    private static final String FORMAT = "(%d, %d, %d)";

    private final int x;
    private final int y;
    private final int z;

    public static Vector parse(String input) {
      Matcher matcher = PARSE_PATTERN.matcher(input.replaceAll("\\s*", ""));
      if (matcher.matches()) {
        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));
        int z = Integer.parseInt(matcher.group(3));
        return new Vector(x, y, z);
      } else {
        throw new IllegalArgumentException();
      }
    }

    public Vector(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Vector add(Vector other) {
      return new Vector(x + other.x, y + other.y, z + other.z);
    }

    public Vector subtract(Vector other) {
      return new Vector(x - other.x, y - other.y, z - other.z);
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getZ() {
      return z;
    }

    @Override
    public String toString() {
      return String.format(FORMAT, x, y, z);
    }

  }

}
