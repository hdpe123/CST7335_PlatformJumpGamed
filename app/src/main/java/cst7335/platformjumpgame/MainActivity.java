package cst7335.platformjumpgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the GameView instance from the layout
        gameView = findViewById(R.id.game_view);

        // Start run() thread
        gameView.resume();
    }
}
