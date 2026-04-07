package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
        scoreManager = new ScoreManager();
        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        font.getData().setScale(3);

        layout = new GlyphLayout();

        highscoreSound = Gdx.audio.newSound(Gdx.files.internal("HighScoreSound.wav"));

    }

    public void renderScore(SpriteBatch batch) {
        String scoreText = "Score: " + scoreManager.getScore();
        String highScoreText = "Highscore: " + scoreManager.getHighScore();

        layout.setText(font, scoreText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() - 100;
        font.draw(batch, scoreText, x, y);

        font.draw(batch, highScoreText, 20, Gdx.graphics.getHeight() - 20);

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
