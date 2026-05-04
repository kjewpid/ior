package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import se.yrgo.game.core.Button;
import se.yrgo.game.core.Difficulty;

public class MenuRenderer {

    private Texture menuBackground;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    private Difficulty selectedDifficulty = Difficulty.EASY;

    private boolean selected;
    private boolean usingMouse = false;

    public void load(float screenWidth) {
        menuBackground = new Texture(Gdx.files.internal("Menu/menu.png"));

        float buttonWidth = new Texture(Gdx.files.internal("Menu/Easy.png")).getWidth();
        float centerX = (screenWidth - buttonWidth) / 2;

        easyButton = new Button(centerX, 670,
                "Menu/Easy.png", "Menu/Easy_hover.png");

        mediumButton = new Button(centerX, 470,
                "Menu/Normal.png", "Menu/Normal_hover.png");

        hardButton = new Button(centerX, 230,
                "Menu/Hard.png", "Menu/Hard_hover.png");
    }

    public void setSelectedDifficulty(Difficulty difficulty) {
        this.selectedDifficulty = difficulty;
    }

    public void update() {
        easyButton.update();
        mediumButton.update();
        hardButton.update();

        if (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) {
            usingMouse = true;
        }
        // Kolla om keyboard används
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            usingMouse = false;
        }

        // MUS styr
        if (usingMouse) {
            if (easyButton.isHovered()) {
                selectedDifficulty = Difficulty.EASY;
            } else if (mediumButton.isHovered()) {
                selectedDifficulty = Difficulty.MEDIUM;
            } else if (hardButton.isHovered()) {
                selectedDifficulty = Difficulty.HARD;
            }
        }

        // KEYBOARD styr
        if (!usingMouse) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedDifficulty = next(selectedDifficulty);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedDifficulty = previous(selectedDifficulty);
            }
        }
        easyButton.setSelected(selectedDifficulty == Difficulty.EASY);
        mediumButton.setSelected(selectedDifficulty == Difficulty.MEDIUM);
        hardButton.setSelected(selectedDifficulty == Difficulty.HARD);
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
        if (easyButton.isHovered()) {
            selectedDifficulty = Difficulty.EASY;
        } else if (mediumButton.isHovered()) {
            selectedDifficulty = Difficulty.MEDIUM;
        } else if (hardButton.isHovered()) {
            selectedDifficulty = Difficulty.HARD;
        }
        return null;
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    private Difficulty next(Difficulty current) {
        Difficulty[] values = Difficulty.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }

    private Difficulty previous(Difficulty current) {
        Difficulty[] values = Difficulty.values();
        int index = (current.ordinal() - 1 + values.length) % values.length;
        return values[index];
    }

    public void dispose() {
        menuBackground.dispose();
        easyButton.dispose();
        mediumButton.dispose();
        hardButton.dispose();
    }
}