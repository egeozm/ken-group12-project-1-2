package com.gui.debbugers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

/**
 * The Debugger3D class provides functionality for 3D debugging, including
 * placing spheres at specific locations on a terrain based on user input.
 */
public class Debugger3D {
    private final Array<ModelInstance> spheres;
    private final Model sphereModel;
    private boolean debuggerEnabled;

    /**
     * Constructs a new Debugger3D instance and initializes the sphere model.
     */
    public Debugger3D() {
        spheres = new Array<>();
        debuggerEnabled = false;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
        builder.sphere(0.5f, 0.5f, 0.5f, 16, 16);
        sphereModel = modelBuilder.end();
    }

    /**
     * Toggles the debugger on or off.
     */
    public void toggleDebugger() {
        debuggerEnabled = !debuggerEnabled;
    }

    /**
     * Checks if the debugger is currently enabled.
     *
     * @return true if the debugger is enabled, false otherwise.
     */
    public boolean isDebuggerEnabled() {
        return debuggerEnabled;
    }

    /**
     * Handles user input for placing spheres on the terrain based on mouse clicks.
     *
     * @param camera    The camera used to calculate the ray from the screen coordinates.
     * @param heightMap The height map representing the terrain.
     */
    public void handleInput(PerspectiveCamera camera, double[][] heightMap) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && debuggerEnabled) {
            Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 intersection = new Vector3();
            if (getIntersectionWithTerrain(ray, heightMap, intersection)) {
                System.out.println("Point clicked: " + intersection);
                addSphereAt(intersection);
            }
        }
    }

    /**
     * Computes the intersection of a ray with the terrain and finds the intersection point.
     *
     * @param ray         The ray to intersect with the terrain.
     * @param heightMap   The height map representing the terrain.
     * @param intersection The vector to store the intersection point.
     * @return true if an intersection is found, false otherwise.
     */
    private boolean getIntersectionWithTerrain(Ray ray, double[][] heightMap, Vector3 intersection) {
        int terrainWidth = heightMap.length;
        int terrainHeight = heightMap[0].length;
        float gridSize = 1f;

        Plane plane = new Plane(new Vector3(0, 1, 0), 0);
        if (Intersector.intersectRayPlane(ray, plane, intersection)) {
            float x = intersection.x;
            float z = intersection.z;

            if (x >= 0 && x < terrainWidth * gridSize && z >= 0 && z < terrainHeight * gridSize) {
                int x0 = (int) Math.floor(x / gridSize);
                int x1 = x0 + 1;
                int z0 = (int) Math.floor(z / gridSize);
                int z1 = z0 + 1;

                if (x1 < terrainWidth && z1 < terrainHeight) {
                    float sx = (x / gridSize) - x0;
                    float sz = (z / gridSize) - z0;

                    float h00 = (float) heightMap[x0][z0];
                    float h10 = (float) heightMap[x1][z0];
                    float h01 = (float) heightMap[x0][z1];
                    float h11 = (float) heightMap[x1][z1];

                    float h0 = h00 * (1 - sx) + h10 * sx;
                    float h1 = h01 * (1 - sx) + h11 * sx;

                    intersection.y = (h0 * (1 - sz) + h1 * sz);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a sphere at the specified position.
     *
     * @param position The position to place the sphere.
     */
    public void addSphereAt(Vector3 position) {
        ModelInstance sphereInstance = new ModelInstance(sphereModel);
        sphereInstance.transform.setToTranslation(position);
        spheres.add(sphereInstance);
    }

    /**
     * Renders all spheres using the provided model batch and environment.
     *
     * @param modelBatch  The model batch to render the spheres.
     * @param environment The environment settings for rendering.
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        for (ModelInstance sphere : spheres) {
            modelBatch.render(sphere, environment);
        }
    }

    /**
     * Disposes of the resources used by the sphere model.
     */
    public void dispose() {
        sphereModel.dispose();
    }
}
