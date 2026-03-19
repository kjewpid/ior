package se.yrgo.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import se.yrgo.game.Game;

public class Lwjgl3Launcher {

    public static void main(String[] args) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(""); // Titel på spel
        config.setWindowedMode(1920, 1080);

        new Lwjgl3Application(new Game(), config);
    }
}
