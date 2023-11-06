package cst7335.platformjumpgame;

public class Player {
    public float x = 100f, y = 500f;
    public float velocityY = 0;
    private static final float GRAVITY = 1f;
    private static final float JUMP_STRENGTH = -15f;

    public void update() {
        y += velocityY;
        velocityY += GRAVITY;
        // Add collision detection with ground or platforms here
    }

    public void jump() {
        velocityY = JUMP_STRENGTH;
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
}
