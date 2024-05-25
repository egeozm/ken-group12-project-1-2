package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * The UILabel class represents a user interface label for displaying text on the screen.
 * It handles the rendering and updating of the label text.
 */
public class UILabel {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private String text;
    private final OrthographicCamera camera;

    /**
     * Constructs a UILabel with the specified initial text.
     *
     * @param text the initial text to be displayed on the label
     */
    public UILabel(String text, Vector3 position) {
        this.text = text;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(/*camera.viewportWidth / 2, camera.viewportHeight / 2, 0*/ position);
        camera.update();
    }

    /**
     * Sets the text to be displayed on the label.
     *
     * @param text the new text to be displayed
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Renders the label on the screen.
     */
    public void render() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, text, 10, 30);
        spriteBatch.end();
    }

    /**
     * Disposes of the resources used by the UILabel.
     * This should be called when the UILabel is no longer needed to free up resources.
     */
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}
