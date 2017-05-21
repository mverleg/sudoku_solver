package net.wangcn.sudoku;

public class Solver implements Cloneable {
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

  Solver(Board board, boolean[][][] scratch) {
    // For cloning
    this.MAX = board.MAX;
    if (scratch.length != MAX || scratch[0].length != MAX || scratch[0][0].length != MAX) {
      throw new IllegalArgumentException();
    }
    this.board = board;
    this.scratch = scratch;
  }

  public Board solve() {
    int updateCount;
    int level = 0;
    // Try steps in order, restart at the first one each time an update is made,
    // and keep going until no step results in any updates.
    while (true) {
//      System.out.println("doing step 1");
      try {
        updateCount = this.resolveOneOptionCells();
      } catch (InconsistentSudokuBoardException ex) {
        System.out.println("Inconsistent! " + ex.toString());
        break;
      }
//      this.board.disp();
      if (updateCount > 0) continue;
      level = Math.max(level, 1);
//      System.out.println("doing step 2");
      updateCount = this.resolveOnlyOnePossiblePosition();
//      this.board.disp();
      if (updateCount > 0) continue;
      level = Math.max(level, 2);
      try {
        updateCount = this.branchPossibilities();
      } catch (InconsistentSudokuBoardException ex) {
        System.err.println("INCONSISTENT BOARD STATE; STOPPING!!! " + ex.toString());
        break;
      }
      if (updateCount == 0) break;
      level = Math.max(level, 3);
    }
    System.out.println("Highest level: " + level);
    return this.board;
  }

  /*
  Set a sure value, updating the scratchpad to reflect the new restrictions.
   */
  private void setValue(int x, int y, int value) {
    // Clear the row
    for (int j = 0; j < MAX; j++) {
      this.scratch[x][j][value - 1] = false;
    }
    // Clear the column
    for (int i = 0; i < MAX; i++) {
      this.scratch[i][y][value - 1] = false;
    }
    // Clear the block
    final int sqrtMax = (int) Math.sqrt(MAX);
    final int iini = x - x % sqrtMax;
    for (int i = iini; i < iini + sqrtMax; i++) {
      final int jini = y - y % sqrtMax;
      for (int j = jini; j < jini + sqrtMax; j++) {
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
  private int resolveOneOptionCells() throws InconsistentSudokuBoardException {
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
            throw new InconsistentSudokuBoardException("Found inconsistent state "
                + "(0 possibilities) when attempting to resolve cell, possibly during branch?");
          }
          this.setValue(x, y, knownValue);
          updateCount++;
        }
      }
    }
    return updateCount;
  }

  /*
  For each row, column or cell, if there's only one place a 5 can go, then
  it should go there, even if that cell has other possibilities.
   */
  private int resolveOnlyOnePossiblePosition() {
    int updateCount = 0;
    for (int k = 0; k < MAX; k++) {
      final int val = k + 1;
      for (int x = 0; x < MAX; x++) {
        // Check the row
        int yfound = -1;
        findUniquePos: {
          for (int y = 0; y < MAX; y++) {
            // If the value is already known, we can stop for this row
            if (this.board.get(x, y) == val) {
              break findUniquePos;
            }
            if (this.scratch[x][y][k]) {
              if (yfound >= 0) {
                // We had already found a position, so it is not unique; stop
                break findUniquePos;
              }
              yfound = y;
            }
          }
          // If we get here, there is only one legal position to put `val`
          if (yfound > 0) {
//            System.out.println("ROW FOUND POSITION " + x + " " + yfound + " for " + val);
            updateCount++;
            setValue(x, yfound, val);
          }
        }
      }
      for (int y = 0; y < MAX; y++) {
        // Check the column
        int xfound = -1;
        findUniquePos: {
          for (int x = 0; x < MAX; x++) {
            // If the value is already known, we can stop for this row
            if (this.board.get(x, y) == val) {
              break findUniquePos;
            }
            if (this.scratch[x][y][k]) {
              if (xfound >= 0) {
                // We had already found a position, so it is not unique; stop
                break findUniquePos;
              }
              xfound = x;
            }
          }
          // If we get here, there is only one legal position to put `val`
          if (xfound > 0) {
//            System.out.println("COLUMN FOUND POSITION " + xfound + " " + y + " for " + val);
            updateCount++;
            setValue(xfound, y, val);
          }
        }
      }
      final int sqrtMax = (int) Math.sqrt(MAX);
      for (int blockx = 0; blockx < sqrtMax; blockx++) {
        for (int blocky = 0; blocky < sqrtMax; blocky++) {
          // Search the block for identical values
          int xfound = -1, yfound = -1;
          findUniquePos:
          {
            for (int x = blockx * sqrtMax; x < (blockx + 1) * sqrtMax; x++) {
              for (int y = blockx * sqrtMax; y < (blockx + 1) * sqrtMax; y++) {
                // If the value is already known, we can stop for this row
                if (this.board.get(x, y) == val) {
                  break findUniquePos;
                }
                if (this.scratch[x][y][k]) {
                  if (xfound >= 0) {
                    // We had already found a position, so it is not unique; stop
                    break findUniquePos;
                  }
                  xfound = x;
                  yfound = y;
                }
              }
            }
            // If we get here, there is only one legal position to put `val`
            if (xfound > 0) {
//              System.out.println("BLOCK FOUND POSITION " + xfound + " " + yfound + " for " + val);
              updateCount++;
              setValue(xfound, yfound, val);
            }
          }
        }
      }
    }
    return updateCount;
  }

  /*
  Find a cell with the fewest options (> 1), and try both.
  All but one should lead to an inconsistency.
   */
  private int branchPossibilities() throws InconsistentSudokuBoardException {
    if (! this.board.isConsistent()) {
      throw new InconsistentSudokuBoardException("Board state is not consistent at the start of branchTestPossibilities");
    }
    int fewx = -1, fewy = -1, fewCount = MAX + 1;
    for (int x = 0; x < MAX; x++) {
      for (int y = 0; y < MAX; y++) {
        if (this.board.get(x, y) != Board.UNKNOWN) {
          continue;
        }
        int possibleCount = 0;
        for (int k = 0; k < MAX; k++) {
          if (this.scratch[x][y][k]) {
            possibleCount++;
          }
        }
        if (possibleCount < fewCount) {
          fewx = x;
          fewy = y;
          fewCount = possibleCount;
        }
      }
    }
    if (fewx < 0) {
      System.out.println("Sudoku solved!");
      return 0;
    }
    for (int k = 0; k < MAX; k++) {
      if (this.scratch[fewx][fewy][k]) {
        System.out.printf("Branching at %d, %d (%d options); setting %d\n", fewx, fewy, fewCount, k + 1);
        Solver subsolver = this.clone();
        subsolver.setValue(fewx, fewy, k + 1);
        Board board = subsolver.solve();
        if (board.isSolved()) {
          System.out.printf("Branch %d, %d => %d found a solution! Using this value (repeating steps)\n", fewx, fewy, k + 1);
          this.setValue(fewx, fewy, k + 1);
          return 1;
        }
      }
    }
    // This means none of the branches reached a legal state, which should be impossible.
    throw new IllegalStateException();
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

  public Solver clone() {
    Board cloneBoard = this.board.clone();
    boolean[][][] cloneScratch = new boolean[MAX][MAX][MAX];
    for (int x = 0; x < MAX; x++) {
      for (int y = 0; y < MAX; y++) {
        for (int k = 0; k < MAX; k++) {
          cloneScratch[x][y][k] = scratch[x][y][k];
        }
      }
    }
    return new Solver(cloneBoard, cloneScratch);
  }
}
