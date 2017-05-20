package net.wangcn.sudoku;

import java.io.File;
import java.io.FileNotFoundException;

class Main {
  public static void main(String[] args) {
    Board board = null;
    try {
      board = new Board(new File("data/sudoku/easy/001_initial.csv"));
    } catch (FileNotFoundException | InvalidSudokuFileException e) {
      System.err.println("Sudoku file not found or not readable!");
      return;
    }
    board.disp();

    Solver solver = new Solver(board);
    solver.dispScratch();
    board = solver.solve();
    solver.dispScratch();
    //todo: Do solving stuff here!

    if (board.isSolved()) {
      System.out.println("YAAY IT IS SOLVED!");
    } else {
      System.out.println("Did not make it, too bad!");
    }
    board.disp();
  }
}

