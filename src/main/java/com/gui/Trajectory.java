package com.gui;

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

public class Trajectory {
    private ModelInstance trajectoryInstance;
    private Vector3 direction;
    private boolean kickingMode;
    private ModelBuilder modelBuilder;
    private double kickingPower = 0;

    public Trajectory(double kickingPower) {
        direction = new Vector3(1, 0, 0);  // Initial direction
        kickingMode = false;
        this.kickingPower = kickingPower;
        createTrajectoryModel();
    }

    private void createTrajectoryModel() {
        //System.out.println(kickingPower);
        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material(ColorAttribute.createDiffuse(Color.RED));
        MeshPartBuilder builder = modelBuilder.part("trajectory", GL20.GL_LINES, Usage.Position | Usage.ColorPacked, material);
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, direction.x * (float) kickingPower, direction.y * (float) kickingPower, direction.z * (float) kickingPower);  // Line length is 10 units
        Model trajectoryModel = modelBuilder.end();
        trajectoryInstance = new ModelInstance(trajectoryModel);
    }
    public void setKickingPower(double kickingPower){
        this.kickingPower = kickingPower;
    }
    public void update(Vector3 ballPosition) {
        // Update the trajectory line
        createTrajectoryModel();  // Recreate the trajectory model with the new direction
        trajectoryInstance.transform.setToTranslation(ballPosition);
        if (kickingMode) {
            // Update direction based on input
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
    public void toggleKickingMode() {
        kickingMode = !kickingMode;
    }

    public boolean isKickingMode() {
        return kickingMode;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public ModelInstance getInstance() {
        return trajectoryInstance;
    }
}
