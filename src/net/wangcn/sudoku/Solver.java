package net.wangcn.sudoku;

public class Solver {
  private final int MAX;
  private Board board;
  private boolean[][][] scratch;

  public Solver(Board board) {
    this.board = board;
    this.MAX = board.MAX;
    this.scratch = new boolean[MAX][MAX][MAX];
    for (int x = 0; x < MAX; x++) {
      for (int y = 0; y < MAX; y++) {
        for (int k = 0; k < MAX; k++) {
          this.scratch[x][y][k] = true;
        }
      }
    }
    for (int x = 0; x < MAX; x++) {
      for (int y = 0; y < MAX; y++) {
        if (this.board.get(x, y) != Board.UNKNOWN) {
          this.setValue(x, y, this.board.get(x, y));
        }
      }
    }
  }

  public Board solve() {
    while (this.resolveOneOptionCells() > 0);
    return this.board;
  }

  /*
  Set a sure value, updating the scratchpad to reflect the new restrictions.
   */
  private void setValue(int x, int y, int value) {
    // Clear the row
    for (int i = 0; i < MAX; i++) {
      this.scratch[i][y][value - 1] = false;
    }
    // Clear the column
    for (int j = 0; j < MAX; j++) {
      this.scratch[x][j][value - 1] = false;
    }
    // Clear the block
    final int sqrtMax = (int) Math.sqrt(MAX);
    final int iini = x - x % sqrtMax;
    for (int i = iini; i < iini + sqrtMax; i++) {
      final int jini = y - y % sqrtMax;
      for (int j = jini; j < jini + sqrtMax; j++) {
        System.out.printf("for %d, %d clear %d, %d (value %d)\n", x, y, i, j, value);
        this.scratch[i][j][value - 1] = false;
      }
    }
    // Remove possibilities from cell
    for (int k = 0; k < MAX; k++) {
      this.scratch[x][y][k] = (k + 1 == value);
    }
    // Set on the board
    this.board.set(x, y, value);
  }

  /*
  Find cells which weren't previously known but which have only one option now.
   */
  private int resolveOneOptionCells() {
    int updateCount = 0;
    for (int x = 0; x < MAX; x++) {
      for (int y = 0; y < MAX; y++) {
        if (this.board.get(x, y) != Board.UNKNOWN) {
          // This value was already known
          continue;
        }
        int knownValue = -1;
        findUniqueValue: {
          for (int k = 0; k < MAX; k++) {
            if (this.scratch[x][y][k]) {
              if (knownValue >= 0) {
                // knownValue was set and we found another candidate, so it's not unique
                break findUniqueValue;
              }
              knownValue = k + 1;
            }
          }
          // If we get here, then either 0 or 1 values were found (0 should be impossible)
          if (knownValue == -1) {
            throw new IllegalStateException();
          }
          this.setValue(x, y, knownValue);
          updateCount += 1;
        }
      }
    }
    System.out.println("updateCount = " + updateCount); // todo
    return updateCount;
  }

  /*
  Print the scratchpad to the screen.
   */
  public void dispScratch() {
    for (int x = 0; x < MAX; x++) {
      final int sqrtMax = (int) Math.sqrt(MAX);
      for (int i = 0; i < sqrtMax; i++) {
        for (int y = 0; y < MAX; y++) {
          for (int j = 0; j < sqrtMax; j++) {
            int val = sqrtMax * i + j + 1;
            if (this.scratch[x][y][val - 1]) {
              System.out.print(val);
            } else {
              System.out.print(".");
            }
          }
          if (y % 3 == 2) {
            System.out.print(" | ");
          } else {
            System.out.print(" ");
          }
        }
        System.out.println();
      }
      if (x < MAX - 1 && x % 3 == 2) {
        System.out.println("=== === === | === === === | === === ===");
      } else {
        System.out.println();
      }
    }
  }
}
