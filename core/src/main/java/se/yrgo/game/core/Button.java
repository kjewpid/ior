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

    public Button(float x, float y, float width, float height,
                  String normalImg, String hoverImg) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        normalTexture = new Texture(Gdx.files.internal(normalImg));
        hoverTexture = new Texture(Gdx.files.internal(hoverImg));
    }

    public void update() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        hovered =
                mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public boolean isClicked() {
        return hovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    public void render(SpriteBatch batch) {
        batch.draw(
                hovered ? hoverTexture : normalTexture,
                x, y, width, height
        );
    }

    public void dispose() {
        normalTexture.dispose();
        hoverTexture.dispose();
    }
}