package com.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.example.Ball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Golf3D implements Screen {
    private GameLauncher game;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelInstance[] terrainInstances;
    private Environment environment;
    private CameraController cameraController;
    private HashMap<String, Texture> textures;
    private SkySphere skySphere;
    private GolfBall golfBall;
    private Sun sun;
    private Music gameMusic;
    private String function;
    private int width;
    private int height;
    private Terrain terrain;
    private Tree tree;
    private House house;
    private ArrayList<Tree> trees;
    private ArrayList<House> houses;
    private Trajectory trajectory = new Trajectory(1);
    private double[][] heightMap;
    private String[][] obstacles;
    private boolean kickingMode;
    private Vector3 kickDirection;
    private Debugger3D debugger;
    private UILabel bottomLeftLabel;
    private double kickPower;
    private WinLabel winLabel;
    HashMap<String, Double> parameters;

    public Golf3D(GameLauncher game, Music gameMusic, String function, HashMap<String, Double> parameters) {
        width = (int)(double)parameters.get("width");
        height = (int)(double)parameters.get("height");
        this.parameters = parameters;
        this.game = game;
        this.gameMusic = gameMusic;
        this.function = function;
        this.width = width;
        this.height = height;
        this.kickingMode = false;
        this.kickDirection = new Vector3();
        this.debugger = new Debugger3D();
        kickPower = 1;
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

        terrain = new Terrain(function, parameters);
        heightMap = terrain.getHeightCoordinates();
        Material[][] materials = terrain.getMaterialCoordinates();
        obstacles = terrain.getObstaclesCoordinates();
        trees = new ArrayList<Tree>();
        houses = new ArrayList<House>();
        int mapWidth = heightMap.length;
        int mapHeight = heightMap[0].length;

        // Create the terrain model
        int chunkSize = 50;  // Adjust chunk size to fit within vertex limit
        ModelBuilder modelBuilder = new ModelBuilder();

        int numChunksX = (mapWidth + chunkSize - 1) / chunkSize;
        int numChunksY = (mapHeight + chunkSize - 1) / chunkSize;
        terrainInstances = new ModelInstance[numChunksX * numChunksY];

        int index = 0;
        float platformHeight = -10f;  // Height to which boundary vertices extend downwards

        for (int cx = 0; cx < numChunksX; cx++) {
            for (int cy = 0; cy < numChunksY; cy++) {
                int startX = cx * chunkSize;
                int startY = cy * chunkSize;
                int endX = Math.min(startX + chunkSize, mapWidth - 1);
                int endY = Math.min(startY + chunkSize, mapHeight - 1);

                modelBuilder.begin();

                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        Vector3 p1 = new Vector3(x - width, (float) heightMap[x][y], y - height);
                        Vector3 p2 = new Vector3(x + 1 - width, (float) heightMap[x + 1][y], y - height);
                        Vector3 p3 = new Vector3(x + 1 - width, (float) heightMap[x + 1][y + 1], y + 1 - height);
                        Vector3 p4 = new Vector3(x - width, (float) heightMap[x][y + 1], y + 1 - height);

                        Material material = materials[x][y] != null ? materials[x][y] : new Material(TextureAttribute.createDiffuse(textures.get("terrain")));
                        MeshPartBuilder builder = modelBuilder.part("terrain" + cx + "_" + cy, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, material);

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
                        if (x == 0 || y == 0 || x == mapWidth - 2 || y == mapHeight - 2) {
                            createBoundaryFaces(builder, modelBuilder, p1, p2, p3, p4, platformHeight, textures.get("side"));
                        }

                        if(obstacles[x][y].equals("tree")){
                            tree = new Tree("assets/log.jpeg", "assets/leaves.jpg");
                            tree.setPosition(x- width, (float) heightMap[x][y]-3f, y - height);
                            trees.add(tree);
                        }
                        else if(obstacles[x][y].equals("house")){
                            house = new House("assets/base.png", "assets/planks.png", "assets/door.png", "assets/glass.png", "assets/log.jpeg", 0.3f);
                            house.setPosition(x- width, (float) heightMap[x][y], y - height);
                            houses.add(house);
                        }
                    }
                }

                Model terrainModel = modelBuilder.end();
                terrainInstances[index++] = new ModelInstance(terrainModel);
            }
        }

        // Set up the environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));
        winLabel = new WinLabel();
        // Initialize the golf ball
        golfBall = new GolfBall("assets/golfball.jpeg", terrain, winLabel);
        trajectory = new Trajectory(1);
        kickingMode = false;
        kickDirection = new Vector3(1, 0, 0); // Initial kick direction
        // Set the golf ball position randomly or allow player to select position
        setRandomBallPosition();
        bottomLeftLabel = new UILabel("Power: " + kickPower);
    }
    private void setRandomBallPosition() {
        Random random = new Random();
        double ballX = 0;
        double ballZ = 0;
        if(parameters.get("xBall").equals(Double.NaN)) {
            ballX = random.nextInt(width * 2 - 1);
            while (!obstacles[(int) ballX][(int) ballZ].equals("0")) {
                ballX = random.nextInt(width * 2 - 1);
            }
        }
        else {
            ballX = (double)parameters.get("xBall")+width;
        }
        if(parameters.get("zBall").equals(Double.NaN)){
            ballZ = random.nextInt(height * 2 - 1);
            while (!obstacles[(int) ballX][(int) ballZ].equals("0")) {
                ballZ = random.nextInt(height * 2 - 1);
            }
        }
        else {
            ballZ = (int)(double)parameters.get("zBall")+height;
        }
        float ballY = (float) heightMap[(int) ballX][(int)ballZ]; // No offset to perfectly align with the terrain
        golfBall.setPosition((float) (ballX - width), ballY + 0.5f, (float) (ballZ - height));
    }

    public void setBallPosition(float x, float z) {
        float ballY = (float) (double) terrain.getHeight((int)x, (int) z); // No offset to perfectly align with the terrain
        golfBall.setPosition(x - width, ballY, z - height);
    }
    private void createBoundaryFaces(MeshPartBuilder builder, ModelBuilder modelBuilder, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, float platformHeight, Texture sideTexture) {
        float extendHeight = 10f;  // Extend vertices downwards by 10 units

        Material sideMaterial = new Material(TextureAttribute.createDiffuse(sideTexture));
        builder = modelBuilder.part("terrain" + p1.x + "_" + p1.z, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, sideMaterial);
        Vector3 p1Bottom = new Vector3(p1.x, p1.y - extendHeight, p1.z);
        Vector3 p2Bottom = new Vector3(p2.x, p2.y - extendHeight, p2.z);
        Vector3 p3Bottom = new Vector3(p3.x, p3.y - extendHeight, p3.z);
        Vector3 p4Bottom = new Vector3(p4.x, p4.y - extendHeight, p4.z);

        // Create vertical faces to form the sides of the terrain
        createVerticalFace(builder, p1, p2, p2Bottom, p1Bottom, sideMaterial);  // Front face
        createVerticalFace(builder, p2, p3, p3Bottom, p2Bottom, sideMaterial);  // Right face
        createVerticalFace(builder, p3, p4, p4Bottom, p3Bottom, sideMaterial);  // Back face
        createVerticalFace(builder, p4, p1, p1Bottom, p4Bottom, sideMaterial);  // Left face
    }

    private void createVerticalFace(MeshPartBuilder builder, Vector3 top1, Vector3 top2, Vector3 bottom2, Vector3 bottom1, Material sideMaterial) {
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
    private void resetCamera(){
        Vector3 previousPosition = camera.position;
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(previousPosition.x, previousPosition.y, previousPosition.z);
        camera.lookAt(new Vector3(golfBall.getPosition().x, golfBall.getPosition().y, golfBall.getPosition().z));
        camera.up.set(Vector3.Y);
        camera.near = 1f;
        camera.far = 30000f;
        camera.update();

        // Set up the camera controller
        cameraController = new CameraController(camera);
    }
    @Override
    public void render(float delta) {
        cameraController.update(Gdx.graphics.getDeltaTime());

        if (trajectory.isKickingMode()) {
            Vector3 ballPosition = golfBall.getInstance().transform.getTranslation(new Vector3());
            Vector3 direction = trajectory.getDirection();

            // Set the camera position behind the ball, orthogonal to the trajectory
            Vector3 cameraPosition = new Vector3(ballPosition).sub(direction.nor().scl(10)).add(0, 5, 0);
            camera.position.set(cameraPosition);
            camera.lookAt(ballPosition);
            camera.up.set(Vector3.Y);
            camera.update();
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        golfBall.update(delta);
        modelBatch.begin(camera);
        skySphere.render(modelBatch, environment);
        trajectory.update(golfBall.getInstance().transform.getTranslation(new Vector3()));
        modelBatch.render(trajectory.getInstance(), environment);
        for (ModelInstance instance : terrainInstances) {
            modelBatch.render(instance, environment);
        }
        for (Tree tree : trees) {
            modelBatch.render(tree.getTrunkInstance(), environment);
            modelBatch.render(tree.getLeavesInstance(), environment);
        }
        for(House house : houses){
            modelBatch.render(house.getWallInstance(), environment);
            modelBatch.render(house.getRoofInstance(), environment);
            modelBatch.render(house.getDoorInstance(), environment);
            modelBatch.render(house.getWindowInstance(), environment);
            //modelBatch.render(house.getBaseInstance(), environment);
        }
        modelBatch.render(golfBall.getInstance(), environment);
        modelBatch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            trajectory.toggleKickingMode();
        }
        if (debugger.isDebuggerEnabled()) {
            debugger.render(modelBatch, environment);
        }
        bottomLeftLabel.render();
        winLabel.render();

        handleInput();
    }
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            kickingMode = !kickingMode;
            if (kickingMode) {
                // Attach camera to ball in kicking mode
                camera.position.set(golfBall.getInstance().transform.getTranslation(new Vector3()).add(0, 10, 10));
                camera.lookAt(golfBall.getInstance().transform.getTranslation(new Vector3()));
                camera.update();
            }
        }

        if (kickingMode) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
                System.out.println("aboba");
                kickPower = Math.min(kickPower + 1, 100);
                trajectory.setKickingPower(kickPower);
                bottomLeftLabel.setText("Power: " + kickPower);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
                kickPower = Math.max(kickPower - 1, 0);
                trajectory.setKickingPower(kickPower);
                bottomLeftLabel.setText("Power: " + kickPower);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                kickDirection.z -= 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                kickDirection.z += 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                kickDirection.x -= 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                kickDirection.x += 0.1f;
            }
            // Ensure camera stays orthogonal to the trajectory
            camera.lookAt(golfBall.getInstance().transform.getTranslation(new Vector3()).add(kickDirection));
            camera.update();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                kickingMode = false;
                trajectory.toggleKickingMode();
                resetCamera();
                System.out.println("dawdwadawdawda");
                // Kick the ball using getTrajectoryArray
                Ball ball = new Ball(terrain);
                double[][] trajectory1 = ball.getTrajectoryArray(0.1, golfBall.getPosition().x, golfBall.getPosition().z, trajectory.getDirection().x*kickPower, trajectory.getDirection().z*kickPower, 30);// Example power and angle
                System.out.println(trajectory.getDirection().x + " " + trajectory.getDirection().z);
                Debugger.printMatrix(trajectory1);
                //Debugger.printArray(trajectoryVec3);
                golfBall.setTrajectoryVec3(trajectory1);
                golfBall.kickingTurn();
                kickPower = 1f;
                trajectory.setKickingPower(kickPower);
                bottomLeftLabel.setText("Power: " + kickPower);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            debugger.toggleDebugger();
        }
        debugger.handleInput(camera, heightMap);
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
        modelBatch.dispose();
        for (ModelInstance instance : terrainInstances) {
            instance.model.dispose();
        }
        for (Map.Entry<String, Texture> texture : textures.entrySet()) {
            texture.getValue().dispose();
        }
        skySphere.dispose();
        for (Tree tree : trees) {
            tree.dispose();
        }
        house.dispose();
        debugger.dispose();
        bottomLeftLabel.dispose();
        winLabel.dispose();
    }
}
