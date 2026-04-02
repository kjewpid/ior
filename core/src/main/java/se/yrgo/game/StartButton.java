package se.yrgo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartButton {
    private final float buttonWidth;
    private final float buttonHeight;
    private final float buttonX;
    private final float buttonY;
    private Texture texture;

    public StartButton(float buttonWidth, float buttonHeight, float buttonX, float buttonY) {
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.buttonX = buttonX;
        this.buttonY = buttonY;
    }

    public void loadButton() {
        texture = new Texture(Gdx.files.internal("StartImage.png"));
    }

    public void renderStartButton(SpriteBatch batch) {
        batch.begin();
        batch.draw(texture, buttonX, buttonY, buttonWidth, buttonHeight);
        batch.end();
    }
}
