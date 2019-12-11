package day10;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMain {

  private static final String INPUT_FILE = "input.txt";
  private static final int WAGER_DEMOLITION_POSITION = 200;
  private static final String MAX_UNOBSTRUCTED_ASTEROIDS = "Part 1: The maximum number of "
      + "asteroids with unobstructed visibility from the selected monitoring station is %d.%n";
  private static final String DEMOLITION_SEQUENCE_ITEM =
      "Part 2: The %dth asteroid in the clockwise demolition sequence is at %s.%n";


  public static void main(String[] args) throws URISyntaxException, IOException {
    AsteroidBelt belt = AsteroidBelt.from(INPUT_FILE);
    belt.selectStation();
    System.out.printf(MAX_UNOBSTRUCTED_ASTEROIDS, belt.getVisible().size());
    System.out.printf(DEMOLITION_SEQUENCE_ITEM,
        WAGER_DEMOLITION_POSITION, belt.demolitionSequence().get(WAGER_DEMOLITION_POSITION - 1));
  }

  private static class AsteroidBelt {

    private static final double TOLERANCE = 0.000001;

    private final List<Location> asteroids;
    private Location station;
    private List<Location> visible;
    private List<Location> blocked;

    private AsteroidBelt(List<Location> asteroids) {
      this.asteroids = asteroids;
    }

    public static AsteroidBelt from(String inputFile)
        throws URISyntaxException, IOException {
      Path path = Path.of(AsteroidBelt.class.getResource(INPUT_FILE).toURI());
      return new AsteroidBelt(parse(path));
    }

    public List<Location> getAsteroids() {
      return asteroids;
    }

    public Location getStation() {
      return station;
    }

    public List<Location> getVisible() {
      return visible;
    }

    public List<Location> getBlocked() {
      return blocked;
    }

    public void selectStation() {
      Location bestStation = null;
      List<Location> bestVisible = null;
      int index = 0;
      for (Location candidate : asteroids) {
        List<Location> visible = visibleFrom(candidate, index++);
        if (bestVisible == null || visible.size() > bestVisible.size()) {
          bestVisible = visible;
          bestStation = candidate;
        }
      }
      station = bestStation;
      visible = bestVisible;
      visible.sort(Comparator.comparing(loc -> station.clockwiseAngleTo(loc)));
      Set<Location> blocked = new HashSet<>(asteroids);
      blocked.remove(bestStation);
      blocked.removeAll(bestVisible);
      this.blocked = new ArrayList<>(blocked);
      this.blocked.sort(Comparator.comparing((Location loc) -> station.clockwiseAngleTo(loc))
          .thenComparing((Location loc) -> station.distanceTo(loc)));
    }

    public List<Location> demolitionSequence() {
      if (station == null) {
        throw new IllegalStateException();
      }
      List<Location> zapped = new LinkedList<>(visible);
      List<Location> waiting = new LinkedList<>(blocked);
      while (!waiting.isEmpty()) {
        double previousAngle = -Double.MAX_VALUE;
        for (Iterator<Location> iter = waiting.iterator(); iter.hasNext(); ) {
          Location target = iter.next();
          double angle = station.clockwiseAngleTo(target);
          if (angle - previousAngle > TOLERANCE) {
            previousAngle = angle;
            zapped.add(target);
            iter.remove();
          }
        }
      }
      return zapped;
    }

    private static List<Location> parse(Path path) throws IOException {
      AtomicInteger rowCounter = new AtomicInteger(0);
      try (Stream<String> stream = Files.lines(path)) {
        return stream
            .map(String::trim)
            .filter(Predicate.not(String::isEmpty))
            .flatMap(s -> {
              int row = rowCounter.getAndIncrement();
              List<Location> asteroids = new LinkedList<>();
              char[] chars = s.toCharArray();
              for (int col = 0; col < chars.length; col++) {
                if (chars[col] == '#') {
                  asteroids.add(new Location(row, col));
                }
              }
              return asteroids.stream();
            })
            .collect(Collectors.toList());
      }
    }

    private List<Location> visibleFrom(Location candidate, int split) {
      List<Location> visible = new ArrayList<>();
      Set<Location> blocked = new HashSet<>();
      // Left partition scan.
      for (ListIterator<Location> blockIter = asteroids.listIterator(split);
          blockIter.hasPrevious(); ) {
        int position = blockIter.previousIndex();
        Location block = blockIter.previous();
        if (!blocked.contains(block)) {
          visible.add(block);
          for (ListIterator<Location> testIter = asteroids.listIterator(position);
              testIter.hasPrevious(); ) {
            Location test = testIter.previous();
            if (collinear(candidate, block, test)) {
              blocked.add(test);
            }
          }
        }
      }
      // Right partition scan.
      blocked.clear();
      for (ListIterator<Location> blockIter = asteroids.listIterator(split + 1);
          blockIter.hasNext(); ) {
        Location block = blockIter.next();
        int position = blockIter.nextIndex();
        if (!blocked.contains(block)) {
          visible.add(block);
          for (ListIterator<Location> testIter = asteroids.listIterator(position);
              testIter.hasNext(); ) {
            Location test = testIter.next();
            if (collinear(candidate, block, test)) {
              blocked.add(test);
            }
          }
        }
      }
      return visible;
    }

    private boolean collinear(Location loc1, Location loc2, Location loc3) {
      int rowDiff1 = loc2.getRow() - loc1.getRow();
      int colDiff1 = loc2.getColumn() - loc1.getColumn();
      int rowDiff2 = loc3.getRow() - loc1.getRow();
      int colDiff2 = loc3.getColumn() - loc1.getColumn();
      return ((rowDiff1 == 0 && rowDiff2 == 0)
          || (colDiff1 == 0 && colDiff2 == 0)
          || (rowDiff1 * colDiff2 == rowDiff2 * colDiff1
              && rowDiff1 != 0 && rowDiff2 != 0 && colDiff1 != 0 && colDiff2 != 0));
    }

  }

  private static class Location implements Comparable<Location> {

    public static final Comparator<Location> BY_ROW =
        Comparator.comparing(Location::getRow).thenComparing(Location::getColumn);
    public static final Comparator<Location> BY_COLUMN =
        Comparator.comparing(Location::getColumn).thenComparing(Location::getRow);
    private static final String FORMAT = "%s(row: %d, col: %d)";
    private final int row;
    private final int column;
    private final int hash;
    private final String str;

    private Location(int row, int column) {
      this.row = row;
      this.column = column;
      hash = Objects.hash(row, column);
      str = String.format(FORMAT, getClass().getSimpleName(), row, column);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == this
          || (obj instanceof Location
              && ((Location) obj).row == row
              && ((Location) obj).column == column);
    }

    @Override
    public int compareTo(Location other) {
      return BY_ROW.compare(this, other);
    }

    @Override
    public String toString() {
      return str;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return column;
    }

    public double clockwiseAngleTo(Location other) {
      double angle = Math.atan2(other.column - column, row - other.row);
      if (angle < 0) {
        angle += 2 * Math.PI;
      }
      return angle;
    }

    public double distanceTo(Location other) {
      return Math.hypot(other.row - row, other.column - column);
    }

  }

}
