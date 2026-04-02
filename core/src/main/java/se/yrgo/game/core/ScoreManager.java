package se.yrgo.game.core;

public class ScoreManager {
    private int score;
    private int highScore;

    public ScoreManager() {
        this.score = 0;
        this.highScore = 0;
    }

    public void incrementPoint() {
        score++;
        if (score > highScore) {
            highScore = score;
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
