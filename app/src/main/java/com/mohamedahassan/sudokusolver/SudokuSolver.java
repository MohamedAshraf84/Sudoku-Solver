package com.mohamedahassan.sudokusolver;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuSolver extends AppCompatActivity {

    private int ANIMATION_SPEED = 5 * 15;
    private ConstraintLayout mControls;
    private SudokuBoardView mSudokuBoardView;
    private SudokuPuzzleSolver mSudokuPuzzleSolver;
    private SudokuPuzzleGenerator mSudokuPuzzleGenerator;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView mTvSpeed;
    MenuItem mRandomMenuItem, mUndoMenuItem;
    private ExecutorService mExecutor;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.undo_action)
        {
            mSudokuBoardView.undo();
        }
        else if (item.getItemId() == R.id.generate_random_action) {
            mSudokuBoardView.setBoard(mSudokuPuzzleGenerator.generatePuzzle());
        }
        else if (item.getItemId() == R.id.settings_action) {
            mBottomSheetDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mRandomMenuItem = menu.findItem(R.id.generate_random_action);
        mUndoMenuItem = menu.findItem(R.id.undo_action);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_solver);

        mSudokuBoardView = findViewById(R.id.sudoku_board_view);
        mControls = findViewById(R.id.gl_controls);

        View bottom_layout = LayoutInflater.from(this).inflate(R.layout.bottom_layout, null);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottom_layout);

        SwitchCompat animationSwitch = mBottomSheetDialog.findViewById(R.id.animation_switch_btn);
        Spinner difficultySpinner = mBottomSheetDialog.findViewById(R.id.difficulty_spinner);
        SeekBar seekBar = mBottomSheetDialog.findViewById(R.id.animation_seekbar);
        mTvSpeed = mBottomSheetDialog.findViewById(R.id.speed_tv);


        Button clearBtn = findViewById(R.id.clearBtn);
        Button solveBtn = findViewById(R.id.solveBtn);

        mSudokuPuzzleGenerator = new SudokuPuzzleGenerator(SudokuPuzzleGenerator.EASY);
        mSudokuPuzzleSolver = new SudokuPuzzleSolver(mSudokuBoardView.getSudokuBoard());

        mSudokuPuzzleSolver.setSudokuListener(new SudokuPuzzleSolver.SudokuSolverListener() {
            @Override
            public void onSolverStarted() {
                runOnUiThread(() -> setUIEnabled(mControls, false));

            }
            @Override
            public void onSolverFinished() {
                runOnUiThread(() -> setUIEnabled(mControls, true));

            }
        });

        assert animationSwitch != null;
        animationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSudokuPuzzleSolver.setAnimation(isChecked);
            if(isChecked)
            {
                buttonView.setText(getString(R.string.animation_on));
            }
            else {
                buttonView.setText(getString(R.string.animation_off));
            }
        });

        assert difficultySpinner != null;
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] spinnerData = getResources().getStringArray(R.array.difficulties);
                if (spinnerData[position].equals("Easy")) {
                    mSudokuPuzzleGenerator.setDifficulty(SudokuPuzzleGenerator.EASY);
                }
                else if (spinnerData[position].equals("Medium")) {
                    mSudokuPuzzleGenerator.setDifficulty(SudokuPuzzleGenerator.MEDIUM);
                }
                else {
                    mSudokuPuzzleGenerator.setDifficulty(SudokuPuzzleGenerator.HARD);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assert seekBar != null;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ANIMATION_SPEED = progress * 5;
                mTvSpeed.setText(String.format("%d ms", ANIMATION_SPEED));
                mSudokuPuzzleSolver.setAnimationSpeed(ANIMATION_SPEED);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ConstraintLayout numbers = findViewById(R.id.cl);

        for (int i = 0; i <  numbers.getChildCount(); ++i)
        {
            View child = numbers.getChildAt(i);

            int finalI = i;
            child.setOnClickListener(v -> mSudokuBoardView.updateBoard((finalI + 1) % 10));
        }

        clearBtn.setOnClickListener(v ->  mSudokuBoardView.clearBoard());


        ConstraintLayout cl_parent = findViewById(R.id.cl_sudoku_solver);
        Snackbar snackbar = Snackbar.make(cl_parent, R.string.puzzle_solved_message, Snackbar.LENGTH_LONG);
        snackbar.setAction("dismiss", v -> snackbar.dismiss());

        solveBtn.setOnClickListener(v -> {
            BoardCell [][]board = mSudokuBoardView.getSudokuBoard();

            if(mSudokuPuzzleSolver.getSudokuValidator().isValidSudoku(board))
            {
                mExecutor = Executors.newFixedThreadPool(1);
                mSudokuPuzzleSolver.setBoard(board);
                mExecutor.execute(() -> {
                    if (mSudokuPuzzleSolver.solveSudoku())
                        runOnUiThread(snackbar::show);
                });

            }
            else {
                snackbar.setText(R.string.puzzle_unsolved_message);
                snackbar.show();
            }
        });


    }

    public void setUIEnabled(ViewGroup view, boolean state)
    {
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);

            if (child instanceof ViewGroup)
                setUIEnabled((ViewGroup) child, state);
            else
                child.setEnabled(state);
        }

        mUndoMenuItem.setEnabled(state);
        mRandomMenuItem.setEnabled(state);
    }
}