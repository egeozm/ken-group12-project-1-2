package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * The WinLabel class represents a label displayed when the player wins the game.
 * It handles the rendering and visibility of the "YOU WIN!" message.
 */
public class WinLabel {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private boolean visible;
    private String text;

    /**
     * Constructs a WinLabel.
     * Initializes the font, camera, and sets the label to be initially invisible.
     */
    public WinLabel(String text) {
        spriteBatch = new SpriteBatch();
        this.text = text;
        // Generate a bitmap font with a specific size
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/skin/Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;  // Set font size
        font = generator.generateFont(parameter);
        generator.dispose();

        font.setColor(Color.RED);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        visible = false;
    }

    /**
     * Makes the "YOU WIN!" label visible.
     */
    public void show() {
        visible = true;
    }

    /**
     * Renders the "YOU WIN!" label on the screen if it is visible.
     */
    public void render() {
        if (!visible) return;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, text, (float) Gdx.graphics.getWidth() / 2 - 150, (float) Gdx.graphics.getHeight() / 2 + 30);
        spriteBatch.end();
    }

    /**
     * Disposes of the resources used by the WinLabel.
     * This should be called when the WinLabel is no longer needed to free up resources.
     */
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}
