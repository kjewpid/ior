package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import se.yrgo.game.entities.Flower;

import java.util.ArrayList;

public class FlowerRenderer {
    private TextureAtlas flowerAtlas;
    private TextureAtlas flowerGlowAtlas;
    private Animation<TextureRegion> flowerAnimation;
    private Animation<TextureRegion> flowerGlowAnimation;
    private ArrayList<Flower> flowers = new ArrayList<>();
    private Texture flowerImage;
    private Sound flowerSound;

    public FlowerRenderer() {

    }

    public void loadAssets() {
        flowerImage = new Texture("flower.png");

        flowerAtlas = new TextureAtlas(Gdx.files.internal("flower/flower.atlas"));
        flowerGlowAtlas = new TextureAtlas(Gdx.files.internal("flower/glow.atlas"));
        flowerAnimation = new Animation<>(0.1f, flowerAtlas.getRegions(), Animation.PlayMode.LOOP);
        flowerGlowAnimation = new Animation<>(0.1f, flowerGlowAtlas.getRegions(), Animation.PlayMode.LOOP);
        flowerSound = Gdx.audio.newSound(Gdx.files.internal("flower/pickUpFlowerSound.mp3"));
    }

    public void renderFlowers(SpriteBatch batch, float stateTime) {
        TextureRegion flowerFrame;
        TextureRegion glowFrame;

        float width = 50;
        float height = 50;

        for (Flower f : flowers) {
            flowerFrame = flowerAnimation.getKeyFrame(stateTime);
            glowFrame = flowerGlowAnimation.getKeyFrame(stateTime);

            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.draw(glowFrame, f.getX() - width / 2, f.getY() - height / 2, width, height);

            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            // batch.setColor(1f, 0.95f, 0.95f, 2f);
            batch.draw(flowerFrame, f.getX() - width / 2, f.getY() - height / 2, width, height);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }


    public void spawnFlowers(float worldHeight, float worldWidth) {
        if (Math.random() < 0.02) { // ca 2% chans varje frame
            float y = 50 + (float) (Math.random() * (worldHeight - 100));

            float flowerWidth = 100f;
            float x = worldWidth + flowerWidth;
            flowers.add(new Flower(x, y));
        }
    }

    public void updateFlowers(float delta, float speed, float worldHeight, float worldWidth) {
        spawnFlowers(worldHeight, worldWidth);
        for (int i = flowers.size() - 1; i >= 0; i--) {
            Flower f = flowers.get(i);
            f.update(delta, speed);
            if (f.getX() < -50 || f.isCollected()) {
                flowers.remove(i);
            }
        }
    }

    private void removeOffScreenFlower(Flower flower) {
        if (flower.getX() < -50 || flower.isCollected()) {
            flowers.remove(flower);
        }
    }

    public ArrayList<Flower> getFlowers() {
        return flowers;
    }

    public Sound getFlowerSound() {
        return flowerSound;
    }

    public void clearFlowers() {
        flowers.clear();
    }

    public void dispose() {
        if (flowerImage != null) flowerImage.dispose();
        if (flowerAtlas != null) flowerAtlas.dispose();
        if (flowerGlowAtlas != null) flowerGlowAtlas.dispose();
        if (flowerSound != null) flowerSound.dispose();
    }
}
