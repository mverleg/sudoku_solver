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
    // todo: solve difficult puzzles
    return this.board;
  }

  /*
  Set a sure value, updating the scratchpad to reflect the new restrictions.
   */
  private void setValue(int x, int y, int value) {
    // todo
    // Clear the row

    // Clear the column

    // Clear the block

    // Remove possibilities from cell

    // Set on the board
    this.board.set(x, y, value);
  }

  /*
  Find cells which weren't previously known but which have only one option now.
   */
  private int resolveOneOptionCells() {
    // todo
    return 0;
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
