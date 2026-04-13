package se.yrgo.game.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.w3c.dom.Text;
import se.yrgo.game.entities.Layer;

import java.util.ArrayList;
import java.util.List;

public class BackgroundRenderer {
    private Texture backdrop;
    private Texture background_trees_back;
    private Texture background_trees_front;
    private Texture background_fog;

    private Texture background_trees;
    private Texture background_trees_fog;

    private Texture middle_trees;
    private Texture middle_fog;

    private Texture front_trees;
    private Texture front_ground;
    private Texture front_fog;

    private Texture vignette;
    private Texture shader;

    private List<Layer> layers = new ArrayList<>();

    public BackgroundRenderer() {

    }

    public void loadAssets() {
        backdrop = new Texture("background/backdrop.png");
        background_trees_back = new Texture("background/background_back1.png");
        background_trees_front = new Texture("background/background_back2.png");
        background_fog = new Texture("background/background_fog.png");

        background_trees = new Texture("background/back_trees.png");
        background_trees_fog = new Texture("background/back_trees_fog.png");

        middle_trees = new Texture("background/middle_trees.png");
        middle_fog = new Texture("background/middle_fog.png");

        front_trees = new Texture("background/trees_front.png");
        front_ground = new Texture("background/mark_front.png");
        front_fog = new Texture("background/fog_front.png");

        shader = new Texture("background/shader.png");
        vignette = new Texture("background/vignette.png");
    }

    public void setupLayers() {

        layers.add(new Layer(backdrop, 0f, 0, false, 1));

        layers.add(new Layer(background_trees_back, 0.04f, 0, true, 2));
        layers.add(new Layer(background_trees_front, 0.08f, 0, true, 2));

        // Back fogs
        Layer fog1 = new Layer(background_fog, 0.03f, -50, false, 6);
        fog1.setOffsetX(0);
        layers.add(fog1);

        Layer fog2 = new Layer(background_fog, 0.03f, -40, false, 6);
        fog2.setOffsetX(400);
        layers.add(fog2);

        Layer fog3 = new Layer(background_fog, 0.03f, -60, false, 6);
        fog3.setOffsetX(800);
        layers.add(fog3);

        layers.add(new Layer(background_trees, 0.15f, 0, true, 2));

        // Mid fogs
        Layer midFog1 = new Layer(background_trees_fog, 0.12f, -20, false, 6);
        midFog1.setOffsetX(0);
        layers.add(midFog1);

        Layer midFog2 = new Layer(background_trees_fog, 0.12f, -10, false, 6);
        midFog2.setOffsetX(500);
        layers.add(midFog2);

        layers.add(new Layer(middle_trees, 0.3f, 0, true, 2));

        layers.add(new Layer(middle_fog, 0.3f, 0, false, 6));

        Layer frontTrees = new Layer(front_trees, 0.8f, -20, true, 2);
        frontTrees.setScale(1.1f);
        layers.add(frontTrees);

        Layer frontGround = new Layer(front_ground, 0.8f, 100, true, 2);
        frontGround.setScale(1.1f);
        layers.add(frontGround);

        // Front fogs
        Layer frontFog1 = new Layer(front_fog, 0.5f, -5, false, 6);
        frontFog1.setOffsetX(0);
        layers.add(frontFog1);

        Layer frontFog2 = new Layer(front_fog, 0.5f, 0, false, 6);
        frontFog2.setOffsetX(300);
        layers.add(frontFog2);

        Layer frontFog3 = new Layer(front_fog, 0.5f, -10, false, 6);
        frontFog3.setOffsetX(600);
        layers.add(frontFog3);

        layers.add(new Layer(shader, 0f, 0, false, 1));
        layers.add(new Layer(vignette, 0f, 0, false, 1));
    }

    public void update(float delta, float cameraSpeed) {

        for (Layer layer : layers) {
            layer.setOffsetX(
                layer.getOffsetX() +
                    cameraSpeed * layer.getSpeed() * delta
            );

            float width = layer.getTexture().getWidth() * layer.getScale();

            if (layer.isRepeat() && layer.getOffsetX() > width) {
                layer.setOffsetX(layer.getOffsetX() - width);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Layer layer : layers) {
            float width = layer.getTexture().getWidth();
            float height = layer.getTexture().getHeight();
            float scale = layer.getScale();

            float scaledWidth = width * scale;
            float scaledHeight = height * scale;

            if (layer.getTexture() == shader) {
                batch.setColor(1f, 1f, 1f, 0.4f);
                batch.draw(shader, 0, 0);
                batch.setColor(1f, 1f, 1f, 1f);
                continue;
            }

            if (layer.getInstances() > 1 && !layer.isRepeat()) {
                for (int i = 0; i < layer.getInstances(); i++) {
                    float x = -layer.getOffsetX() + i * scaledWidth;

                    float y = layer.getY();
                    if (scale != 1f) {
                        y -= (scaledHeight - height);
                    }

                    batch.draw(layer.getTexture(), x, y, scaledWidth, scaledHeight);
                }
            } else if (layer.isRepeat()) {
                float y = layer.getY();
                if (scale != 1f) {
                    y -= (scaledHeight - height);
                }

                batch.draw(layer.getTexture(), -layer.getOffsetX(), y, scaledWidth, scaledHeight);
                batch.draw(layer.getTexture(), -layer.getOffsetX() + scaledWidth, y, scaledWidth, scaledHeight);
            } else {
                float y = layer.getY();
                if (scale != 1f) {
                    y -= (scaledHeight - height);
                }

                batch.draw(layer.getTexture(), 0, y, scaledWidth, scaledHeight);
            }
        }
    }
}

