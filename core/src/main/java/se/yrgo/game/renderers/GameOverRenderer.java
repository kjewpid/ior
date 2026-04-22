package se.yrgo.game.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Pixmap;

public class GameOverRenderer {
    private Texture skull;
    private Texture gameOverInstructions;
    private Texture pixel;

    private float scale = 0.2f;
    private float targetScale = 1.0f;
    private float zoomSpeed = 1.5f;
    private float time = 0f;
    private boolean zoomFinished = false;

    private float instructionAlpha = 0f;
    private boolean instructionStarted = false;
    private float instructionTime = 0f;

    public GameOverRenderer() {

    }

    public void loadAssets() {
        skull = new Texture("GameOverScreen/Skull.png");
        gameOverInstructions = new Texture("GameOverScreen/GameOverInstructions.png");
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        pixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float delta) {
        time += delta;

        if (!zoomFinished) {
            scale = MathUtils.lerp(scale, targetScale, delta * zoomSpeed);

            if (Math.abs(scale - targetScale) < 0.01f) {
                scale = targetScale;
                zoomFinished = true;
            }
        }

        if (zoomFinished) {
            instructionTime += delta;
            instructionStarted = true;

            instructionAlpha = MathUtils.lerp(instructionAlpha, 1f, delta * 2f);
        }
    }

    public void render(SpriteBatch batch, float worldWidth, float worldHeight) {

        float pulse = 1f;

        if (zoomFinished) {
            pulse = 1f + MathUtils.sin(time * 2f) * 0.03f;
        }

        float finalScale = scale * pulse;

        float skullW = skull.getWidth() * 0.5f * finalScale;
        float skullH = skull.getHeight() * 0.5f * finalScale;

        float centerX = worldWidth / 2f;
        float centerY = worldHeight / 2f;

        float fadeDuration = 1.0f;
        float fadeProgress = MathUtils.clamp(time / fadeDuration, 0f, 1f);

        fadeProgress = fadeProgress * fadeProgress;
        batch.setColor(0f, 0f, 0f, fadeProgress);
        batch.draw(pixel, 0, 0, worldWidth, worldHeight);
        batch.setColor(1f, 1f, 1f, 1f);

        batch.setColor(1f, 1f, 1f, fadeProgress);
        batch.draw(skull, centerX - skullW / 2f, centerY - skullH / 2f, skullW, skullH);
        batch.setColor(1f, 1f, 1f, 1f);

        float instructionScale = 0.6f;

        if (instructionStarted) {
            instructionScale += MathUtils.sin(instructionTime * 2f) * 0.03f;
        }

        float iw = gameOverInstructions.getWidth() * instructionScale;
        float ih = gameOverInstructions.getHeight() * instructionScale;


        batch.setColor(1f, 1f, 1f, instructionAlpha);

        batch.draw(gameOverInstructions, centerX - iw / 2f, 60, iw, ih);

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        safeDispose(skull);
        safeDispose(gameOverInstructions);
    }

    public void safeDispose(Texture texture) {
        if (texture != null) {
            texture.dispose();
        }
    }

    public void reset() {
        scale = 0.2f;
        targetScale = 1.0f;
        time = 0f;

        instructionTime = 0f;
        instructionAlpha = 0f;
        instructionStarted = false;

        zoomFinished = false;
    }
}

