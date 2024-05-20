package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PowerBar {
    private Texture barTexture;
    private Rectangle barBounds;
    private float power;
    private final float maxPower;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    public PowerBar(float maxPower) {
        this.maxPower = maxPower;
        this.power = 0;

        // Create a pixmap for the bar
        Pixmap pixmap = new Pixmap(200, 30, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        barTexture = new Texture(pixmap);
        pixmap.dispose();

        barBounds = new Rectangle(10, 10, 200, 30); // Position at bottom-left corner
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    public void setPower(float power) {
        this.power = Math.max(0, Math.min(power, maxPower));
    }

    public float getPower() {
        return power;
    }

    public void render() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(barTexture, barBounds.x, barBounds.y, barBounds.width * (power / maxPower), barBounds.height);
        spriteBatch.end();
    }

    public void dispose() {
        barTexture.dispose();
        spriteBatch.dispose();
    }
}
