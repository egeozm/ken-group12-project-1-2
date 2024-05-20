package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class GolfHole {
    private Sprite sprite;
    private SpriteBatch spriteBatch;
    private Vector3 position;
    public void setPosition(float x, float y, float z) {
        position = new Vector3(x, y, z);
    }
    public GolfHole(String texturePath, double[][] heightMap, int width, int height, String[][] obstacles) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);
        spriteBatch = new SpriteBatch();
        // Set random position for the hole
    }

    public void render() {
        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();
    }

    public Vector3 getPosition() {
        return position;
    }

    public void dispose() {
        sprite.getTexture().dispose();
        spriteBatch.dispose();
    }
}