package se.yrgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Game extends ApplicationAdapter {
    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;

    private ShapeRenderer shaperenderer; // Tilläfllig karaktär


    @Override
    public void create() {
        shaperenderer = new ShapeRenderer();
        // För att initiera resurser, t.ex. texturer, bilder, fonts etc
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);  // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity = 600;
        }

        float screenHeight = Gdx.graphics.getHeight();
        if (characterY > screenHeight) {
            characterY = screenHeight;
            velocity = 0;
        }

        if (characterY < 0) { // Tillfälligt för hindra karaktär att ramla ner utanför skärmen
            characterY = 0;
            if (velocity < 0) {
                velocity = 0;
            }
        }

        float delta = Gdx.graphics.getDeltaTime();
        velocity += gravity * delta;
        characterY += velocity * delta;

        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        shaperenderer.setColor(1, 1, 0, 1);
        shaperenderer.circle(350, characterY, 30);
        shaperenderer.end();


    }

    @Override
    public void dispose() {
        shaperenderer.dispose();
        // För att stänga resurser
    }
}

