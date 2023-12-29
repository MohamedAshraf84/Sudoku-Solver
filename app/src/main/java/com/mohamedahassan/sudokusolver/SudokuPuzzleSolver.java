package com.mohamedahassan.sudokusolver;

public class SudokuPuzzleSolver  {
    private final int BOARD_SIZE = 9;
    private boolean mIsSleep;
    private boolean mAnimation;
    private BoardCell [][] mBoard;
    private final SudokuValidator mSudokuValidator;
    private int mNoOfEmptyCells;
    private int mNoOfCurrentFilledCells;
    private int mAnimationSpeed;
    private SudokuSolverListener mSudokuSolverListener;

    public SudokuPuzzleSolver(BoardCell[][] board)
    {
        this.setBoard(board);
        this.mSudokuValidator = new SudokuValidator();
    }
    public void setSudokuListener(SudokuSolverListener SudokuSolverListener)
    {
        this.mSudokuSolverListener = SudokuSolverListener;
    }

    public void setBoard(BoardCell[][] board) {
        this.mBoard = board;
        this.mNoOfCurrentFilledCells = 0;
        this.mNoOfEmptyCells = this.countEmptyCells();
    }

    public void setAnimation(boolean animation) {
        this.mAnimation = animation;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.mAnimationSpeed = animationSpeed;
    }

    public BoardCell[][] getBoard()
    {
        return mBoard;
    }

    public boolean isSleep()
    {
        return mIsSleep;
    }

    public SudokuValidator getSudokuValidator() {
        return mSudokuValidator;
    }

    private int countEmptyCells()
    {
        int noOfEmptyCells = 0;
        for (int r = 0; r < BOARD_SIZE; ++r) {
            for (int c = 0; c < BOARD_SIZE; ++c) {
                if (mBoard[r][c].getNumber() == 0)
                    noOfEmptyCells++;
            }
        }

        return noOfEmptyCells;
    }


    public boolean /*int [][]*/ solveSudoku()
    {

        if (mSudokuSolverListener != null)
            mSudokuSolverListener.onSolverStarted();

        boolean solved = solve();

        if (mSudokuSolverListener != null)
            mSudokuSolverListener.onSolverFinished();

        return solved;
    }

    public boolean solve(){

        if(mNoOfCurrentFilledCells == mNoOfEmptyCells)
            return true;

        for (int row = 0; row < BOARD_SIZE; ++row) {
            for (int col = 0; col < BOARD_SIZE; ++col) {
                if(mBoard[row][col].getNumber() == 0)
                {
                    for (int num = 1; num <= BOARD_SIZE; ++num) {
                        if(mSudokuValidator.isValid(mBoard, row, col, num))
                        {
                            mBoard[row][col] = new BoardCell(num, false);
                            mNoOfCurrentFilledCells++;

                            delay();

                            if(solve())
                                return true;

                            mBoard[row][col] = new BoardCell(0, false);
                            mNoOfCurrentFilledCells--;

                            delay();
                        }
                    }
                    return false;
                }
            }
        }

        return false;
    }

    private void delay() {
        if(mAnimation)
        {
            mIsSleep = true;
            sleep(mAnimationSpeed);
            mIsSleep = false;
        }
    }


    public void sleep(int speed)
    {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public interface SudokuSolverListener
    {
        void onSolverStarted();
        void onSolverFinished();
    }
}