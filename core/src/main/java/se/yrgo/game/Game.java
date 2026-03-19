package se.yrgo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;

public class Game extends ApplicationAdapter {
    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;
    private boolean gameStarted = false;
    private SpriteBatch batch;
    
    // StartImage
    private float buttonWidth = 400;
    private float buttonHeight = 200;
    private float buttonX, buttonY;
    private Texture startImage;
    
    // Karaktär
    private Texture characterImage;
    private float startX = -120;
    private float characterX = 400;
    private float flySpeed = 600;
    
    // Poäng
    private ScoreManager scoreManager;
    private BitmapFont font;
    private GlyphLayout layout;
    private Sound highscoreSound;
    private boolean newHighscorePlayed = false;
    @Override

    public void create() {
        batch = new SpriteBatch();
        characterImage = new Texture(Gdx.files.internal("Character.png"));
        startImage = new Texture(Gdx.files.internal("StartImage.png"));
        buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2;

        scoreManager = new ScoreManager();
        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        font.getData().setScale(3);

        layout = new GlyphLayout();

        highscoreSound = Gdx.audio.newSound(Gdx.files.internal("HighScoreSound.wav"));
    }

    public void score() {
        String scoreText = "Score: " + scoreManager.getScore();
        String highScoreText = "Highscore: " + scoreManager.getHighScore();

        layout.setText(font, scoreText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() - 100;
        font.draw(batch, scoreText, x, y);

        font.draw(batch, highScoreText, 20, Gdx.graphics.getHeight() - 20);

        if (Gdx.input.justTouched()) {
            scoreManager.incrementPoint();
        }

        /*
         * Lägg till denna när vi har skaffat hinder, för att ge poäng för hinder
         * istället
         * if (obstacleX < characterX && !obstacleCounted) {
         * scoreManager.incrementPoint();
         * obstacleCounted = true;
         * }
         */
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1); // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameStarted = true;
            jump();
            newHighscorePlayed = false;
            scoreManager.resetScore();
        }


        if (!gameStarted) {
            batch.begin();
            batch.draw(startImage, buttonX, buttonY, buttonWidth, buttonHeight);
            batch.end();
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        // Kolla input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            jump();
        }

        // Karaktär-kod
        float screenHeight = Gdx.graphics.getHeight();
        if (startX < characterX) {
            startX += flySpeed * delta;
            if (startX > characterX) {
                startX = characterX;
            }
        }

        if (characterY > screenHeight) {
            characterY = screenHeight;
            velocity = 0;
        }

        if (characterY < 0) {
            characterY = 0;
            if (velocity < 0) {
                velocity = 0;
            }
        }

        if (scoreManager.getScore() == scoreManager.getHighScore()
                && scoreManager.getScore() > 0
                && !newHighscorePlayed) {

            highscoreSound.play(0.7f);
            newHighscorePlayed = true;
        }

        batch.begin();
        batch.draw(characterImage, startX - 30, characterY - 30, 120, 120);
        score();
        batch.end();

        velocity += gravity * delta;
        characterY += velocity * delta;
    }

    // NY METOD FÖR HOPP
    private void jump() {
        velocity = 600;
    }

    @Override
    public void dispose() {
        batch.dispose();
        characterImage.dispose();
        startImage.dispose();
        highscoreSound.dispose();
    }
}
