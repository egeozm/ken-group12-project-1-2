package com.gui.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ode.PhysicsCoefficients;
import com.gui.camera.CameraController;
import com.gui.debbugers.Debugger3D;
import com.gui.labels.UILabel;
import com.gui.labels.WinLabel;
import com.gui.objects.*;
import com.gui.terrain.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.gui.game.VertexProcessing.initVertices;
import static com.gui.objects.GolfBall.setRandomBallPosition;
import static com.gui.terrain.Terrain.getInstance;

public class Golf3D implements Screen {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;

    public static CameraController getCameraController() {
        return cameraController;
    }

    public static Material[][] materials;

    private static CameraController cameraController;
    public static HashMap<String, Texture> textures;
    private SkySphere skySphere;
    private GolfBall golfBall;
    private GolfBall advancedAI;
    private final Music gameMusic;
    private final String function;
    private final int width;
    private final int height;
    private Terrain terrain;

    public static House house;
    public static ArrayList<Tree> trees;

    public static ArrayList<Wall> walls;
    public static ArrayList<House> houses;
    private Trajectory trajectory;
    private double[][] heightMap;
    private String[][] obstacles;
    private boolean kickingMode;
    private Vector3 kickDirection;
    private final Debugger3D debugger;
    private UILabel bottomLeftLabel;
    private UILabel coordinatesLabel;
    private UILabel coordinatesAILabel;
    private UILabel shotsLabel;
    private double kickPower;
    private WinLabel winLabel;
    private WinLabel winLabelAI;
    private final HashMap<String, Double> parameters;
    private int numberOfShots = 0;
    private final PhysicsCoefficients physicsCoefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

    private Renderer3D renderer3D;

    public Golf3D(Music gameMusic, String function, HashMap<String, Double> parameters) {
        width = (int) (double) parameters.get("width");
        height = (int) (double) parameters.get("height");
        this.parameters = parameters;
        this.gameMusic = gameMusic;
        this.function = function;
        this.kickingMode = false;
        this.kickDirection = new Vector3();
        this.debugger = new Debugger3D();
        kickPower = 1;
    }

    private double[] goalxz() {
        int goalx = 0;
        int goalz = 0;
        for (int i = 0; i < terrain.obstaclesCoordinates.length; i++) {
            for (int j = 0; j < terrain.obstaclesCoordinates[1].length; j++) {
                if (terrain.obstaclesCoordinates[i][j].equals("hole")) {
                    goalx = i;
                    goalz = j;
                }
            }
        }
        double vx = goalx - terrain.getWidth();
        double vz = goalz - terrain.getHeight();
        return new double[]{vx, vz};
    }

    @Override
    public void show() {
        modelBatch = new ModelBatch();
        textures = new HashMap<>();

        // Load textures
        textures.put("terrain", new Texture(Gdx.files.internal("assets/dirt.jpg")));
        textures.put("side", new Texture(Gdx.files.internal("assets/dirt.jpg")));

        skySphere = new SkySphere("assets/skybox_top.jpg", "assets/skybox_front.jpg");

        // Set up the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 100f, 200f);
        camera.lookAt(50, 100, 0);
        camera.near = 1f;
        camera.far = 30000f;
        camera.update();

        // Set up the camera controller
        cameraController = new CameraController(camera);

        terrain = getInstance(function, parameters);
        heightMap = terrain.getHeightCoordinates();
        materials = terrain.getMaterialCoordinates();
        obstacles = terrain.getObstaclesCoordinates();
        trees = new ArrayList<>();
        houses = new ArrayList<>();
        walls = new ArrayList<>();

        ModelInstance[] terrainInstance = initVertices();

        // Set up the environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));
        winLabel = new WinLabel("You Win");
        winLabelAI = new WinLabel("Advanced Bot Wins");
        golfBall = new GolfBall("assets/golfball.jpeg", terrain, winLabel, 1);
        advancedAI = new GolfBall("assets/golfball.jpeg", terrain, winLabelAI, 10);
        trajectory = new Trajectory(1);
        kickingMode = false;
        kickDirection = new Vector3(1, 0, 0);
        setRandomBallPosition(golfBall, parameters);
        setRandomBallPosition(advancedAI, parameters);
        coordinatesLabel = new UILabel("Player Coordinates:" + "\n" + "X: " + golfBall.getPosition().x + "\n" + "Y: " + (float) terrain.getHeight(golfBall.getPosition().x, golfBall.getPosition().z) + "\n" + "Z: " + golfBall.getPosition().z + "\n", new Vector3(camera.viewportWidth / 2, -camera.viewportHeight / 2.2f, 0));
        coordinatesAILabel = new UILabel("AI Coordinates:" + "\n" + "X: " + advancedAI.getPosition().x + "\n" + "Y: " + (float) terrain.getHeight(advancedAI.getPosition().x, advancedAI.getPosition().z) + "\n" + "Z: " + advancedAI.getPosition().z + "\n", new Vector3(camera.viewportWidth / 3, -camera.viewportHeight / 2.2f, 0));
        shotsLabel = new UILabel("Shots done: " + numberOfShots, new Vector3(camera.viewportWidth / 2f - 100f, camera.viewportHeight / 2f, 0));
        bottomLeftLabel = new UILabel("Power: " + kickPower, new Vector3(camera.viewportWidth / 2, camera.viewportHeight / 2, 0));

        renderer3D = new Renderer3D(camera, modelBatch, environment, skySphere, trajectory, bottomLeftLabel, shotsLabel, coordinatesLabel, coordinatesAILabel, winLabel, winLabelAI);

        for (ModelInstance instance : terrainInstance) {
            renderer3D.addRenderableObject("terrain", instance);
        }

        for (Tree tree : trees) {
            renderer3D.addRenderableObject("trees", tree.getTrunkInstance());
            renderer3D.addRenderableObject("trees", tree.getLeavesInstance());
        }
        for (Wall wall : walls) {
            renderer3D.addRenderableObject("walls", wall.getTrunkInstance());
            renderer3D.addRenderableObject("walls", wall.getLeavesInstance());
        }
        for (House house : houses) {
            renderer3D.addRenderableObject("houses", house.getWallInstance());
            renderer3D.addRenderableObject("houses", house.getRoofInstance());
            renderer3D.addRenderableObject("houses", house.getDoorInstance());
            renderer3D.addRenderableObject("houses", house.getWindowInstance());
        }
        InputHandler.initialize(golfBall, advancedAI, trajectory, (int) kickPower, kickingMode, kickDirection, numberOfShots, bottomLeftLabel, shotsLabel, renderer3D, camera, terrain, debugger);
    }


    @Override
    public void render(float delta) {
        cameraController.update(Gdx.graphics.getDeltaTime());

        if (trajectory.isKickingMode()) {
            Vector3 ballPosition = golfBall.getInstance().transform.getTranslation(new Vector3());
            Vector3 direction = trajectory.getDirection();

            Vector3 cameraPosition = new Vector3(ballPosition).sub(direction.nor().scl(10)).add(0, 5, 0);
            camera.position.set(cameraPosition);
            camera.lookAt(ballPosition);
            camera.up.set(Vector3.Y);
            camera.update();
        }

        renderer3D.render(delta, golfBall, advancedAI);

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        gameMusic.stop();
    }

    @Override
    public void dispose() {
        renderer3D.dispose();
        debugger.dispose();
    }
}
