package com.mohamedahassan.sudokusolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class SudokuBoardView extends View {
    
    private final int BORDER_STROKE_WIDTH = 6,
                      LINE_STROKE_WIDTH = 3;
    public final static int GRID_SIZE = 9;
    public final static int SUB_GRID_SIZE = 3;

    private int mBoardTouchXCoordinate = Integer.MAX_VALUE,
            mBoardTouchYCoordinate = Integer.MAX_VALUE;
    private BoardCell[][] mBoard;
    private float mCellSize;
    private int mSize;
    private SudokuValidator mSudokuValidator;
    private Stack<Pair<Pair<Integer, Integer>, BoardCell>> mPreviousMoves;
    private Paint mBoardPaint,
                  mLinePaint,
                  mHighlightPaint,
                  mNumberPaint,
                  mWrongPlacedNumberPaint,
                  mSameAsSelectedCellPaint,
                  mSolvedCellsPaint;

    public void setBoard(BoardCell[][] board) {
        mBoard = board;
        invalidate();
    }
    
    public BoardCell[][] getSudokuBoard() {
        return mBoard;
    }
    
    public SudokuBoardView(Context context) {
        super(context);
        init(null);
    }

    public SudokuBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SudokuBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);

    }

    private void init(@Nullable AttributeSet set) {

        mBoard = new BoardCell[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; ++i)
            for (int j = 0; j < GRID_SIZE; ++j)
                mBoard[i][j] = new BoardCell();

        mBoardPaint = new Paint();
        mLinePaint = new Paint();
        mHighlightPaint = new Paint();
        mNumberPaint = new Paint();
        mSameAsSelectedCellPaint = new Paint();
        mWrongPlacedNumberPaint = new Paint();
        mSolvedCellsPaint = new Paint();
        mPreviousMoves = new Stack<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize, heightSize;
        int widthMode, heightMode;
        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = 400, desiredHeight = 400;
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        mSize = Math.min(width, height);
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBoardPaint.setStyle(Paint.Style.STROKE);
        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setStrokeWidth(BORDER_STROKE_WIDTH);
        mBoardPaint.setAntiAlias(true);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(LINE_STROKE_WIDTH);
        mLinePaint.setAntiAlias(true);

        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(getResources().getColor(R.color.highlight_cell));
        mHighlightPaint.setAntiAlias(true);
        

        float numberSize = (float) (1.15 * mCellSize);
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        mNumberPaint.setStyle(Paint.Style.FILL);
        mNumberPaint.setTypeface(typeface);
        mNumberPaint.setTextSize(numberSize);
        mNumberPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mNumberPaint.setAntiAlias(true);


        mSolvedCellsPaint.setStyle(Paint.Style.FILL);
        mSolvedCellsPaint.setTypeface(typeface);
        mSolvedCellsPaint.setTextSize(numberSize);
        mSolvedCellsPaint.setColor(getResources().getColor(R.color.solved_cell));
        mSolvedCellsPaint.setAntiAlias(true);

        mSameAsSelectedCellPaint.setStyle(Paint.Style.FILL);
        mSameAsSelectedCellPaint.setColor(getResources().getColor(R.color.same_as_selected_cell));
        mSameAsSelectedCellPaint.setAntiAlias(true);

        mWrongPlacedNumberPaint.setStyle(Paint.Style.FILL);
        mWrongPlacedNumberPaint.setColor(getResources().getColor(R.color.wrong_placed_cell));
        mWrongPlacedNumberPaint.setAntiAlias(true);

        mCellSize = mSize / (float) GRID_SIZE;

        drawBoard(canvas);
        drawBoardNumbers(canvas);
        colorCells(canvas);
        highlightSelectedNumber(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            float x = event.getY();
            float y = event.getX();

            mBoardTouchXCoordinate = (int) (x / mCellSize);
            mBoardTouchYCoordinate = (int) (y / mCellSize);

            return true;
        }

        return false;
    }

    private void drawBoard(Canvas canvas) {
        drawBorder(canvas);
        drawRows(canvas);
        drawColumns(canvas);
    }

    private void drawBorder(Canvas canvas)
    {
        canvas.drawRect(0,0, mSize, mSize, mBoardPaint);
    }

    private void drawRows(Canvas canvas) {
        for (int row = 1; row < GRID_SIZE; ++row) {
            mLinePaint.setStrokeWidth(1);
            if (row % SUB_GRID_SIZE == 0) {
                mLinePaint.setStrokeWidth(LINE_STROKE_WIDTH);
            }
            canvas.drawLine( 0.0f, mCellSize * row, mSize, mCellSize * row, mLinePaint);
        }
    }

    private void drawColumns(Canvas canvas) {
        for (int column = 1; column < GRID_SIZE; ++column) {
            mLinePaint.setStrokeWidth(1);
            if (column % SUB_GRID_SIZE == 0) {
                mLinePaint.setStrokeWidth(LINE_STROKE_WIDTH);
            }
            canvas.drawLine(mCellSize * column, 0.0f, mCellSize * column, mSize, mLinePaint);
        }
    }

    
    private void highlightSelectedNumber(Canvas canvas)
    {
        if(isTouchPositionInsideGrid()) {
            int num = mBoard[mBoardTouchXCoordinate][mBoardTouchYCoordinate].getNumber();
            mSudokuValidator = new SudokuValidator();
            if(num != 0)
            {
                if (mSudokuValidator.isValidSudoku(mBoard)) {
                    for (int r = 0; r < GRID_SIZE; ++r) {
                        for (int c = 0; c < GRID_SIZE; ++c) {
                            if (r != mBoardTouchXCoordinate && c != mBoardTouchYCoordinate
                                    && mBoard[r][c].getNumber() == num) {
                                canvas.drawRect(c * mCellSize, r * mCellSize, (c + 1) * mCellSize,
                                        (r + 1) * mCellSize, mSameAsSelectedCellPaint);
                            }
                        }
                    }
                }
                else
                {
                    for (int r = 0; r < GRID_SIZE; ++r) {
                        for (int c = 0; c < GRID_SIZE; ++c) {
                           if (mBoard[r][c].getNumber() == num) {

                               if((r == mBoardTouchXCoordinate || c == mBoardTouchYCoordinate)
                                  && getCellSubGridStartRow(r) == getCellSubGridStartRow(r)
                                  && getCellSubGridStartColumn(c) == getCellSubGridStartColumn(c)) {

                                   canvas.drawRect(c * mCellSize, r * mCellSize, (c + 1) * mCellSize,
                                           (r + 1) * mCellSize, mWrongPlacedNumberPaint);

                               }
                               else
                               {
                                   canvas.drawRect(c * mCellSize, r * mCellSize, (c + 1) * mCellSize,
                                           (r + 1) * mCellSize, mSameAsSelectedCellPaint);
                               }
                            }
                        }
                    }
                }
            }
        }
    }

    private void colorCells(Canvas canvas)
    {
        colorCell(canvas);
        colorRowCells(canvas);
        colorColumnCells(canvas);
        colorSubGridCells(canvas);
        invalidate();
    }

    private void colorCell(Canvas canvas)
    {
        if(isTouchPositionInsideGrid())
        {
            canvas.drawRect(mBoardTouchYCoordinate * mCellSize, mBoardTouchXCoordinate * mCellSize,
                    (mBoardTouchYCoordinate + 1) * mCellSize, (mBoardTouchXCoordinate + 1) * mCellSize, mHighlightPaint);
        }
    }

    private void colorRowCells(Canvas canvas)
    {
        if(isTouchPositionInsideGrid())
        {
            int startRow = getCellSubGridStartRow(mBoardTouchYCoordinate);
            canvas.drawRect(0, mBoardTouchXCoordinate * mCellSize,
                    startRow * mCellSize, (mBoardTouchXCoordinate + 1) * mCellSize, mHighlightPaint);

            canvas.drawRect((SUB_GRID_SIZE + startRow) * mCellSize, mBoardTouchXCoordinate * mCellSize,
                    GRID_SIZE * mCellSize, (mBoardTouchXCoordinate + 1) * mCellSize, mHighlightPaint);
        }

    }

    private int getCellSubGridStartRow(int i)
    {
        return i - (i % SUB_GRID_SIZE);
    }

    private int getCellSubGridStartColumn(int i)
    {
        return getCellSubGridStartRow(i);
    }


    private void colorColumnCells(Canvas canvas)
    {
        if(isTouchPositionInsideGrid())
        {
            int startColumn = getCellSubGridStartColumn(mBoardTouchXCoordinate);
            canvas.drawRect((mBoardTouchYCoordinate * mCellSize), 0,
                    (mBoardTouchYCoordinate + 1) * mCellSize, startColumn * mCellSize, mHighlightPaint);

            canvas.drawRect( (mBoardTouchYCoordinate * mCellSize),(startColumn + SUB_GRID_SIZE) * mCellSize,
                    (mBoardTouchYCoordinate + 1) * mCellSize, GRID_SIZE * mCellSize, mHighlightPaint);
        }
    }
    private void colorSubGridCells(Canvas canvas)
    {
        int startRow = getCellSubGridStartRow(mBoardTouchYCoordinate);
        int startColumn = getCellSubGridStartColumn(mBoardTouchXCoordinate);

        if(isTouchPositionInsideGrid())
        {
            canvas.drawRect((startRow) * mCellSize, (startColumn) * mCellSize,
                    (SUB_GRID_SIZE + startRow) * mCellSize, (SUB_GRID_SIZE + startColumn) * mCellSize, mHighlightPaint);
        }
    }

    public boolean isTouchPositionInsideGrid()
    {
        return mBoardTouchXCoordinate != Integer.MAX_VALUE && mBoardTouchYCoordinate != Integer.MAX_VALUE;
    }

    private void drawBoardNumbers(Canvas canvas) {

        Rect bounds = new Rect();
        float numWidth, numHeight;
        float xOffset, yOffset;

        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int column = 0; column < GRID_SIZE; ++column) {
                if (mBoard[row][column].getNumber() != 0) {

                    String num = String.valueOf(mBoard[row][column].getNumber());
                    mNumberPaint.getTextBounds(num, 0, num.length(), bounds);
                    numWidth = bounds.width();
                    numHeight = bounds.height();

                    xOffset = mCellSize / 2 - numWidth / 2;
                    yOffset = mCellSize / 2 + numHeight / 2;

                    if (num.equals("1"))
                        xOffset -= numWidth / 4;


                    if (!mBoard[row][column].isOriginal()) {
                        canvas.drawText(num, column * mCellSize + xOffset, row * mCellSize + yOffset, mSolvedCellsPaint);

                    }else {
                        canvas.drawText(num, column * mCellSize + xOffset, row * mCellSize + yOffset, mNumberPaint);
                    }


                }
            }
        }

    }

    public void clearBoard()
    {
        for (int r = 0; r < GRID_SIZE; ++r)
            for (int c = 0; c < GRID_SIZE; ++c)
                mBoard[r][c] = new BoardCell();

    }

    public void updateBoard(int num)
    {
        if(isTouchPositionInsideGrid())
        {
            BoardCell boardCell = mBoard[mBoardTouchXCoordinate][mBoardTouchYCoordinate];
            mBoard[mBoardTouchXCoordinate][mBoardTouchYCoordinate] = new BoardCell(num, true);
            mPreviousMoves.add(new Pair<>(new Pair<>(mBoardTouchXCoordinate, mBoardTouchYCoordinate), boardCell));
            invalidate();
        }
    }

    public void undo()
    {
        if(isTouchPositionInsideGrid())
        {
            if(!mPreviousMoves.isEmpty())
            {
                Pair<Pair<Integer, Integer>, BoardCell> pair = mPreviousMoves.pop();
                int x = pair.first.first, y = pair.first.second;
                mBoardTouchXCoordinate = x;
                mBoardTouchYCoordinate = y;
                mBoard[x][y] = pair.second;

                invalidate();
            }
        }
    }
    
}