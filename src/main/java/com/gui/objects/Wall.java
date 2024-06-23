package com.gui.objects;

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
 * The Tree class represents a tree in the game, consisting of a trunk and leaves.
 * It handles the creation, positioning, and rendering of the tree.
 */
public class Wall {
    private final ModelInstance trunkInstance;
    private final ModelInstance leavesInstance;

    /**
     * Constructs a Tree object with the specified textures for the trunk and leaves.
     *
     * @param trunkTexturePath  the file path to the trunk texture
     * @param leavesTexturePath the file path to the leave texture
     */
    public Wall(String trunkTexturePath, String leavesTexturePath) {
        // Load textures
        Texture trunkTexture = new Texture(Gdx.files.internal(trunkTexturePath));
        Texture leavesTexture = new Texture(Gdx.files.internal(leavesTexturePath));

        // Create materials
        Material trunkMaterial = new Material(TextureAttribute.createDiffuse(trunkTexture));
        Material leavesMaterial = new Material(TextureAttribute.createDiffuse(leavesTexture));

        // Create model builder
        ModelBuilder modelBuilder = new ModelBuilder();

        // Create trunk divided into three segments
        modelBuilder.begin();
        MeshPartBuilder trunkBuilder = modelBuilder.part("trunk", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, trunkMaterial);
        float trunkWidth = 1f;
        float trunkHeight = 8f;
        float segmentHeight = trunkHeight / 3;

        // Create each segment of the trunk
        for (int i = 0; i < 3; i++) {
            float y = i * segmentHeight;
            createTrunkSegment(trunkBuilder, trunkWidth, segmentHeight, y);
        }

        Model trunkModel = modelBuilder.end();
        trunkInstance = new ModelInstance(trunkModel);

        // Create leaves
        modelBuilder.begin();
        modelBuilder.part("leaves", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, leavesMaterial)
                .box(0f, 0f, 0f); // Dimensions for the leaves
        Model leavesModel = modelBuilder.end();
        leavesInstance = new ModelInstance(leavesModel);

        // Position the leaves on top of the trunk
        leavesInstance.transform.setToTranslation(0, trunkHeight, 0); // Adjust height to sit on top of the trunk
    }

    /**
     * Creates a trunk segment with the specified dimensions and vertical offset.
     *
     * @param builder the MeshPartBuilder used to construct the trunk segment
     * @param width   the width of the trunk segment
     * @param height  the height of the trunk segment
     * @param yOffset the vertical offset of the trunk segment
     */
    private void createTrunkSegment(MeshPartBuilder builder, float width, float height, float yOffset) {
        // Vertices
        float[] p1 = {-width / 2, yOffset, -width / 2};
        float[] p2 = {width / 2, yOffset, -width / 2};
        float[] p3 = {width / 2, yOffset + height, -width / 2};
        float[] p4 = {-width / 2, yOffset + height, -width / 2};

        float[] p5 = {-width / 2, yOffset, width / 2};
        float[] p6 = {width / 2, yOffset, width / 2};
        float[] p7 = {width / 2, yOffset + height, width / 2};
        float[] p8 = {-width / 2, yOffset + height, width / 2};

        // Front face
        addDoubleSidedQuad(builder, p1, p2, p3, p4, 0, 0, -1);

        // Back face
        addDoubleSidedQuad(builder, p5, p6, p7, p8, 0, 0, 1);

        // Left face
        addDoubleSidedQuad(builder, p1, p5, p8, p4, -1, 0, 0);

        // Right face
        addDoubleSidedQuad(builder, p2, p6, p7, p3, 1, 0, 0);

        // Top face
        addDoubleSidedQuad(builder, p4, p3, p7, p8, 0, 1, 0);

        // Bottom face
        addDoubleSidedQuad(builder, p1, p2, p6, p5, 0, -1, 0);
    }

    /**
     * Adds a double-sided quad to the builder.
     *
     * @param builder the MeshPartBuilder used to construct the quad
     * @param v1      the first vertex of the quad
     * @param v2      the second vertex of the quad
     * @param v3      the third vertex of the quad
     * @param v4      the fourth vertex of the quad
     * @param nx      the normal x-component
     * @param ny      the normal y-component
     * @param nz      the normal z-component
     */
    private void addDoubleSidedQuad(MeshPartBuilder builder, float[] v1, float[] v2, float[] v3, float[] v4, float nx, float ny, float nz) {
        // Front face (normal facing outwards)
        builder.vertex(v1[0], v1[1], v1[2], nx, ny, nz, 0, 0);
        builder.vertex(v2[0], v2[1], v2[2], nx, ny, nz, 1, 0);
        builder.vertex(v3[0], v3[1], v3[2], nx, ny, nz, 1, 1);
        builder.vertex(v4[0], v4[1], v4[2], nx, ny, nz, 0, 1);

        short i1 = builder.lastIndex();
        short i2 = (short) (i1 - 1);
        short i3 = (short) (i2 - 1);
        short i4 = (short) (i3 - 1);

        builder.triangle(i1, i2, i3);
        builder.triangle(i1, i3, i4);

        // Back face (normal facing inwards)
        builder.vertex(v4[0], v4[1], v4[2], -nx, -ny, -nz, 0, 0);
        builder.vertex(v3[0], v3[1], v3[2], -nx, -ny, -nz, 1, 0);
        builder.vertex(v2[0], v2[1], v2[2], -nx, -ny, -nz, 1, 1);
        builder.vertex(v1[0], v1[1], v1[2], -nx, -ny, -nz, 0, 1);

        short i5 = builder.lastIndex();
        short i6 = (short) (i5 - 1);
        short i7 = (short) (i6 - 1);
        short i8 = (short) (i7 - 1);

        builder.triangle(i5, i6, i7);
        builder.triangle(i5, i7, i8);
    }

    /**
     * Gets the ModelInstance representing the trunk.
     *
     * @return the trunk ModelInstance
     */
    public ModelInstance getTrunkInstance() {
        return trunkInstance;
    }

    /**
     * Gets the ModelInstance representing the leaves.
     *
     * @return the leaves ModelInstance
     */
    public ModelInstance getLeavesInstance() {
        return leavesInstance;
    }

    /**
     * Sets the position of the tree in the game world.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public void setPosition(float x, float y, float z) {
        float xBias = 0f;
        float zBias = 0f;
        trunkInstance.transform.setToTranslation(x + xBias, y, z + zBias);
        leavesInstance.transform.setToTranslation(x + xBias, y + 8f, z + zBias); // Adjust height
        // to sit on top of the trunk
    }

    /**
     * Disposes of the tree model resources.
     */
    public void dispose() {
        trunkInstance.model.dispose();
        leavesInstance.model.dispose();
    }
}
