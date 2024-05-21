package com.gui;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * The SkySphere class represents a 3D skybox consisting of two half-spheres (top and bottom) that
 * are textured to create a sky environment.
 */
public class SkySphere {
    private final ModelInstance topSphereInstance;
    private final ModelInstance bottomSphereInstance;
    private final Model topSkysphereModel;
    private final Model bottomSkysphereModel;
    private final Texture topTexture;
    private final Texture bottomTexture;

    /**
     * Constructs a SkySphere with specified texture paths for the top and bottom half-spheres.
     *
     * @param topTexturePath    the file path to the texture for the top half-sphere
     * @param bottomTexturePath the file path to the texture for the bottom half-sphere
     */
    public SkySphere(String topTexturePath, String bottomTexturePath) {
        topTexture = new Texture(topTexturePath);
        bottomTexture = new Texture(bottomTexturePath);
        Material topMaterial = new Material(TextureAttribute.createDiffuse(topTexture));
        Material bottomMaterial = new Material(TextureAttribute.createDiffuse(bottomTexture));

        ModelBuilder modelBuilder = new ModelBuilder();

        // Create top half-sphere
        modelBuilder.begin();
        MeshPartBuilder topPart = modelBuilder.part("topSkysphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, topMaterial);
        createHalfSphere(topPart, true);
        topSkysphereModel = modelBuilder.end();
        topSphereInstance = new ModelInstance(topSkysphereModel);

        // Create bottom half-sphere
        modelBuilder.begin();
        MeshPartBuilder bottomPart = modelBuilder.part("bottomSkysphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, bottomMaterial);
        createHalfSphere(bottomPart, false);
        bottomSkysphereModel = modelBuilder.end();
        bottomSphereInstance = new ModelInstance(bottomSkysphereModel);
    }

    /**
     * Creates a half-sphere using the specified MeshPartBuilder.
     *
     * @param part the MeshPartBuilder used to create the half-sphere
     * @param top  true if creating the top half-sphere, false if creating the bottom half-sphere
     */
    private void createHalfSphere(MeshPartBuilder part, boolean top) {
        for (int u = 0; u < 64; u++) {
            float theta0 = (float) (u * 2 * Math.PI / 64);
            float theta1 = (float) ((u + 1) * 2 * Math.PI / 64);
            for (int v = 0; v < 16; v++) {
                float phi0 = top ? (float) (v * Math.PI / (2 * 16)) : (float) (v * Math.PI / (2 * 16) + Math.PI / 2);
                float phi1 = top ? (float) ((v + 1) * Math.PI / (2 * 16)) : (float) ((v + 1) * Math.PI / (2 * 16) + Math.PI / 2);

                Vector3 p0 = new Vector3(
                        (float) 500.0 * MathUtils.sin(phi0) * MathUtils.cos(theta0),
                        (float) 500.0 * MathUtils.cos(phi0),
                        (float) 500.0 * MathUtils.sin(phi0) * MathUtils.sin(theta0)
                );
                Vector3 p1 = new Vector3(
                        (float) 500.0 * MathUtils.sin(phi0) * MathUtils.cos(theta1),
                        (float) 500.0 * MathUtils.cos(phi0),
                        (float) 500.0 * MathUtils.sin(phi0) * MathUtils.sin(theta1)
                );
                Vector3 p2 = new Vector3(
                        (float) 500.0 * MathUtils.sin(phi1) * MathUtils.cos(theta1),
                        (float) 500.0 * MathUtils.cos(phi1),
                        (float) 500.0 * MathUtils.sin(phi1) * MathUtils.sin(theta1)
                );
                Vector3 p3 = new Vector3(
                        (float) 500.0 * MathUtils.sin(phi1) * MathUtils.cos(theta0),
                        (float) 500.0 * MathUtils.cos(phi1),
                        (float) 500.0 * MathUtils.sin(phi1) * MathUtils.sin(theta0)
                );

                float[] vertex0 = {p0.x, p0.y, p0.z, 0, -1, 0, (float)u / 64, (float)v / 16};
                float[] vertex1 = {p1.x, p1.y, p1.z, 0, -1, 0, (float)(u + 1) / 64, (float)v / 16};
                float[] vertex2 = {p2.x, p2.y, p2.z, 0, -1, 0, (float)(u + 1) / 64, (float)(v + 1) / 16};
                float[] vertex3 = {p3.x, p3.y, p3.z, 0, -1, 0, (float)u / 64, (float)(v + 1) / 16};

                part.vertex(vertex0);
                part.vertex(vertex1);
                part.vertex(vertex2);
                part.vertex(vertex3);

                short i0 = part.lastIndex();
                short i1 = (short) (i0 - 1);
                short i2 = (short) (i1 - 1);
                short i3 = (short) (i2 - 1);

                part.triangle(i0, i1, i2);
                part.triangle(i0, i2, i3);
            }
        }
    }

    /**
     * Renders the sky sphere.
     *
     * @param modelBatch  the ModelBatch used for rendering
     * @param environment the environment used for rendering
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(topSphereInstance, environment);
        modelBatch.render(bottomSphereInstance, environment);
    }

    /**
     * Disposes of the sky sphere resources.
     */
    public void dispose() {
        topSkysphereModel.dispose();
        bottomSkysphereModel.dispose();
        topTexture.dispose();
        bottomTexture.dispose();
    }
}
