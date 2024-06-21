package com.gui.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * The Trajectory class represents the trajectory of a golf ball in the game.
 * It handles the visualization and updating of the trajectory line based on user input and kicking power.
 */
public class Trajectory {
    private ModelInstance trajectoryInstance;
    private final Vector3 direction;
    private boolean kickingMode;
    private double kickingPower;

    /**
     * Constructs a Trajectory object with the specified initial kicking power.
     *
     * @param kickingPower the initial kicking power
     */
    public Trajectory(double kickingPower) {
        direction = new Vector3(1, 0, 0);  // Initial direction
        kickingMode = false;
        this.kickingPower = kickingPower;
        createTrajectoryModel();
    }

    /**
     * Creates the trajectory model based on the current direction and kicking power.
     */
    private void createTrajectoryModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material(ColorAttribute.createDiffuse(Color.RED));
        MeshPartBuilder builder = modelBuilder.part("trajectory", GL20.GL_LINES, Usage.Position | Usage.ColorPacked, material);
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, direction.x * (float) kickingPower, direction.y * (float) kickingPower, direction.z * (float) kickingPower);
        Model trajectoryModel = modelBuilder.end();
        trajectoryInstance = new ModelInstance(trajectoryModel);
    }

    /**
     * Sets the kicking power.
     *
     * @param kickingPower the new kicking power
     */
    public void setKickingPower(double kickingPower) {
        this.kickingPower = kickingPower;
    }

    /**
     * Updates the trajectory model and direction based on the ball's position and user input.
     *
     * @param ballPosition the current position of the ball
     */
    public void update(Vector3 ballPosition) {
        createTrajectoryModel();  // Recreate the trajectory model with the new direction
        trajectoryInstance.transform.setToTranslation(ballPosition);
        if (kickingMode) {
            // Update a direction based on input
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                direction.z -= 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                direction.z += 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                direction.x -= 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                direction.x += 0.1f;
            }

            // Normalize direction
            direction.nor();
        }
    }

    /**
     * Toggles the kicking mode on or off.
     */
    public void toggleKickingMode() {
        kickingMode = !kickingMode;
    }

    /**
     * Checks if the kicking mode is enabled.
     *
     * @return true if kicking mode is enabled, false otherwise
     */
    public boolean isKickingMode() {
        return kickingMode;
    }

    /**
     * Gets the current direction of the trajectory.
     *
     * @return the direction vector
     */
    public Vector3 getDirection() {
        return direction;
    }

    /**
     * Gets the ModelInstance representing the trajectory.
     *
     * @return the trajectory ModelInstance
     */
    public ModelInstance getInstance() {
        return trajectoryInstance;
    }
}
