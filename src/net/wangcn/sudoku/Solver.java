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
    for (int i = 0; i < MAX; i++) {
      this.scratch[x][i][value - 1] = false;
    }
    // Clear the column
    for (int j = 0; j < MAX; j++) {
      this.scratch[j][y][value - 1] = false;
    }
    // Clear the block
    for (int m = 0; m <= 2; m++){
      if(x==3*m && y==3*m){
        this.scratch[x+1][y+1][value-1]= false;
        this.scratch[x+2][y+2][value-1]= false;
        this.scratch[x+2][y+1][value-1]= false;
        this.scratch[x+1][y+2][value-1]= false;
      }
      else if(x==3*m+1 && y==3*m+1){
        this.scratch[x+1][y+1][value-1]= false;
        this.scratch[x-1][y-1][value-1]= false;
        this.scratch[x-1][y+1][value-1]= false;
        this.scratch[x+1][y-1][value-1]= false;
      }
      else if(x==3*m+2 && y==3*m+2){
        this.scratch[x-1][y-1][value-1]= false;
        this.scratch[x-2][y-2][value-1]= false;
        this.scratch[x-2][y-1][value-1]= false;
        this.scratch[x-1][y-2][value-1]= false;
      }
      else if(x==3*m+1 && y==3*m){
        this.scratch[x+1][y+1][value-1]= false;
        this.scratch[x+1][y+2][value-1]= false;
        this.scratch[x-1][y+1][value-1]= false;
        this.scratch[x-1][y+2][value-1]= false;
      }
      else if(x==3*m+2 && y==3*m){
        this.scratch[x-1][y+1][value-1]= false;
        this.scratch[x-2][y+2][value-1]= false;
        this.scratch[x-2][y+1][value-1]= false;
        this.scratch[x-1][y+2][value-1]= false;
      }
      else if(x==3*m && y==3*m+1){
        this.scratch[x+1][y-1][value-1]= false;
        this.scratch[x+2][y+1][value-1]= false;
        this.scratch[x+2][y-1][value-1]= false;
        this.scratch[x+1][y+1][value-1]= false;
      }
      else if(x==3*m && y==3*m+2){
        this.scratch[x+1][y-1][value-1]= false;
        this.scratch[x+2][y-2][value-1]= false;
        this.scratch[x+2][y-1][value-1]= false;
        this.scratch[x+1][y-2][value-1]= false;
      }
      else if(x==3*m+1 && y==3*m+2){
        this.scratch[x+1][y-1][value-1]= false;
        this.scratch[x-1][y-2][value-1]= false;
        this.scratch[x-1][y-1][value-1]= false;
        this.scratch[x+1][y-2][value-1]= false;
      }
      else if(x==3*m+2 && y==3*m+1){
        this.scratch[x-1][y-1][value-1]= false;
        this.scratch[x-2][y+1][value-1]= false;
        this.scratch[x-2][y-1][value-1]= false;
        this.scratch[x-1][y+1][value-1]= false;
      }

    }
    // Remove possibilities from cell
    for (int k = 0; k < MAX; k++) {
      this.scratch[x][y][k] = false;
    }
    this.scratch[x][y][value - 1] = true;

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
          // If it's already known then we don't do anything
          continue;
        }
        // It's not known yet, so we check whether there is only 1 possibility
        int possibleCount = 0;
        int possibleValue = 1;
        for (int k = 0; k < MAX; k++) {
          if (this.scratch[x][y][k] == true) {
            possibleValue = k + 1;
            possibleCount++;
          }
        }
        if (possibleCount == 1) {
          setValue(x,y,possibleValue);
          updateCount++;
          System.out.println("possible count for " + x + "," + y + " = " + possibleCount + "; possible: " + possibleValue);
        }
      }
    }
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
