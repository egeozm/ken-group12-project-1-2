package com.gui.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.ode.PhysicsCoefficients;
import com.gui.parser.BiFunctionParser;

import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * The Terrain class represents the game's terrain, including its height map, material map, and obstacles.
 * It uses a provided height function to generate the terrain and randomly places obstacles such as trees and houses.
 */
public class Terrain {
    private static Terrain instance;

    private BiFunction<Double, Double, Double> heightFunction;
    private double kineticFrictionGrass;
    private double kineticFrictionSand;
    private double coefficient = 0.1;
    private double heightCoefficient = 1;
    private double heightBias = 0;
    public static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Material> materials = new HashMap<>();
    private double[][] heightCoordinates;
    private HashMap<String, Double> parameters;
    public String[][] obstaclesCoordinates;
    private Material[][] materialCoordinates;
    private double treeRate = 0.003f;
    private double houseRate = 0.001f;
    private int width;
    private int height;
    private int holeX;
    private int holeZ;

    public int getHoleX() {
        return holeX;
    }

    public int getHoleZ() {
        return holeZ;
    }

    /**
     * Private constructor to prevent instantiation from other classes.
     */
    private Terrain() {
        // Default initialization or leave empty if the instance will be initialized later
    }

    private Terrain(BiFunction<Double, Double, Double> heightFunction, double kineticFrictionGrass, double kineticFrictionSand) {
        this.heightFunction = heightFunction;
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.kineticFrictionSand = kineticFrictionSand;
    }

    private Terrain(String expression, HashMap<String, Double> parameters) {
        init();
        this.parameters = parameters;
        this.width = (int) (double) parameters.get("width");
        this.height = (int) (double) parameters.get("height");
        this.coefficient = parameters.get("functionStep");
        this.heightBias = parameters.get("yBias");
        this.heightCoefficient = parameters.get("heightCoefficient");
        this.treeRate = parameters.get("treeSpawnRate");
        this.houseRate = parameters.get("housesSpawnRate");
        this.heightFunction = BiFunctionParser.parse(expression);
        this.kineticFrictionGrass = 0.08;
        this.kineticFrictionSand = 0.2;
        generateMap(width, height);
    }

    /**
     * Method to get the single instance of the Terrain class.
     */
    public static synchronized Terrain getInstance() {
        if (instance == null) {
            instance = new Terrain();
        }
        return instance;
    }

    public static synchronized Terrain getInstance(BiFunction<Double, Double, Double> heightFunction, double kineticFrictionGrass, double kineticFrictionSand) {
        if (instance == null) {
            instance = new Terrain(heightFunction, kineticFrictionGrass, kineticFrictionSand);
        } else {
            instance.heightFunction = heightFunction;
            instance.kineticFrictionGrass = kineticFrictionGrass;
            instance.kineticFrictionSand = kineticFrictionSand;
        }
        return instance;
    }

    public static synchronized Terrain getInstance(String expression, HashMap<String, Double> parameters) {
        if (instance == null) {
            instance = new Terrain(expression, parameters);
        } else {
            instance.init();
            instance.parameters = parameters;
            instance.width = (int) (double) parameters.get("width");
            instance.height = (int) (double) parameters.get("height");
            instance.coefficient = parameters.get("functionStep");
            instance.heightBias = parameters.get("yBias");
            instance.heightCoefficient = parameters.get("heightCoefficient");
            instance.treeRate = parameters.get("treeSpawnRate");
            instance.houseRate = parameters.get("housesSpawnRate");
            instance.heightFunction = BiFunctionParser.parse(expression);
            instance.kineticFrictionGrass = 0.08;
            instance.kineticFrictionSand = 0.2;
            instance.generateMap(instance.width, instance.height);
        }
        return instance;
    }

    /**
     * Initializes the textures and materials used in the terrain.
     */
    private static void init() {
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

    /**
     * Generates the height map, material map, and obstacle map for the terrain.
     *
     * @param width  the width of the terrain
     * @param height the height of the terrain
     */
    private void generateMap(int width, int height) {
        Random rand = new Random();
        heightCoordinates = new double[2 * width][2 * height];
        materialCoordinates = new Material[2 * width][2 * height];
        obstaclesCoordinates = new String[2 * width][2 * height];
        for (int x = -width; x < width; x++) {
            for (int y = -height; y < height; y++) {
                double heightCell = getHeight(x, y);
                heightCoordinates[x + width][y + height] = heightCell;
                generateMaterialMap(x + width, y + height, heightCell);
                if ((int) (double) parameters.get("xBall") != x && (int) (double) parameters.get("zBall") != y) {
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
                } else {
                    if (materialCoordinates[x + width][y + height] == materials.get("water"))
                        obstaclesCoordinates[x + width][y + height] = "water";
                    else
                        obstaclesCoordinates[x + width][y + height] = "0";
                }
            }
        }
        for (int x = -width + 1; x < width - 1; x++)
            for (int y = -height + 1; y < height - 1; y++) {
                if ((int) (double) parameters.get("xBall") != x && (int) (double) parameters.get("zBall") != y) {
                    if (materialCoordinates[x + width][y + height] != materials.get("water") && obstaclesCoordinates[x + width][y + height].equals("0") && rand.nextDouble(1) < treeRate) {
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

    /**
     * Generates the material map based on the height map.
     *
     * @param x      the x-coordinate
     * @param y      the y-coordinate
     * @param height the height at the specified coordinates
     */
    public void generateMaterialMap(int x, int y, double height) {
        Material material = materials.get("grass");
        double divisionCoefficient = 1;
        if (height < 0 * divisionCoefficient)
            material = materials.get("water");
        else if (height < 0.4 * divisionCoefficient)
            material = materials.get("grass");
        materialCoordinates[x][y] = material;
    }

    /**
     * Generates the golf hole on the terrain.
     */
    private void generateGolfHole() {
        int holeX;
        int holeZ = 0;
        Random random = new Random();
        if (parameters.get("xHole").equals(Double.NaN)) {
            do {
                holeX = random.nextInt(width * 2 - 1);
            } while (!obstaclesCoordinates[holeX][holeZ].equals("0"));
        } else {
            holeX = (int) (double) parameters.get("xHole") + width;
        }
        if (parameters.get("zHole").equals(Double.NaN)) {
            do {
                holeZ = random.nextInt(height * 2 - 1);
            } while (!obstaclesCoordinates[holeX][holeZ].equals("0"));
        } else {
            holeZ = (int) (double) parameters.get("zHole") + height;
        }
        obstaclesCoordinates[holeX][holeZ] = "hole";
        materialCoordinates[holeX][holeZ] = materials.get("hole");
        this.holeX = holeX - width;
        this.holeZ = holeZ - height;
    }

    /**
     * Returns the height of the terrain at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the height of the terrain at the specified coordinates
     */
    public double getHeight(double x, double y) {
        return heightFunction.apply(x * coefficient, y * coefficient) * heightCoefficient + heightBias;
    }

    /**
     * Returns the slope of the terrain at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return an array containing the slopes in the x and y directions
     */
    public double[] getSlope(double x, double y) {
        double epsilon = 1e-6;
        double height = getHeight(x, y);
        double dHdX = (getHeight(x + epsilon, y) - height) / epsilon;
        double dHdY = (getHeight(x, y + epsilon) - height) / epsilon;
        return new double[]{dHdX, dHdY};
    }

    /**
     * Checks if the specified coordinates are in a sandpit.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the coordinates are in a sandpit, false otherwise
     */
    public boolean isSand(double x, double y) {
        boolean sandPit1 = (x - 4) * (x - 4) + (y - 2) * (y - 2) < 1.5; // circle at (4, 2)
        boolean sandPit2 = (x + 3) * (x + 3) + (y + 2) * (y + 2) < 2; // circle at (-3, -2)
        return sandPit1 || sandPit2;
    }

    /**
     * Returns the kinetic friction at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the kinetic friction at the specified coordinates
     */
    public double getKineticFriction(double x, double y) {
        if (isSand(x, y)) {
            return kineticFrictionSand;
        }
        return kineticFrictionGrass;
    }

    /**
     * Returns the width of the terrain.
     *
     * @return the width of the terrain
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the terrain.
     *
     * @return the height of the terrain
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the terrain.
     *
     * @param height the height of the terrain
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the height coordinates of the terrain.
     *
     * @return a 2D array containing the height coordinates of the terrain
     */
    public double[][] getHeightCoordinates() {
        return heightCoordinates;
    }

    /**
     * Returns the material coordinates of the terrain.
     *
     * @return a 2D array containing the material coordinates of the terrain
     */
    public Material[][] getMaterialCoordinates() {
        return materialCoordinates;
    }

    /**
     * Returns the obstacle coordinates of the terrain.
     *
     * @return a 2D array containing the obstacle coordinates of the terrain
     */
    public String[][] getObstaclesCoordinates() {
        return obstaclesCoordinates;
    }
}
