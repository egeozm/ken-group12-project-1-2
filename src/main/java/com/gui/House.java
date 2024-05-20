package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class House {
    private ModelInstance wallInstance;
    private ModelInstance roofInstance;
    private ModelInstance doorInstance;
    private ModelInstance windowInstance;
    private ModelInstance baseInstance;
    private double sizeCoefficient;
    private float xbias = -0.5f;
    private float zbias = -0.5f;

    public House(String wallTexturePath, String roofTexturePath, String doorTexturePath, String windowTexturePath, String baseTexturePath, float sizeCoefficient) {
        this.sizeCoefficient = sizeCoefficient;
        // Load textures
        Texture wallTexture = new Texture(Gdx.files.internal(wallTexturePath));
        Texture roofTexture = new Texture(Gdx.files.internal(roofTexturePath));
        Texture doorTexture = new Texture(Gdx.files.internal(doorTexturePath));
        Texture windowTexture = new Texture(Gdx.files.internal(windowTexturePath));
        Texture baseTexture = new Texture(Gdx.files.internal(baseTexturePath));

        // Create materials
        Material wallMaterial = new Material(TextureAttribute.createDiffuse(wallTexture));
        Material roofMaterial = new Material(TextureAttribute.createDiffuse(roofTexture));
        Material doorMaterial = new Material(TextureAttribute.createDiffuse(doorTexture));
        Material windowMaterial = new Material(TextureAttribute.createDiffuse(windowTexture));
        Material baseMaterial = new Material(TextureAttribute.createDiffuse(baseTexture));

        // Create model builder
        ModelBuilder modelBuilder = new ModelBuilder();

        float width = 10 * sizeCoefficient;
        float height = 10 * sizeCoefficient+7;
        float depth = 10 * sizeCoefficient;
        float doorWidth = 2 * sizeCoefficient;
        float doorHeight = 4 * sizeCoefficient;
        float windowWidth = 2 * sizeCoefficient;
        float windowHeight = 2 * sizeCoefficient;
        float roofHeight = 2 * sizeCoefficient;
        float baseHeight = 1 * sizeCoefficient;

        // Create walls
        modelBuilder.begin();
        MeshPartBuilder wallBuilder = modelBuilder.part("wall", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, wallMaterial);
        createWalls(wallBuilder, width, height);
        Model wallModel = modelBuilder.end();
        wallInstance = new ModelInstance(wallModel);

        // Create roof
        modelBuilder.begin();
        MeshPartBuilder roofBuilder = modelBuilder.part("roof", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, roofMaterial);
        createRoof(roofBuilder, width, roofHeight, depth);
        Model roofModel = modelBuilder.end();
        roofInstance = new ModelInstance(roofModel);

        // Create door
        modelBuilder.begin();
        MeshPartBuilder doorBuilder = modelBuilder.part("door", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, doorMaterial);
        createDoor(doorBuilder, doorWidth, doorHeight);
        Model doorModel = modelBuilder.end();
        doorInstance = new ModelInstance(doorModel);

        // Create windows
        modelBuilder.begin();
        MeshPartBuilder windowBuilder = modelBuilder.part("window", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, windowMaterial);
        createWindows(windowBuilder, windowWidth, windowHeight);
        Model windowModel = modelBuilder.end();
        windowInstance = new ModelInstance(windowModel);

        // Create base
        /*modelBuilder.begin();
        MeshPartBuilder baseBuilder = modelBuilder.part("base", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, baseMaterial);
        createBase(baseBuilder, width + 2 * sizeCoefficient, baseHeight, depth + 2 * sizeCoefficient);
        Model baseModel = modelBuilder.end();
        baseInstance = new ModelInstance(baseModel);*/

        // Position parts
        wallInstance.transform.setToTranslation(0, baseHeight, 0);
        roofInstance.transform.setToTranslation(0, height + baseHeight, 0);
        doorInstance.transform.setToTranslation(0, baseHeight, depth / 2 - doorWidth / 2);
        windowInstance.transform.setToTranslation(width / 2 - windowWidth / 2, height / 2 + baseHeight, depth / 2 - windowWidth / 2);
        //baseInstance.transform.setToTranslation(0, 0, 0);
    }

    private void createWalls(MeshPartBuilder builder, float width, float height) {
        // Front wall
        builder.rect(
                -width / 2, 0, width / 2,
                width / 2, 0, width / 2,
                width / 2, height, width / 2,
                -width / 2, height, width / 2,
                0, 0, 1
        );
        builder.rect(
                width / 2, 0, width / 2,
                -width / 2, 0, width / 2,
                -width / 2, height, width / 2,
                width / 2, height, width / 2,
                0, 0, -1
        );
        // Back wall
        builder.rect(
                width / 2, 0, -width / 2,
                -width / 2, 0, -width / 2,
                -width / 2, height, -width / 2,
                width / 2, height, -width / 2,
                0, 0, -1
        );
        builder.rect(
                -width / 2, 0, -width / 2,
                width / 2, 0, -width / 2,
                width / 2, height, -width / 2,
                -width / 2, height, -width / 2,
                0, 0, 1
        );
        // Left wall
        builder.rect(
                -width / 2, 0, -width / 2,
                -width / 2, 0, width / 2,
                -width / 2, height, width / 2,
                -width / 2, height, -width / 2,
                -1, 0, 0
        );
        builder.rect(
                -width / 2, 0, width / 2,
                -width / 2, 0, -width / 2,
                -width / 2, height, -width / 2,
                -width / 2, height, width / 2,
                1, 0, 0
        );
        // Right wall
        builder.rect(
                width / 2, 0, width / 2,
                width / 2, 0, -width / 2,
                width / 2, height, -width / 2,
                width / 2, height, width / 2,
                1, 0, 0
        );
        builder.rect(
                width / 2, 0, -width / 2,
                width / 2, 0, width / 2,
                width / 2, height, width / 2,
                width / 2, height, -width / 2,
                -1, 0, 0
        );
    }

    private void createRoof(MeshPartBuilder builder, float width, float height, float depth) {
        // Bottom face of the roof (front side)
        builder.rect(
                -width / 2, height, -depth / 2,
                width / 2, height, -depth / 2,
                width / 2, 0, -depth / 2,
                -width / 2, 0, -depth / 2,
                0, -1, 0
        );
        builder.rect(
                width / 2, height, -depth / 2,
                -width / 2, height, -depth / 2,
                -width / 2, 0, -depth / 2,
                width / 2, 0, -depth / 2,
                0, 1, 0
        );

        // Bottom face of the roof (back side)
        builder.rect(
                -width / 2, height, depth / 2,
                width / 2, height, depth / 2,
                width / 2, 0, depth / 2,
                -width / 2, 0, depth / 2,
                0, -1, 0
        );
        builder.rect(
                width / 2, height, depth / 2,
                -width / 2, height, depth / 2,
                -width / 2, 0, depth / 2,
                width / 2, 0, depth / 2,
                0, 1, 0
        );

        // Top face of the roof (front side)
        builder.rect(
                -width / 2, height, -depth / 2,
                width / 2, height, -depth / 2,
                width / 2, height, depth / 2,
                -width / 2, height, depth / 2,
                0, 1, 0
        );
        builder.rect(
                width / 2, -height/8, -depth / 2,
                width / 2, height, -depth / 2,
                width / 2, height, depth / 2,
                width / 2, -height/8, depth / 2,
                0, 1, 0
        );
        builder.rect(
                -width / 2, -height/4, depth / 2,
                -width / 2, height, depth / 2,
                -width / 2, height, -depth / 2,
                -width / 2, -height/4, -depth / 2,
                0, 1, 0
        );
        builder.rect(
                width / 2, height, -depth / 2,
                -width / 2, height, -depth / 2,
                -width / 2, height, depth / 2,
                width / 2, height, depth / 2,
                0, 1, 0
        );

        // Top face of the roof (back side)
        builder.rect(
                -width / 2, 0, depth / 2,
                width / 2, 0, depth / 2,
                width / 2, height, depth / 2,
                -width / 2, height, depth / 2,
                0, 1, 0
        );
        builder.rect(
                width / 2, 0, depth / 2,
                -width / 2, 0, depth / 2,
                -width / 2, height, depth / 2,
                width / 2, height, depth / 2,
                0, -1, 0
        );
    }

    private void createDoor(MeshPartBuilder builder, float width, float height) {
        // Front face of the door
        builder.rect(
                -width / 2, 0, 0,
                width / 2, 0, 0,
                width / 2, height, 0,
                -width / 2, height, 0,
                0, 0, 1
        );
        builder.rect(
                width / 2, 0, 0,
                -width / 2, 0, 0,
                -width / 2, height, 0,
                width / 2, height, 0,
                0, 0, -1
        );
    }

    private void createWindows(MeshPartBuilder builder, float width, float height) {
        // Front face of the window
        builder.rect(
                -width / 2, 0, 0,
                width / 2, 0, 0,
                width / 2, height, 0,
                -width / 2, height, 0,
                0, 0, 1
        );
        builder.rect(
                width / 2, 0, 0,
                -width / 2, 0, 0,
                -width / 2, height, 0,
                width / 2, height, 0,
                0, 0, -1
        );
    }

    private void createBase(MeshPartBuilder builder, float width, float height, float depth) {
        // Front face of the base
        builder.rect(
                -width / 2, 0, -depth / 2,
                width / 2, 0, -depth / 2,
                width / 2, height, -depth / 2,
                -width / 2, height, -depth / 2,
                0, 0, -1
        );
        builder.rect(
                width / 2, 0, -depth / 2,
                -width / 2, 0, -depth / 2,
                -width / 2, height, -depth / 2,
                width / 2, height, -depth / 2,
                0, 0, 1
        );
        // Back face of the base
        builder.rect(
                -width / 2, 0, depth / 2,
                width / 2, 0, depth / 2,
                width / 2, height, depth / 2,
                -width / 2, height, depth / 2,
                0, 0, 1
        );
        builder.rect(
                width / 2, 0, depth / 2,
                -width / 2, 0, depth / 2,
                -width / 2, height, depth / 2,
                width / 2, height, depth / 2,
                0, 0, -1
        );
        // Left face of the base
        builder.rect(
                -width / 2, 0, -depth / 2,
                -width / 2, 0, depth / 2,
                -width / 2, height, depth / 2,
                -width / 2, height, -depth / 2,
                -1, 0, 0
        );
        builder.rect(
                -width / 2, 0, depth / 2,
                -width / 2, 0, -depth / 2,
                -width / 2, height, -depth / 2,
                -width / 2, height, depth / 2,
                1, 0, 0
        );
        // Right face of the base
        builder.rect(
                width / 2, 0, -depth / 2,
                width / 2, 0, depth / 2,
                width / 2, height, depth / 2,
                width / 2, height, -depth / 2,
                1, 0, 0
        );
        builder.rect(
                width / 2, 0, depth / 2,
                width / 2, 0, -depth / 2,
                width / 2, height, -depth / 2,
                width / 2, height, depth / 2,
                -1, 0, 0
        );
    }

    public ModelInstance getWallInstance() {
        return wallInstance;
    }

    public ModelInstance getRoofInstance() {
        return roofInstance;
    }

    public ModelInstance getDoorInstance() {
        return doorInstance;
    }

    public ModelInstance getWindowInstance() {
        return windowInstance;
    }

    /*public ModelInstance getBaseInstance() {
        return baseInstance;
    }*/

    public void setPosition(float x, float y, float z) {
        wallInstance.transform.setToTranslation(x+xbias, y-7, z+zbias);
        roofInstance.transform.setToTranslation(x+xbias, (float) (y + 10*sizeCoefficient), z+zbias);
        doorInstance.transform.setToTranslation((float) (x - 4*sizeCoefficient)+xbias, y, (float) (z + 5.1*sizeCoefficient)+zbias); // Position door at the front
        windowInstance.transform.setToTranslation((float) (x + 4*sizeCoefficient)+xbias, (float) (y + 5*sizeCoefficient), (float) (z + 5.1*sizeCoefficient)+zbias); // Position window at the front
        //baseInstance.transform.setToTranslation(x, (float) (y - 1*sizeCoefficient), z); // Position base under the house
    }

    public void dispose() {
        wallInstance.model.dispose();
        roofInstance.model.dispose();
        doorInstance.model.dispose();
        windowInstance.model.dispose();
        //baseInstance.model.dispose();
    }
}
