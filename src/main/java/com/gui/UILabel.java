package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UILabel {
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private String text;
    private OrthographicCamera camera;

    public UILabel(String text) {
        this.text = text;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, text, 10, 30);
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}