package com.pw.qi1siwole.game2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Game2048Layout.OnGame2048LayoutListener {

    private Game2048Layout mGame2048Layout;
    private Button mRestartButton;
    private TextView mScoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();
    }

    /*
     * 初始化View
     */
    private void initViews() {
        mGame2048Layout = (Game2048Layout)findViewById(R.id.game_2048_layout);
        mRestartButton = (Button)findViewById(R.id.restart_button);
        mScoreTextView = (TextView)findViewById(R.id.score_text);
    }

    /*
     * 设置Event
     */
    private void initEvents() {
        mGame2048Layout.setOnGame2048LayoutListener(this);
        mRestartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mRestartButton) {
            mGame2048Layout.restartGame();
        }
    }

    @Override
    public void onScoreChanged(int score) {
        mScoreTextView.setText(String.format("Score: %4d", score));
    }

    @Override
    public void onGameOver() {
        showTip(R.string.toast_game_over);
    }

    private void showTip(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
