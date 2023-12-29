package com.mohamedahassan.sudokusolver;

public class SudokuValidator {

    final int BOARD_SIZE = 9;
    final int SUB_BOARD_SIZE = 3;

    private boolean notInRow(BoardCell[][] board, int row, int col, int n)
    {
        for (int iColumn = 0; iColumn < BOARD_SIZE; ++iColumn) {
            if(col != iColumn && board[row][iColumn].getNumber() == n)
                return false;
        }
        return true;
    }

    private boolean notInColumn(BoardCell[][] board, int row, int col, int n)
    {
        for (int iRow = 0; iRow < BOARD_SIZE; ++iRow) {
            if(iRow != row && board[iRow][col].getNumber() == n)
                return false;
        }
        return true;
    }


    private boolean notInSubGrid(BoardCell[][] board, int r, int c, int n)
    {
        int ROW_OFFSET = r - (r % SUB_BOARD_SIZE);
        int COLUMN_OFFSET = c - (c % SUB_BOARD_SIZE);

        for(int i = 0; i < SUB_BOARD_SIZE; ++i)
        {
            for(int j = 0; j < SUB_BOARD_SIZE; ++j)
            {
                if( (ROW_OFFSET + i) != r && (COLUMN_OFFSET + j) != c
                     && board[ROW_OFFSET + i][COLUMN_OFFSET + j].getNumber() == n)
                    return false;
            }
        }
        return true;
    }
    boolean isValid(BoardCell[][] board, int r, int c, int n)
    {
        return(notInRow(board, r, c, n) && notInColumn(board, r, c, n) && notInSubGrid(board, r, c, n));
    }

    boolean isValidSudoku(BoardCell[][] board) {

        for(int r = 0; r < BOARD_SIZE; ++r)
        {
            for(int c = 0; c < BOARD_SIZE; ++c)
            {
                int num = board[r][c].getNumber();
                if(num != 0)
                {
                    if(!isValid(board, r, c, num))
                        return false;
                }
            }
        }
        return true;
    }

}

