package com.gui;

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

public class Debugger3D {
    private Array<ModelInstance> spheres;
    private Model sphereModel;
    private boolean debuggerEnabled;

    public Debugger3D() {
        spheres = new Array<>();
        debuggerEnabled = false;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
        builder.sphere(0.5f, 0.5f, 0.5f, 16, 16);
        sphereModel = modelBuilder.end();
    }

    public void toggleDebugger() {
        debuggerEnabled = !debuggerEnabled;
    }

    public boolean isDebuggerEnabled() {
        return debuggerEnabled;
    }

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

    private boolean getIntersectionWithTerrain(Ray ray, double[][] heightMap, Vector3 intersection) {
        // Define the terrain boundaries and grid size
        int terrainWidth = heightMap.length;
        int terrainHeight = heightMap[0].length;
        float gridSize = 1f;  // Assuming each grid cell is 1x1 unit

        Plane plane = new Plane(new Vector3(0, 1, 0), 0);
        if (Intersector.intersectRayPlane(ray, plane, intersection)) {
            float x = intersection.x;
            float z = intersection.z;

            // Check if the intersection is within the terrain boundaries
            if (x >= 0 && x < terrainWidth * gridSize && z >= 0 && z < terrainHeight * gridSize) {
                // Perform bilinear interpolation to get the height at the intersection point
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
                    float height = (h0 * (1 - sz) + h1 * sz);

                    intersection.y = height;
                    return true;
                }
            }
        }
        return false;
    }

    public void addSphereAt(Vector3 position) {
        ModelInstance sphereInstance = new ModelInstance(sphereModel);
        sphereInstance.transform.setToTranslation(position);
        spheres.add(sphereInstance);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for (ModelInstance sphere : spheres) {
            modelBatch.render(sphere, environment);
        }
    }

    public void dispose() {
        sphereModel.dispose();
    }
}
