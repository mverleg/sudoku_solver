package net.wangcn.sudoku;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;

public class TestBoard {
  @Test
  public void testIsConsistent() throws FileNotFoundException, InvalidSudokuFileException {
    Board board;
    board = new Board();
    for (int value = 1; value <= board.MAX; value++) {
      board.setGiven(0, 0, value);
      board.setGiven(2, 2, value);
      assert (! board.isConsistent());
    }
    board = new Board();
    for (int value = 1; value <= board.MAX; value++) {
      board.setGiven(0, 0, value);
      board.setGiven(0, board.MAX - 1, value);
      assert (! board.isConsistent());
    }
    board = new Board();
    for (int value = 1; value <= board.MAX; value++) {
      board.setGiven(0, 0, value);
      board.setGiven(board.MAX - 1, 0, value);
      assert (! board.isConsistent());
    }
    board = new Board();
    for (int value = 1; value <= board.MAX; value++) {
      board.setGiven(0, 0, value);
      board.setGiven(board.MAX - 1, 0, ((value + 1) % 9) + 1);
      board.setGiven(0, board.MAX - 1, ((value + 2) % 9) + 1);
      board.setGiven(2, 2, ((value + 3) % 9) + 1);
      assert (board.isConsistent());
    }
  }
}
