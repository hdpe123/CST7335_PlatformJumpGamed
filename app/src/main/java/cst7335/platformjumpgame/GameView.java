package cst7335.platformjumpgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Thread gameThread;
    private boolean isPlaying = true;

    private Player player;
    private List<Platform> platforms;
    private boolean isGameOver = false;

    public GameView(Context context) {
        super(context);
        player = new Player();
        platforms = new ArrayList<>();
        platforms.add(new Platform(500, 600));

        resume();  // Start the game loop as soon as GameView is created
        getHolder().addCallback(this);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if necessary
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }



    private void update() {
        player.update();
        Iterator<Platform> iterator = platforms.iterator();
        while (iterator.hasNext()) {
            Platform platform = iterator.next();
            platform.update();
            if (player.collidesWith(platform) && player.isAbove(platform)) {
                player.y = platform.y - 100;  // Position player on top of the platform
                player.velocityY = 0;         // Stop downward motion
            }
            // Handle game over if player falls off the screen
            if (player.y > getHeight()) {
                gameOver();
            }
            // Remove platform if it's off the screen
            if (platform.x + 200 < 0) {
                iterator.remove();
            }
        }
        // Generate new platforms periodically
        if (Math.random() < 0.02) { // 2% chance every frame to spawn a platform
            float yPosition = (float) (400 + Math.random() * 200);  // Random height between 400 and 600
            platforms.add(new Platform(getWidth(), yPosition));
        }

        // Handle game over if player falls off the screen
        if (player.y > getHeight()) {
            gameOver();
        }
    }

    private void gameOver() {
        isPlaying = false;
        isGameOver = true;
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            // Clear screen
            canvas.drawColor(Color.WHITE);

            // Draw player
            canvas.drawRect(player.x, player.y, player.x + 100, player.y + 100, new Paint(Color.RED));

            // Draw platforms
            Paint platformPaint = new Paint(Color.GREEN);
            for (Platform platform : platforms) {
                canvas.drawRect(platform.x, platform.y, platform.x + 200, platform.y + 50, platformPaint);
            }

            // Draw game over text
            if (isGameOver) {
                Paint gameOverPaint = new Paint();
                gameOverPaint.setTextSize(50);
                gameOverPaint.setColor(Color.BLACK);
                canvas.drawText("Game Over! Tap to restart.", getWidth() / 2 - 250, getHeight() / 2, gameOverPaint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }


    public class Player {
        public float x = 100, y = 500; // Default starting position
        public float velocityY = 0;
        private static final float GRAVITY = 1f;
        private static final float JUMP_STRENGTH = -15f;

        public void update() {
            y += velocityY;
            velocityY += GRAVITY;

            // TODO: Add collision detection with ground or platforms here
        }

        public boolean collidesWith(Platform platform) {
            return x < platform.x + 200 &&
                    x + 100 > platform.x &&
                    y + 100 > platform.y &&
                    y < platform.y + 50;
        }

        public boolean isAbove(Platform platform) {
            return x + 100 > platform.x &&
                    x < platform.x + 200 &&
                    y + 100 <= platform.y;
        }
        public void jump() {
            velocityY = JUMP_STRENGTH;
        }
    }


    public class Platform {
        public float x, y;
        private static final float SPEED = 5f;

        public Platform(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void update() {
            x -= SPEED;
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isGameOver) {
                restartGame();
            } else {
                player.jump();
                if (!isPlaying) {
                    isPlaying = true;
                }
            }
        }
        return true;
    }

    private void restartGame() {
        player = new Player();
        platforms.clear();
        platforms.add(new Platform(500, 600));
        isPlaying = true;
        isGameOver = false;
    }


    private void sleep() {
        try {
            Thread.sleep(17);  // Cap the game loop to roughly 60 frames per second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

