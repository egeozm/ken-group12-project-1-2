package com.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.example.PhysicsCoefficients;
import com.gui.BiFunctionParser;

import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;

import static com.example.FunctionParser2.eval;

public class Terrain {
    private BiFunction<Double, Double, Double> heightFunction;
    private double kineticFrictionGrass;
    private double staticFrictionGrass;
    private double kineticFrictionSand;
    private double staticFrictionSand;
    private double coef = 0.1;
    private double heightCoef = 1;
    private double heightBias = 0;
    private double divisionCoef = 1;
    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
    private static HashMap<String, Material> materials = new HashMap<String, Material>();
    private double[][] heightCoordinates;
    HashMap<String, Double> parameters;

    public String[][] getObstaclesCoordinates() {
        return obstaclesCoordinates;
    }

    private String[][] obstaclesCoordinates;
    private Material[][] materialCoordinates;
    private double treeRate = 0.003f;
    private double houseRate = 0.001f;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int width;
    private int height;
    public double[][] getHeightCoordinates() {
        return heightCoordinates;
    }

    public Material[][] getMaterialCoordinates() {
        return materialCoordinates;
    }
    public static void main(String[] args) {
        // height function for the terrain to be generated
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));
        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

        // this creates the terrain according to the height function
        Terrain terrain = new Terrain(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getStaticFrictionGrass(), coefficients.getKineticFrictionSand(), coefficients.getStaticFrictionSand());
    }

    public Terrain(BiFunction<Double, Double, Double> heightFunction, double kineticFrictionGrass, double staticFrictionGrass, double kineticFrictionSand, double staticFrictionSand) {
        this.heightFunction = heightFunction;
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.staticFrictionGrass = staticFrictionGrass;
        this.kineticFrictionSand = kineticFrictionSand;
        this.staticFrictionSand = staticFrictionSand;
    }
    public Terrain(String expression, double kineticFrictionGrass, double staticFrictionGrass, double kineticFrictionSand, double staticFrictionSand) {
        init();
        this.heightFunction = BiFunctionParser.parse(expression);
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.staticFrictionGrass = staticFrictionGrass;
        this.kineticFrictionSand = kineticFrictionSand;
        this.staticFrictionSand = staticFrictionSand;
    }
    public Terrain(String expression, HashMap<String, Double> parameters) {
        init();
        this.parameters = parameters;
        this.width = (int)(double)parameters.get("width");
        this.height = (int)(double)parameters.get("height");
        this.coef = (double)parameters.get("functionStep");
        this.heightBias = (double)parameters.get("yBias");
        this.heightCoef = (double)parameters.get("heightCoef");
        this.treeRate = (double)parameters.get("treeSpawnRate");
        this.houseRate = (double)parameters.get("housesSpawnRate");
        this.heightFunction = BiFunctionParser.parse(expression);
        this.kineticFrictionGrass = 0.08;
        this.staticFrictionGrass = 0.15;
        this.kineticFrictionSand = 0.2;
        this.staticFrictionSand = 0.25;
        generateMap(width, height);
        //Debugger.printMatrix(heightCoordinates);
    }
    private void generateMap(int width, int height) {
        Random rand = new Random();
        heightCoordinates = new double[2*width][2*height];
        materialCoordinates = new Material[2*width][2*height];
        obstaclesCoordinates = new String[2*width][2*height];
        for (int x = -width; x < width; x++) {
            for (int y = -height; y < height; y++) {
                Double heightCell = getHeight(x, y);
                heightCoordinates[x+width][y+height] = heightCell;
                generateMaterialMap(x+width, y+height, heightCell);
                if((int)(double)parameters.get("xBall") != x && (int)(double)parameters.get("zBall") != y) {
                    if (obstaclesCoordinates[x + width][y + height] == null && rand.nextDouble(1) < houseRate && materialCoordinates[x + width][y + height] != materials.get("water")) {
                        obstaclesCoordinates[x + width][y + height] = "house";
                        if (x + width - 1 != -1 && y + height - 1 != -1)
                            obstaclesCoordinates[x + width - 1][y + height - 1] = "houseExtension";
                        if (y + height - 1 != -1)
                            obstaclesCoordinates[x + width][y + height - 1] = "houseExtension";
                        if (x + width - 1 != -1)
                            obstaclesCoordinates[x + width - 1][y + height] = "houseExtension";
                        if (x + width + 1 != 2 * width && y + height - 1 != -1)
                            obstaclesCoordinates[x + width + 1][y + height - 1] = "houseExtension";
                        if (x + width + 1 != 2 * width)
                            obstaclesCoordinates[x + width + 1][y + height] = "houseExtension";
                        if (x + width - 1 != -1 && y + height + 1 != 2 * height)
                            obstaclesCoordinates[x + width - 1][y + height + 1] = "houseExtension";
                        if (y + height + 1 != 2 * height)
                            obstaclesCoordinates[x + width][y + height + 1] = "houseExtension";
                        if (x + height + 1 != 2 * width && y + height + 1 != 2 * height)
                            obstaclesCoordinates[x + width + 1][y + height + 1] = "houseExtension";
                    } else if (materialCoordinates[x + width][y + height] == materials.get("water"))
                        obstaclesCoordinates[x + width][y + height] = "water";
                    else
                        obstaclesCoordinates[x + width][y + height] = "0";
                }
                else {
                    if (materialCoordinates[x + width][y + height] == materials.get("water"))
                        obstaclesCoordinates[x + width][y + height] = "water";
                    else
                        obstaclesCoordinates[x + width][y + height] = "0";
                }
            }
            //Debugger.printMatrix(obstaclesCoordinates);
        }
        for (int x = -width+1; x < width-1; x++)
            for (int y = -height+1; y < height-1; y++){
                if((int)(double)parameters.get("xBall") != x && (int)(double)parameters.get("zBall") != y){
                    if(materialCoordinates[x+width][y+height] != materials.get("water") && obstaclesCoordinates[x+width][y+height].equals("0") && rand.nextDouble(1) < treeRate) {
                        if (obstaclesCoordinates[x + width - 1][y + height - 1].equals("0") && obstaclesCoordinates[x + width - 1][y + height].equals("0") &&
                                obstaclesCoordinates[x + width - 1][y + height + 1].equals("0") && obstaclesCoordinates[x + width][y + height - 1].equals("0") &&
                                obstaclesCoordinates[x + width][y + height].equals("0") && obstaclesCoordinates[x + width][y + height + 1].equals("0") &&
                                obstaclesCoordinates[x + width + 1][y + height - 1].equals("0") && obstaclesCoordinates[x + width + 1][y + height].equals("0") &&
                                obstaclesCoordinates[x + width + 1][y + height + 1].equals("0"))
                            obstaclesCoordinates[x + width][y + height] = "tree";
                    }
                }
            }
        generateGolfHole();
    }
    public void generateMaterialMap(int x, int y, double height){
        Material material = materials.get("grass");
        /*if(height < -0.5*divisionCoef)
            material = materials.get("water");
        else*/ if(height < 0*divisionCoef)
            material = materials.get("water");
        else if(height < 0.4*divisionCoef)
            material = materials.get("grass");
        /*else if(height < 0.8*divisionCoef)
            material = materials.get("rock");
        else
            material = materials.get("snow");*/
        materialCoordinates[x][y] = material;
    }
    private void generateGolfHole(){
        int holeX = 0;
        int holeZ = 0;
        Random random = new Random();
        if(parameters.get("xHole").equals(Double.NaN)) {
            holeX = random.nextInt(width * 2 - 1);
            while (!obstaclesCoordinates[holeX][holeZ].equals("0")) {
                holeX = random.nextInt(width * 2 - 1);
            }
        }
        else {
            holeX = (int)(double)parameters.get("xHole")+width;
        }
        if(parameters.get("zHole").equals(Double.NaN)){
            holeZ = random.nextInt(height * 2 - 1);
            while (!obstaclesCoordinates[holeX][holeZ].equals("0")) {
                holeZ = random.nextInt(height * 2 - 1);
            }
        }
        else {
            holeZ = (int)(double)parameters.get("zHole")+height;
        }
        obstaclesCoordinates[holeX][holeZ] = "hole";
        materialCoordinates[holeX][holeZ] = materials.get("hole");

    }
    public double getHeight(double x, double y) {
        return heightFunction.apply(x*coef, y*coef)*heightCoef+heightBias;
    }
    public Material getHeightMaterial(int x, int y) {
        return materialCoordinates[x][y];
    }
    private static void init(){
        textures.put("waterTexture", new Texture(Gdx.files.internal("assets/water.jpeg")));
        textures.put("sandTexture", new Texture(Gdx.files.internal("assets/sand.jpeg")));
        textures.put("grassTexture", new Texture(Gdx.files.internal("assets/grass.png")));
        textures.put("rockTexture", new Texture(Gdx.files.internal("assets/rock.png")));
        textures.put("snowTexture", new Texture(Gdx.files.internal("assets/snow.png")));
        textures.put("holeTexture", new Texture(Gdx.files.internal("assets/golf-hole.png")));
        materials.put("water", new Material(TextureAttribute.createDiffuse(textures.get("waterTexture"))));
        materials.put("sand", new Material(TextureAttribute.createDiffuse(textures.get("sandTexture"))));
        materials.put("grass", new Material(TextureAttribute.createDiffuse(textures.get("grassTexture"))));
        materials.put("rock", new Material(TextureAttribute.createDiffuse(textures.get("rockTexture"))));
        materials.put("snow", new Material(TextureAttribute.createDiffuse(textures.get("snowTexture"))));
        materials.put("hole", new Material(TextureAttribute.createDiffuse(textures.get("holeTexture"))));
    }

    public double[] getSlope(double x, double y) {
        double epsilon = 1e-6;
        double height = getHeight(x, y);
        double dhdx = (getHeight(x + epsilon, y) - height) / epsilon;
        double dhdy = (getHeight(x, y + epsilon) - height) / epsilon;
        return new double[]{dhdx, dhdy};
    }
    public Material getMaterial(double height, double heightCoef, double heightBias){
        height = height*heightCoef+heightBias;
        Material material = materials.get("grass");
        /*if(height < -0.5*divisionCoef)
            material = materials.get("water");
        else*/ if(height < 0*divisionCoef)
            material = materials.get("water");
        else if(height < 0.4*divisionCoef)
            material = materials.get("grass");
        /*else if(height < 0.8*divisionCoef)
            material = materials.get("rock");
        else
            material = materials.get("snow");*/
        return material;
    }
    /*public double[] getCurvature(double x, double y) {
        double epsilon = 1e-6;
        double height = getHeight(x, y);
        double d2hdx2 = (getHeight(x + epsilon, y) - 2 * height + getHeight(x - epsilon, y)) / (epsilon * epsilon);
        double d2hdy2 = (getHeight(x, y + epsilon) - 2 * height + getHeight(x, y - epsilon)) / (epsilon * epsilon);
        double d2hdxdy = (getHeight(x + epsilon, y + epsilon) - getHeight(x + epsilon, y - epsilon) - getHeight(x - epsilon, y + epsilon) + getHeight(x - epsilon, y - epsilon)) / (4 * epsilon * epsilon);
        return new double[]{d2hdx2, d2hdy2, d2hdxdy};
    }*/

    public boolean isSand(double x, double y) {
        boolean sandPit1 = (x - 4) * (x - 4) + (y - 2) * (y - 2) < 1.5; // circle at (4, 2)
        boolean sandPit2 = (x + 3) * (x + 3) + (y + 2) * (y + 2) < 2; // circle at (-3, -2)
        return sandPit1 || sandPit2;
    }

    public boolean isWater(double x, double y) {
        return getHeight(x, y) < 0;
    }

    public double getKineticFriction(double x, double y) {
        if (isSand(x, y)) {
            return kineticFrictionSand;
        }
        return kineticFrictionGrass;
    }

    public double getStaticFriction(double x, double y) {
        if (isSand(x, y)) {
            return staticFrictionSand;
        }
        return staticFrictionGrass;
    }
}


