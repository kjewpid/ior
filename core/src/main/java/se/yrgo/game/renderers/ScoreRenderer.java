package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import se.yrgo.game.core.ScoreManager;

public class ScoreRenderer {
    private ScoreManager scoreManager;
    private BitmapFont font;
    private GlyphLayout layout;
    private int highScoreAtStart;
    private Sound highscoreSound;
    private boolean newHighscorePlayed;

    public ScoreRenderer(ScoreManager scoreManager) {
        this.newHighscorePlayed = false;
        this.scoreManager = scoreManager;
    }

    public void loadAssets() {
        font = new BitmapFont(Gdx.files.internal("font/fontWOD.fnt"));

        font.getData().setScale(0.7f);
        // font.setColor(Color.WHITE);

        layout = new GlyphLayout();

        highscoreSound = Gdx.audio.newSound(Gdx.files.internal("HighScoreSound.wav"));

    }

    public void renderScore(SpriteBatch batch, float worldWidth, float worldHeight) {
        String scoreText = "Score: " + scoreManager.getScore();
        String highScoreText = "Highscore: " + scoreManager.getHighScore();

        layout.setText(font, scoreText);
        float x = (worldWidth - layout.width) / 2;
        font.draw(batch, scoreText, x, worldHeight - 100);

        layout.setText(font, highScoreText);
        font.draw(batch, highScoreText, 20, worldHeight - 20);

        if (scoreManager.getScore() > highScoreAtStart && !newHighscorePlayed) {
            highscoreSound.play(0.7f);
            newHighscorePlayed = true;
        }
    }

    public void resetHighscoreFlag() {
        newHighscorePlayed = false;
        highScoreAtStart = scoreManager.getHighScore();
    }

    public void dispose() {
        font.dispose();
        highscoreSound.dispose();
    }
}
