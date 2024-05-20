package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Skybox {
    private ModelInstance skyboxInstance;
    private Model skyboxModel;
    private Texture[] textures;

    public Skybox(String left, String right, String top, String bottom, String front, String back) {
        textures = new Texture[6];
        textures[0] = new Texture(Gdx.files.internal(left));    // left
        textures[1] = new Texture(Gdx.files.internal(right));   // right
        textures[2] = new Texture(Gdx.files.internal(top));     // top
        textures[3] = new Texture(Gdx.files.internal(bottom));  // bottom
        textures[4] = new Texture(Gdx.files.internal(front));   // front
        textures[5] = new Texture(Gdx.files.internal(back));    // back

        Material[] materials = new Material[6];
        for (int i = 0; i < 6; i++) {
            materials[i] = new Material(TextureAttribute.createDiffuse(textures[i]));
        }

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float skyboxSize = 500f;

        MeshPartBuilder part;

        // Back face
        part = modelBuilder.part("back", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[5]);
        part.rect(
                new Vector3(-skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(-skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(0, 0, 1)
        );

        // Front face
        part = modelBuilder.part("front", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[4]);
        part.rect(
                new Vector3(-skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(-skyboxSize, skyboxSize, skyboxSize),
                new Vector3(skyboxSize, skyboxSize, skyboxSize),
                new Vector3(skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(0, 0, -1)
        );

        // Left face
        part = modelBuilder.part("left", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[0]);
        part.rect(
                new Vector3(-skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(-skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(-skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(-skyboxSize, skyboxSize, skyboxSize),
                new Vector3(1, 0, 0)
        );

        // Right face
        part = modelBuilder.part("right", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[1]);
        part.rect(
                new Vector3(skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(skyboxSize, skyboxSize, skyboxSize),
                new Vector3(skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(-1, 0, 0)
        );

        // Bottom face
        part = modelBuilder.part("bottom", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[3]);
        part.rect(
                new Vector3(-skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(-skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(skyboxSize, -skyboxSize, skyboxSize),
                new Vector3(skyboxSize, -skyboxSize, -skyboxSize),
                new Vector3(0, 1, 0)
        );

        // Top face
        part = modelBuilder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, materials[2]);
        part.rect(
                new Vector3(-skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(-skyboxSize, skyboxSize, skyboxSize),
                new Vector3(skyboxSize, skyboxSize, skyboxSize),
                new Vector3(skyboxSize, skyboxSize, -skyboxSize),
                new Vector3(0, -1, 0)
        );

        skyboxModel = modelBuilder.end();
        skyboxInstance = new ModelInstance(skyboxModel);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(skyboxInstance, environment);
    }

    public void dispose() {
        skyboxModel.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
    }
}
