package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CharacterRenderer {
    private TextureAtlas beeBodyAtlas;
    private TextureAtlas frontWingAtlas;

    private TextureAtlas backWingAtlas;
    private Animation<TextureRegion> bodyAnimation;
    private Animation<TextureRegion> frontWingAnimation;
    private Animation<TextureRegion> backWingAnimation;

    private TextureAtlas deadBodyAtlas;
    private TextureAtlas deadFrontWingAtlas;
    private TextureAtlas deadBackWingAtlas;

    private Animation<TextureRegion> deadBodyAnimation;
    private Animation<TextureRegion> deadFrontWingAnimation;
    private Animation<TextureRegion> deadBackWingAnimation;

    private TextureRegion bodyFrame;
    private TextureRegion frontWingFrame;
    private TextureRegion backWingFrame;

    public CharacterRenderer() {
    }

    public void loadAssets() {
        beeBodyAtlas = new TextureAtlas(Gdx.files.internal("bee/bee.atlas"));
        frontWingAtlas = new TextureAtlas(Gdx.files.internal("bee/wings_front.atlas"));
        backWingAtlas = new TextureAtlas(Gdx.files.internal("bee/wings_back.atlas"));

        bodyAnimation = new Animation<>(0.07f, beeBodyAtlas.getRegions(), Animation.PlayMode.LOOP);
        frontWingAnimation = new Animation<>(0.05f, frontWingAtlas.getRegions(), Animation.PlayMode.LOOP);
        backWingAnimation = new Animation<>(0.05f, backWingAtlas.getRegions(), Animation.PlayMode.LOOP);

        deadBodyAtlas = new TextureAtlas(Gdx.files.internal("bee/bee_dead.atlas"));
        deadFrontWingAtlas = new TextureAtlas(Gdx.files.internal("bee/bee_dead_front_wings.atlas"));
        deadBackWingAtlas = new TextureAtlas(Gdx.files.internal("bee/bee_dead_back_wings.atlas"));

        deadBodyAnimation = new Animation<>(0.08f, deadBodyAtlas.getRegions(), Animation.PlayMode.NORMAL);
        deadFrontWingAnimation = new Animation<>(0.08f, deadFrontWingAtlas.getRegions(), Animation.PlayMode.LOOP);
        deadBackWingAnimation = new Animation<>(0.08f, deadBackWingAtlas.getRegions(), Animation.PlayMode.LOOP);
    }

    public void renderBee(boolean isDying, float stateTime, SpriteBatch batch, float characterStartX, float characterY) {
        if (isDying) {
            bodyFrame = deadBodyAnimation.getKeyFrame(stateTime);
            frontWingFrame = deadFrontWingAnimation.getKeyFrame(stateTime * 0.4f);
            backWingFrame = deadBackWingAnimation.getKeyFrame(stateTime * 0.4f);
        } else {
            bodyFrame = bodyAnimation.getKeyFrame(stateTime);
            frontWingFrame = frontWingAnimation.getKeyFrame(stateTime);
            backWingFrame = backWingAnimation.getKeyFrame(stateTime);
        }

        float width = 120;
        float height = 120;

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(backWingFrame, characterStartX - width / 2, characterY - height / 2, width, height);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(bodyFrame, characterStartX - width / 2, characterY - height / 2, width, height);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(frontWingFrame, characterStartX - width / 2, characterY - height / 2, width, height);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {
        if (beeBodyAtlas != null) beeBodyAtlas.dispose();
        if (frontWingAtlas != null) frontWingAtlas.dispose();
        if (backWingAtlas != null) backWingAtlas.dispose();
        if (deadBodyAtlas != null) deadBodyAtlas.dispose();
        if (deadFrontWingAtlas != null) deadFrontWingAtlas.dispose();
        if (deadBackWingAtlas != null) deadBackWingAtlas.dispose();
    }
}
