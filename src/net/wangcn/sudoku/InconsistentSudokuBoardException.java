package net.wangcn.sudoku;

public class InconsistentSudokuBoardException extends Throwable {
  public InconsistentSudokuBoardException(String msg) {
      super(msg);
    }
}
