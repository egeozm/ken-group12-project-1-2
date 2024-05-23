package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * The CameraController class is responsible for handling keyboard and mouse inputs to control a Camera in a 3D space.
 * It supports movement in all directions as well as looking around using the mouse.
 */
public class CameraController extends InputAdapter {
    private final Camera camera;
    private final Vector3 direction = new Vector3();
    private final Vector3 right = new Vector3();
    private boolean forward, backward, left, rightMove, up, down;
    private final boolean enabled = true;

    /**
     * Constructs a CameraController for the given camera.
     *
     * @param camera The camera to be controlled.
     */
    public CameraController(Camera camera) {
        this.camera = camera;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCursorCatched(true);
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
        float sensitivity = 0.2f;
        float deltaX = -Gdx.input.getDeltaX() * sensitivity;
        float deltaY = -Gdx.input.getDeltaY() * sensitivity;

        camera.direction.rotate(camera.up, deltaX);
        right.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(right, deltaY);

        return true;
    }

    /**
     * Updates the camera's position and direction based on the current input state.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void update(float deltaTime) {
        if (!enabled) return;
        float speed = 20f;
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
