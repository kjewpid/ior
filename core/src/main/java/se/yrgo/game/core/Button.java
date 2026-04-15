package se.yrgo.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;

public class Button {

    private float x, y, width, height;

    private Texture normalTexture;
    private Texture hoverTexture;

    private boolean hovered;

public Button(float x, float y,
              String normalImg, String hoverImg) {

    this.x = x;
    this.y = y;

    normalTexture = new Texture(Gdx.files.internal(normalImg));
    hoverTexture = new Texture(Gdx.files.internal(hoverImg));

    this.width = normalTexture.getWidth();
    this.height = normalTexture.getHeight();
}

    public void update() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        hovered = mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public boolean isClicked() {
        return hovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    public void render(SpriteBatch batch) {
        batch.draw(
                hovered ? hoverTexture : normalTexture,
                x, y, width, height);
    }

    public void dispose() {
        normalTexture.dispose();
        hoverTexture.dispose();
    }
}