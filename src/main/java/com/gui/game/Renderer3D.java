package com.gui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gui.labels.UILabel;
import com.gui.labels.WinLabel;
import com.gui.objects.GolfBall;
import com.gui.objects.SkySphere;
import com.gui.objects.Trajectory;
import com.gui.objects.Tree;
import com.gui.objects.House;
import com.gui.terrain.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.gui.game.InputHandler.trajectoryCameraToggle;

public class Renderer3D {
    private final PerspectiveCamera camera;
    private final ModelBatch modelBatch;
    private final Environment environment;
    private final SkySphere skySphere;
    private final Trajectory trajectory;
    private final HashMap<String, ArrayList<ModelInstance>> renderableObjects;
    private final UILabel bottomLeftLabel;
    private final UILabel shotsLabel;
    private final UILabel coordinatesLabel;
    private final UILabel coordinatesAILabel;
    private final WinLabel winLabel;
    private final WinLabel winLabelAI;

    public Renderer3D(PerspectiveCamera camera, ModelBatch modelBatch, Environment environment,
                      SkySphere skySphere, Trajectory trajectory,
                      UILabel bottomLeftLabel, UILabel shotsLabel,
                      UILabel coordinatesLabel, UILabel coordinatesAILabel, WinLabel winLabel, WinLabel winLabelAI) {
        this.camera = camera;
        this.modelBatch = modelBatch;
        this.environment = environment;
        this.skySphere = skySphere;
        this.trajectory = trajectory;
        this.renderableObjects = new HashMap<>();
        this.bottomLeftLabel = bottomLeftLabel;
        this.shotsLabel = shotsLabel;
        this.coordinatesLabel = coordinatesLabel;
        this.coordinatesAILabel = coordinatesAILabel;
        this.winLabel = winLabel;
        this.winLabelAI = winLabelAI;
    }

    public void addRenderableObject(String category, ModelInstance instance) {
        renderableObjects.computeIfAbsent(category, k -> new ArrayList<>()).add(instance);
    }

    public void removeRenderableObject(String category, ModelInstance instance) {
        ArrayList<ModelInstance> instances = renderableObjects.get(category);
        if (instances != null) {
            instances.remove(instance);
        }
    }

    public void render(float delta, GolfBall golfBall, GolfBall advancedAI) {
        trajectoryCameraToggle();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        golfBall.update(delta);
        advancedAI.update(delta);
        coordinatesLabel.setText("X: " + golfBall.getPosition().x + "\n" +
                "Y: " + golfBall.getPosition().y + "\n" +
                "Z: " + golfBall.getPosition().z + "\n" +
                "HOLEX: " + Terrain.getInstance().getHoleX() + "\n" +
                "HOLEY: " + Terrain.getInstance().getHoleZ() + "\n");
        coordinatesAILabel.setText("X: " + advancedAI.getPosition().x + "\n" +
                "Y: " + advancedAI.getPosition().y + "\n" +
                "Z: " + advancedAI.getPosition().z + "\n");

        modelBatch.begin(camera);
        skySphere.render(modelBatch, environment);
        trajectory.update(golfBall.getInstance().transform.getTranslation(new Vector3()));
        modelBatch.render(trajectory.getInstance(), environment);

        for (ArrayList<ModelInstance> instances : renderableObjects.values()) {
            for (ModelInstance instance : instances) {
                modelBatch.render(instance, environment);
            }
        }

        modelBatch.render(golfBall.getInstance(), environment);
        modelBatch.render(advancedAI.getInstance(), environment);
        modelBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            trajectory.toggleKickingMode();
        }


        bottomLeftLabel.render();
        shotsLabel.render();
        coordinatesLabel.render();
        coordinatesAILabel.render();
        winLabel.render();
        winLabelAI.render();
        try {
            InputHandler.handleInput();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void dispose() {
        modelBatch.dispose();
        for (ArrayList<ModelInstance> instances : renderableObjects.values()) {
            for (ModelInstance instance : instances) {
                instance.model.dispose();
            }
        }
        skySphere.dispose();
        bottomLeftLabel.dispose();
        shotsLabel.dispose();
        coordinatesLabel.dispose();
        coordinatesAILabel.dispose();
        winLabel.dispose();
        winLabelAI.dispose();
    }
}
