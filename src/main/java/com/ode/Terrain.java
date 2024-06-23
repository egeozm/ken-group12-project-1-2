package com.ode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gui.parser.BiFunctionParser;

import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * The Terrain class generates a terrain map with height, material, and obstacle information.
 * It uses a height function to determine the terrain's height at any given point.
 */
public class Terrain {
    private final BiFunction<Double, Double, Double> heightFunction;
    private final double coefficient;
    private final double heightCoefficient;
    private final double heightBias;
    private static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Material> materials = new HashMap<>();
    HashMap<String, Double> parameters;

    private String[][] obstaclesCoordinates;
    private Material[][] materialCoordinates;
    private final double treeRate;
    private final double houseRate;

    private final int width;
    private final int height;

    /**
     * Main method to demonstrate terrain generation.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Height function for the terrain to be generated
        new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);
    }

    /**
     * Constructs a Terrain instance with specified parameters and height function expression.
     *
     * @param expression The height function expression as a string.
     * @param parameters The map of parameters for terrain generation.
     */
    public Terrain(String expression, HashMap<String, Double> parameters) {
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
        generateMap(width, height);
    }

    /**
     * Generates the terrain map including height, material, and obstacle information.
     *
     * @param width  The width of the terrain.
     * @param height The height of the terrain.
     */
    private void generateMap(int width, int height) {
        Random rand = new Random();
        double[][] heightCoordinates = new double[2 * width][2 * height];
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
            // Debugger.printMatrix(obstaclesCoordinates);
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
     * Generates the material map for the terrain based on height values.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param height The height at the given coordinate.
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
     * Generates a golf hole at a random position on the terrain.
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

    }

    /**
     * Gets the height of the terrain at a given point.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The height at the given point.
     */
    public double getHeight(double x, double y) {
        return heightFunction.apply(x * coefficient, y * coefficient) * heightCoefficient + heightBias;
    }

    /**
     * Initializes textures and materials for the terrain.
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
}
