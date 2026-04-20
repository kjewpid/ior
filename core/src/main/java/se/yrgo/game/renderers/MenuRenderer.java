package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import se.yrgo.game.core.Button;
import se.yrgo.game.core.Difficulty;
public class MenuRenderer {

    private Texture menuBackground;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    public void load(float screenWidth) {
        menuBackground = new Texture(Gdx.files.internal("Menu/menu.png"));

        float buttonWidth = new Texture(Gdx.files.internal("Menu/Easy.png")).getWidth();
        float centerX = (screenWidth - buttonWidth) / 2;

        easyButton = new Button(centerX, 615,
            "Menu/Easy.png", "Menu/Easy_hover.png");

        mediumButton = new Button(centerX, 410,
            "Menu/Normal.png", "Menu/Normal_hover.png");

        hardButton = new Button(centerX, 170,
            "Menu/Hard.png", "Menu/Hard_hover.png");
    }

    public void update() {
        easyButton.update();
        mediumButton.update();
        hardButton.update();
    }

    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        float scale = 0.98f;

        float width = 750 * scale;
        float height = 950 * scale;

        float x = (screenWidth - width) / 2f;
        float y = (screenHeight - height) / 2f;

        batch.draw(menuBackground, x, y, width, height);

        easyButton.render(batch);
        mediumButton.render(batch);
        hardButton.render(batch);
    }

    public Difficulty checkSelection() {
        if (easyButton.isClicked()) return Difficulty.EASY;
        if (mediumButton.isClicked()) return Difficulty.MEDIUM;
        if (hardButton.isClicked()) return Difficulty.HARD;
        return null;
    }

    public void dispose() {
        menuBackground.dispose();
        easyButton.dispose();
        mediumButton.dispose();
        hardButton.dispose();
    }
}
