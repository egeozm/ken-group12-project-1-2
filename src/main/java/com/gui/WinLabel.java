package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class WinLabel {
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private boolean visible;

    public WinLabel() {
        spriteBatch = new SpriteBatch();

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

    public void show() {
        visible = true;
    }

    public void render() {
        if (!visible) return;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, "YOU WIN!", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2 + 30);
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}