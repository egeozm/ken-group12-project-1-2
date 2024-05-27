package com.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Null;

import java.util.HashMap;

/**
 * The GameLauncher class is the entry point for the 3D Golf Game.
 * It sets up the application configuration, loads resources, and initializes the main menu.
 */
public class GameLauncher extends Game {
    private Music mainMenuMusic;
    private Music settingsMusic;
    private Music gameMusic;

    /**
     * Initializes the game, loads resources, and sets the initial screen.
     */
    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("assets/skin/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
        pixmap.dispose();
        // Load the music files
        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/skin/background-music.mp3"));
        settingsMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/skin/settings-music.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/skin/game-music.mp3"));
        mainMenuMusic.setVolume(0f);
        settingsMusic.setVolume(0f);
        gameMusic.setVolume(0f);

        // Start playing the main menu music
        mainMenuMusic.setLooping(true);
        mainMenuMusic.play();
        HashMap<String, Double> parameters = initHashMap();
        // Set the initial screen
        setScreen(new MainMenu(this, mainMenuMusic, settingsMusic, gameMusic, parameters));
    }

    /**
     * Initializes and returns a HashMap with default parameters for the game.
     *
     * @return A HashMap containing default game parameters.
     */
    public HashMap<String, Double> initHashMap() {
        HashMap<String, Double> a = new HashMap<>();
        a.put("width", 50.0);
        a.put("height", 50.0);
        a.put("xBall", Double.NaN);
        a.put("zBall", Double.NaN);
        a.put("xHole", Double.NaN);
        a.put("zHole", Double.NaN);
        a.put("treeSpawnRate", 0.00);
        a.put("housesSpawnRate", 0.00);
        a.put("heightCoefficient", 1.0);
        a.put("yBias", 0.0);
        a.put("functionStep", 0.1);
        return a;
    }

    /**
     * Disposes of resources when the game is closed.
     */
    @Override
    public void dispose() {
        // Dispose of the music
        mainMenuMusic.dispose();
        settingsMusic.dispose();
        gameMusic.dispose();
        super.dispose();
    }

    /**
     * The main method to launch the game.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("3D Golf Game");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setResizable(false);
        new Lwjgl3Application(new GameLauncher(), config);
    }
}
