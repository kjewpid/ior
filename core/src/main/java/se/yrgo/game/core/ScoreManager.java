package se.yrgo.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ScoreManager {
    private int score;
    private int highScore;
    private Preferences prefs;

    public ScoreManager() {
        this.score = 0;

        prefs = Gdx.app.getPreferences("MyGamePrefs");
        this.highScore = prefs.getInteger("highscore", 0);
    }

    public void incrementPoint() {
        score++;
    }

    public void checkHighScore() {
        if (score > highScore) {
            highScore = score;

            prefs.putInteger("highscore", highScore);
            prefs.flush(); 
        }
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public void resetScore() {
        score = 0;
    }
}