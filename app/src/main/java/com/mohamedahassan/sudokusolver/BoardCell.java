package com.mohamedahassan.sudokusolver;

public class BoardCell {

    private int mNumber;
    private boolean mOriginal;

    public BoardCell()
    {

    }
    public BoardCell(int number, boolean original)
    {
        mNumber = number;
        mOriginal = original;
    }
    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public boolean isOriginal() {
        return mOriginal;
    }

    public void setOriginal(boolean original) {
        mOriginal = original;
    }

}
