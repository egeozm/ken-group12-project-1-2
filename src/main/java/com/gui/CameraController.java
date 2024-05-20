package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CameraController extends InputAdapter {
    private Camera camera;
    private Vector3 direction = new Vector3();
    private Vector3 right = new Vector3();
    private float speed = 20f;
    private float sensitivity = 0.2f;
    private boolean forward, backward, left, rightMove, up, down;
    private boolean enabled = true;

    public CameraController(Camera camera) {
        this.camera = camera;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCursorCatched(true);
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!enabled) return false;
        switch (keycode) {
            case Input.Keys.W:
                forward = true;
                break;
            case Input.Keys.S:
                backward = true;
                break;
            case Input.Keys.A:
                left = true;
                break;
            case Input.Keys.D:
                rightMove = true;
                break;
            case Input.Keys.SPACE:
                up = true;
                break;
            case Input.Keys.SHIFT_LEFT:
                down = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!enabled) return false;
        switch (keycode) {
            case Input.Keys.W:
                forward = false;
                break;
            case Input.Keys.S:
                backward = false;
                break;
            case Input.Keys.A:
                left = false;
                break;
            case Input.Keys.D:
                rightMove = false;
                break;
            case Input.Keys.SPACE:
                up = false;
                break;
            case Input.Keys.SHIFT_LEFT:
                down = false;
                break;
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!enabled) return false;
        float deltaX = -Gdx.input.getDeltaX() * sensitivity;
        float deltaY = -Gdx.input.getDeltaY() * sensitivity;

        camera.direction.rotate(camera.up, deltaX);
        right.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(right, deltaY);

        return true;
    }

    public void update(float deltaTime) {
        if (!enabled) return;
        direction.set(camera.direction).nor().scl(speed * deltaTime);
        right.set(camera.direction).crs(camera.up).nor().scl(speed * deltaTime);

        if (forward) {
            camera.position.add(direction);
        }
        if (backward) {
            camera.position.sub(direction);
        }
        if (left) {
            camera.position.sub(right);
        }
        if (rightMove) {
            camera.position.add(right);
        }
        if (up) {
            camera.position.add(0, speed * deltaTime, 0);
        }
        if (down) {
            camera.position.sub(0, speed * deltaTime, 0);
        }

        camera.update();
    }
}
