package net.wangcn.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.Scanner;

public class Board {
  public static final int MAX = 9;
  public static final int UNKNOWN = -1;
  private final int[][] data = new int[MAX][MAX];
  private final int[][] original = new int[MAX][MAX];

  public Board(File initialStateFile) throws FileNotFoundException, InvalidSudokuFileException {
    this.load(initialStateFile);
  }

  Board() {
    // For testing
    for (int x = 0; x < this.data.length; x++) {
      for (int y = 0; y < this.data[x].length; y++) {
        this.data[x][y] = UNKNOWN;
      }
    }
  }

  /*
  Load a game from a file
   */
  private void load(File initialStateFile) throws FileNotFoundException, InvalidSudokuFileException {
    Scanner scanner = new Scanner(initialStateFile);
    for (int x = 0; x < this.data.length; x++) {
      for (int y = 0; y < this.data[x].length; y++) {
        String value = scanner.next();
        if (Objects.equals(value, ".") || Objects.equals(value, "?")) {
          this.data[x][y] = UNKNOWN;
        } else {
          try {
            this.data[x][y] = Integer.parseInt(value);
          } catch (NumberFormatException ex) {
            throw new InvalidSudokuFileException(
                String.format("Could not understand %s in Sudoku file!", value));
          }
        }
        this.original[x][y] = this.data[x][y];
      }
    }
  }

  static private boolean isValidValue(int value) {
    if (value == UNKNOWN) {
      return true;
    }
    if (value >= 1 && value <= MAX) {
      return true;
    }
    return false;
  }

  public int get(int x, int y) {
    return this.data[x][y];
  }

  void setGiven(int x, int y, int value) {
    // For testing
    if (! isValidValue(value)) {
      throw new IllegalArgumentException("Cannot set value " + value + "!");
    }
    this.data[x][y] = value;
    this.original[x][y] = value;
  }

  public void set(int x, int y, int value) {
    if (! isValidValue(value)) {
      throw new IllegalArgumentException("Cannot set value " + value + "!");
    }
    if ((this.original[x][y] != UNKNOWN && this.original[x][y] != value)) {
      throw new IllegalStateException("Cannot overwrite given values!");
    }
    this.data[x][y] = value;
  }

  /*
  Check that there are no double numbers per row, column or block.
   */
  public boolean isConsistent() {
    // For each value...
    final int sqrtMax = (int) Math.sqrt(MAX);
    for (int k = 0; k < MAX; k++) {
      final int val = k + 1;
      // Check columns
      for (int x = 0; x < MAX; x++) {
        int count = 0;
        for (int y = 0; y < MAX; y++) {
          if (this.data[x][y] == val) {
            count++;
          }
        }
        if (count > 1) {
          return false;
        }
      }
      // Check rows
      for (int y = 0; y < MAX; y++) {
        int count = 0;
        for (int x = 0; x < MAX; x++) {
          if (this.data[x][y] == val) {
            count++;
          }
        }
        if (count > 1) {
          return false;
        }
      }
      // Check cell
      for (int xblock = 0; xblock < sqrtMax; xblock++) {
        for (int yblock = 0; yblock < sqrtMax; yblock++) {
          int count = 0;
          for (int xsub = xblock * sqrtMax; xsub < (xblock + 1) * sqrtMax; xsub++) {
            for (int ysub = yblock * sqrtMax; ysub < (yblock + 1) * sqrtMax; ysub++) {
              System.out.printf("%d %d -> %d %d\n", xblock, yblock, xsub, ysub);
              if (this.data[xsub][ysub] == val) {
                count++;
              }
            }
          }
          if (count > 1) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /*
  Check that the puzzle has been solved correctly.
   */
  public boolean isSolved() {
    for (int x = 0; x < this.data.length; x++) {
      for (int y = 0; y < this.data[x].length; y++) {
        if (this.data[x][y] == UNKNOWN) {
          // A value is not filled; puzzle not solved!
          return false;
        }
        if (this.original[x][y] != UNKNOWN) {
          if (this.original[x][y] != this.data[x][y]) {
            // Given values in the original have been saved.
            throw new IllegalStateException("Given values have been overwritten; this is invalid!");
          }
        }
      }
    }
    if (! this.isConsistent()) {
      // Sole rules have been broken, puzzle is not solved!
      return false;
    }
    // Passed all the tests, puzzle is solved!
    return true;
  }

  public String toString() {
    return this.toString(true);
  }

  public String toString(boolean showBorders) {
    StringBuilder out = new StringBuilder();
    for (int x = 0; x < this.data.length; x++) {
      if (showBorders && x >  0 && x % 3 == 0) {
        for (int y = 0; y < 2 * this.data[x].length + 3; y++) {
          out.append("-");
        }
        out.append('\n');
      }
      for (int y = 0; y < this.data[x].length; y++) {
        if (showBorders && y >  0 && y % 3 == 0) {
          out.append("| ");
        }
        if (this.data[x][y] == UNKNOWN) {
          out.append(". ");
        } else {
          out.append(String.format("%d ", this.data[x][y]));
        }
      }
      out.append('\n');
    }
    return out.toString();
  }

  public void disp() {
    System.out.println(this.toString());
  }
}
