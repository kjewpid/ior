package se.yrgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

public class Game extends ApplicationAdapter {
    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;

    @Override
    public void create() {
        // För att initiera resurser, t.ex. texturer, bilder, fonts etc
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);  // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();
        velocity += gravity * delta;
        characterY += velocity * delta;
    }

    @Override
    public void dispose() {
        // För att stänga resurser
    }
}

