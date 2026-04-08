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
        background_fog = new Texture("background/fog_back.png");

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

        layers.add(new Layer(background_trees_back, 0.05f, 0, true, 2));
        layers.add(new Layer(background_trees_front, 0.08f, 0, true, 2));

        layers.add(new Layer(background_fog, 0.03f, -50, false, 6));

        layers.add(new Layer(background_trees, 0.15f, 0, true, 2));
        layers.add(new Layer(background_trees_fog, 0.12f, 0, false, 5));

        layers.add(new Layer(middle_trees, 0.3f, 0, true, 2));
        layers.add(new Layer(middle_fog, 0.25f, 0, false, 6));

        layers.add(new Layer(front_trees, 0.6f, -20, true, 2));
        layers.add(new Layer(front_ground, 1.0f, 0, true, 2));
        layers.add(new Layer(front_fog, 0.5f, 0, false, 8));
    }

    public void update(float delta, float cameraSpeed) {
        for (Layer layer : layers) {
            layer.setOffsetX(layer.getOffsetX() + cameraSpeed * layer.getSpeed() * delta);

            float width = layer.getTexture().getWidth();
            if (layer.getOffsetX() > width) {
                layer.setOffsetX(layer.getOffsetX() - width);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Layer layer : layers) {
            float width = layer.getTexture().getWidth();

            if (layer.getInstances() > 1 && !layer.isRepeat()) {
                for (int i = 0; i < layer.getInstances(); i++) {
                    float x = -layer.getOffsetX() + i * width;
                    batch.draw(layer.getTexture(), x, layer.getY());
                }
            } else if (layer.isRepeat()) {
                batch.draw(layer.getTexture(), -layer.getOffsetX(), layer.getY());
                batch.draw(layer.getTexture(), -layer.getOffsetX() + width, layer.getY());
            } else {
                batch.draw(layer.getTexture(), 0, layer.getY());
            }
        }
    }
}

