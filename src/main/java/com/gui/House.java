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

/**
 * Represents a house in the 3D golf game.
 */
public class House {
    private final ModelInstance wallInstance;
    private final ModelInstance roofInstance;
    private final ModelInstance doorInstance;
    private final ModelInstance windowInstance;
    private final double sizeCoefficient;

    /**
     * Constructs a House instance.
     *
     * @param wallTexturePath   The path to the texture for the walls.
     * @param roofTexturePath   The path to the texture for the roof.
     * @param doorTexturePath   The path to the texture for the door.
     * @param windowTexturePath The path to the texture for the windows.
     * @param baseTexturePath   The path to the texture for the base.
     * @param sizeCoefficient   The size coefficient to scale the house.
     */
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
        new Material(TextureAttribute.createDiffuse(baseTexture));

        // Create model builder
        ModelBuilder modelBuilder = new ModelBuilder();

        float width = 10 * sizeCoefficient;
        float height = 10 * sizeCoefficient + 7;
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

        // Create a roof
        modelBuilder.begin();
        MeshPartBuilder roofBuilder = modelBuilder.part("roof", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, roofMaterial);
        createRoof(roofBuilder, width, roofHeight, depth);
        Model roofModel = modelBuilder.end();
        roofInstance = new ModelInstance(roofModel);

        // Create a door
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

        // Position parts
        wallInstance.transform.setToTranslation(0, baseHeight, 0);
        roofInstance.transform.setToTranslation(0, height + baseHeight, 0);
        doorInstance.transform.setToTranslation(0, baseHeight, depth / 2 - doorWidth / 2);
        windowInstance.transform.setToTranslation(width / 2 - windowWidth / 2, height / 2 + baseHeight, depth / 2 - windowWidth / 2);
    }

    /**
     * Creates the walls of the house.
     *
     * @param builder The MeshPartBuilder to use for creating the walls.
     * @param width   The width of the walls.
     * @param height  The height of the walls.
     */
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

    /**
     * Creates the roof of the house.
     *
     * @param builder The MeshPartBuilder to use for creating the roof.
     * @param width   The width of the roof.
     * @param height  The height of the roof.
     * @param depth   The depth of the roof.
     */
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
                width / 2, -height / 8, -depth / 2,
                width / 2, height, -depth / 2,
                width / 2, height, depth / 2,
                width / 2, -height / 8, depth / 2,
                0, 1, 0
        );
        builder.rect(
                -width / 2, -height / 4, depth / 2,
                -width / 2, height, depth / 2,
                -width / 2, height, -depth / 2,
                -width / 2, -height / 4, -depth / 2,
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

    /**
     * Creates the door of the house.
     *
     * @param builder The MeshPartBuilder to use for creating the door.
     * @param width   The width of the door.
     * @param height  The height of the door.
     */
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

    /**
     * Creates the windows of the house.
     *
     * @param builder The MeshPartBuilder to use for creating the windows.
     * @param width   The width of the windows.
     * @param height  The height of the windows.
     */
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

    /**
     * Gets the ModelInstance representing the walls of the house.
     *
     * @return The ModelInstance representing the walls.
     */
    public ModelInstance getWallInstance() {
        return wallInstance;
    }

    /**
     * Gets the ModelInstance representing the roof of the house.
     *
     * @return The ModelInstance representing the roof.
     */
    public ModelInstance getRoofInstance() {
        return roofInstance;
    }

    /**
     * Gets the ModelInstance representing the door of the house.
     *
     * @return The ModelInstance representing the door.
     */
    public ModelInstance getDoorInstance() {
        return doorInstance;
    }

    /**
     * Gets the ModelInstance representing the windows of the house.
     *
     * @return The ModelInstance representing the windows.
     */
    public ModelInstance getWindowInstance() {
        return windowInstance;
    }

    /**
     * Sets the position of the house.
     *
     * @param x The x-coordinate of the house.
     * @param y The y-coordinate of the house.
     * @param z The z-coordinate of the house.
     */
    public void setPosition(float x, float y, float z) {
        float xBias = -0.5f;
        float zBias = -0.5f;
        wallInstance.transform.setToTranslation(x + xBias, y - 7, z + zBias);
        roofInstance.transform.setToTranslation(x + xBias, (float) (y + 10 * sizeCoefficient), z + zBias);
        doorInstance.transform.setToTranslation((float) (x - 4 * sizeCoefficient) + xBias, y, (float) (z + 5.1 * sizeCoefficient) + zBias); // Position a door at the front
        windowInstance.transform.setToTranslation((float) (x + 4 * sizeCoefficient) + xBias, (float) (y + 5 * sizeCoefficient), (float) (z + 5.1 * sizeCoefficient) + zBias); // Position window at the front
    }

    /**
     * Disposes of the resources used by the house.
     */
    public void dispose() {
        wallInstance.model.dispose();
        roofInstance.model.dispose();
        doorInstance.model.dispose();
        windowInstance.model.dispose();
    }
}
