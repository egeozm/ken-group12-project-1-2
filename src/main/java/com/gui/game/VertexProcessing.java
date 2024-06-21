package com.gui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gui.objects.House;
import com.gui.objects.Tree;
import com.gui.terrain.Terrain;

import static com.gui.game.Golf3D.*;

public class VertexProcessing {
    public static void createBoundaryFaces(ModelBuilder modelBuilder, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, Texture sideTexture) {
        float extendHeight = 10f;
        Material sideMaterial = new Material(TextureAttribute.createDiffuse(sideTexture));
        MeshPartBuilder builder = modelBuilder.part("terrain" + p1.x + "_" + p1.z, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, sideMaterial);
        Vector3 p1Bottom = new Vector3(p1.x, p1.y - extendHeight, p1.z);
        Vector3 p2Bottom = new Vector3(p2.x, p2.y - extendHeight, p2.z);
        Vector3 p3Bottom = new Vector3(p3.x, p3.y - extendHeight, p3.z);
        Vector3 p4Bottom = new Vector3(p4.x, p4.y - extendHeight, p4.z);

        createVerticalFace(builder, p1, p2, p2Bottom, p1Bottom);
        createVerticalFace(builder, p2, p3, p3Bottom, p2Bottom);
        createVerticalFace(builder, p3, p4, p4Bottom, p3Bottom);
        createVerticalFace(builder, p4, p1, p1Bottom, p4Bottom);
    }

    public static void createVerticalFace(MeshPartBuilder builder, Vector3 top1, Vector3 top2, Vector3 bottom2, Vector3 bottom1) {
        builder.setColor(Color.WHITE);
        builder.ensureVertices(4);
        builder.ensureTriangleIndices(6);

        int i1 = builder.vertex(top1, new Vector3(0, 0, -1), null, new Vector2(0, 0));
        int i2 = builder.vertex(top2, new Vector3(0, 0, -1), null, new Vector2(1, 0));
        int i3 = builder.vertex(bottom2, new Vector3(0, 0, -1), null, new Vector2(1, 1));
        int i4 = builder.vertex(bottom1, new Vector3(0, 0, -1), null, new Vector2(0, 1));

        builder.triangle((short) i1, (short) i2, (short) i3);
        builder.triangle((short) i1, (short) i3, (short) i4);
    }

    public static ModelInstance[] initVertices(){
        int chunkSize = 50;
        ModelBuilder modelBuilder = new ModelBuilder();
        int numChunksX = (Terrain.getInstance().getWidth()*2 + chunkSize - 1) / chunkSize;
        int numChunksY = (Terrain.getInstance().getHeight()*2 + chunkSize - 1) / chunkSize;
        ModelInstance[] terrainInstances = new ModelInstance[numChunksX * numChunksY];
        int index = 0;

        for (int cx = 0; cx < numChunksX; cx++) {
            for (int cy = 0; cy < numChunksY; cy++) {
                int startX = cx * chunkSize;
                int startY = cy * chunkSize;
                int endX = Math.min(startX + chunkSize, Terrain.getInstance().getWidth()*2 - 1);
                int endY = Math.min(startY + chunkSize, Terrain.getInstance().getHeight()*2 - 1);

                modelBuilder.begin();

                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        Vector3 p1 = new Vector3(x - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x][y], y - Terrain.getInstance().getHeight());
                        Vector3 p2 = new Vector3(x + 1 - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x + 1][y], y - Terrain.getInstance().getHeight());
                        Vector3 p3 = new Vector3(x + 1 - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x + 1][y + 1], y + 1 - Terrain.getInstance().getHeight());
                        Vector3 p4 = new Vector3(x - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x][y + 1], y + 1 - Terrain.getInstance().getHeight());

                        Material material = materials[x][y] != null ? materials[x][y] : new Material(TextureAttribute.createDiffuse(textures.get("terrain")));
                        MeshPartBuilder builder = modelBuilder.part("terrain" + cx + "_" + cy, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);

                        // Top face
                        builder.vertex(p1.x, p1.y, p1.z, 0, 1, 0, 0, 0);
                        builder.vertex(p2.x, p2.y, p2.z, 0, 1, 0, 1, 0);
                        builder.vertex(p3.x, p3.y, p3.z, 0, 1, 0, 1, 1);
                        builder.vertex(p4.x, p4.y, p4.z, 0, 1, 0, 0, 1);

                        short i1 = builder.lastIndex();
                        short i2 = (short) (i1 - 1);
                        short i3 = (short) (i2 - 1);
                        short i4 = (short) (i3 - 1);

                        builder.triangle(i1, i2, i3);
                        builder.triangle(i1, i3, i4);

                        // Extend boundary vertices downwards to create volume
                        if (x == 0 || y == 0 || x == Terrain.getInstance().getWidth()*2 - 2 || y == Terrain.getInstance().getHeight()*2 - 2) {
                            createBoundaryFaces(modelBuilder, p1, p2, p3, p4, textures.get("side"));
                        }

                        if (Terrain.getInstance().getObstaclesCoordinates()[x][y].equals("tree")) {
                            Tree tree = new Tree("assets/log.jpeg", "assets/leaves.jpg");
                            tree.setPosition(x - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x][y] - 3f, y - Terrain.getInstance().getHeight());
                            trees.add(tree);
                        } else if (Terrain.getInstance().getObstaclesCoordinates()[x][y].equals("house")) {
                            house = new House("assets/base.png", "assets/planks.png", "assets/door.png", "assets/glass.png", "assets/log.jpeg", 0.3f);
                            house.setPosition(x - Terrain.getInstance().getWidth(), (float) Terrain.getInstance().getHeightCoordinates()[x][y], y - Terrain.getInstance().getHeight());
                            houses.add(house);
                        }
                    }
                }

                Model terrainModel = modelBuilder.end();
                terrainInstances[index++] = new ModelInstance(terrainModel);
            }
        }
        return terrainInstances;
    }
}
