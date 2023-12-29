package com.mohamedahassan.sudokusolver;

import java.security.SecureRandom;
import java.util.Arrays;

public class SudokuPuzzleGenerator {

    public static final int EASY = 40;
    public static final int MEDIUM = 35;
    public static final int HARD = 25;
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    private int EMPTY_CELLS;
    private BoardCell[][] mPuzzle;
    private final SudokuValidator mSudokuValidator;


    public SudokuPuzzleGenerator(int difficulty)
    {
        initPuzzle();
        mSudokuValidator = new SudokuValidator();
        EMPTY_CELLS = difficulty;
    }

    public void setDifficulty(int difficulty)
    {
       EMPTY_CELLS = difficulty;
    }
    public BoardCell[][] generatePuzzle() {
        resetPuzzle();
        fillGrid();
        removeNumbers();
        finalizePuzzle();

        return mPuzzle;
    }

    private void finalizePuzzle() {
        for (int i = 0; i < GRID_SIZE; ++i)
            for (int j = 0; j < GRID_SIZE; ++j)
                if (mPuzzle[i][j].getNumber() != 0)
                    mPuzzle[i][j].setOriginal(true);
    }

    private void initPuzzle()
    {
        mPuzzle = new BoardCell[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; ++i)
            for (int j = 0; j < GRID_SIZE; ++j)
                mPuzzle[i][j] = new BoardCell(0, false);
    }


    private void resetPuzzle() {
        for (BoardCell[] row: mPuzzle)
            Arrays.fill(row, new BoardCell(0, false));
    }

    private void fillGrid() {
        fillDiagonalSubGrids();
        SudokuPuzzleSolver sudokuSolver = new SudokuPuzzleSolver(mPuzzle);
        sudokuSolver.solveSudoku();
    }

    private void fillDiagonalSubGrids() {

        for (int i = 0; i < GRID_SIZE; i += SUBGRID_SIZE)
            fillSubGrid(i, i);
    }

    private void fillSubGrid(int row, int col) {
        int num;
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                do {
                    num = random.nextInt(GRID_SIZE) + 1;
                } while (!mSudokuValidator.isValid(mPuzzle,row + i, col + j, num));

                mPuzzle[row + i][col + j] = new BoardCell(num, true);
            }
        }
    }

    private void removeNumbers() {
        SecureRandom random = new SecureRandom();
        int cellsToRemove = GRID_SIZE * GRID_SIZE - EMPTY_CELLS;

        while (cellsToRemove > 0) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (mPuzzle[row][col].getNumber() != 0) {
                mPuzzle[row][col] = new BoardCell(0, false);

                cellsToRemove--;
            }
        }
    }
}
