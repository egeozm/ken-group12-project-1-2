package com.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import java.util.HashMap;
import java.util.Random;

import static com.example.FunctionParser2.eval;

public class TestTerrain {
    private static int width;
    private static int height;
    private static testCell[][] heightMap;
    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
    private static HashMap<String, Material> materials = new HashMap<String, Material>();
    private static void init(){
        textures.put("waterTexture", new Texture(Gdx.files.internal("assets/water.jpeg")));
        textures.put("sandTexture", new Texture(Gdx.files.internal("assets/sand.jpeg")));
        textures.put("grassTexture", new Texture(Gdx.files.internal("assets/grass.png")));
        textures.put("rockTexture", new Texture(Gdx.files.internal("assets/rock.png")));
        textures.put("snowTexture", new Texture(Gdx.files.internal("assets/snow.png")));
        materials.put("water", new Material(TextureAttribute.createDiffuse(textures.get("waterTexture"))));
        materials.put("sand", new Material(TextureAttribute.createDiffuse(textures.get("sandTexture"))));
        materials.put("grass", new Material(TextureAttribute.createDiffuse(textures.get("grassTexture"))));
        materials.put("rock", new Material(TextureAttribute.createDiffuse(textures.get("rockTexture"))));
        materials.put("snow", new Material(TextureAttribute.createDiffuse(textures.get("snowTexture"))));
    }
    public TestTerrain(int width, int height, String function) {
        init();
        this.width = width;
        this.height = height;
        this.heightMap = new testCell[width*2][height*2];
        generateDefaultMap(0.1, 4.0, -15.0, function);
    }

    private void generateDefaultMap(Double coef, Double heightCoef, Double heightBias, String function) {
        Random rand = new Random();
        for (int x = -width; x < width; x++) {
            for (int y = -height; y < height; y++) {
                double xParam = x*coef;
                double yParam = y*coef;
                HashMap<String, Double> inVals = new HashMap<String, Double>();
                inVals.put("x", xParam);
                inVals.put("y", yParam);
                Double heightCell = (eval(function, inVals));
                heightMap[x+width][y+height] = new testCell(heightCell, heightCoef, heightBias);

            }
        }
    }

    public static testCell[][] getHeightMap() {
        return heightMap;
    }

    public static TestTerrain default_map(String function, int width, int height) {
        TestTerrain terrainn = new TestTerrain(width, height, function);
        return terrainn;
    }
    class testCell{

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        private Material material;
        private Double height;

        testCell(Double height, Material material) {
            this.height = height;
            this.material = material;
        }
        testCell(Double height, Double heightCoef, Double heightBias) {
            this.height = height*heightCoef+heightBias;
            this.material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
            if(height < -0.5)
                this.material = materials.get("water");
            else if(height < 0)
                this.material = materials.get("sand");
            else if(height < 0.4)
                this.material = materials.get("grass");
            else if(height < 0.8)
                this.material = materials.get("rock");
            else
                this.material = materials.get("snow");
        }
    }
}
