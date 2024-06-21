package com.gui.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gui.labels.WinLabel;
import com.gui.terrain.Terrain;

import java.util.HashMap;
import java.util.Random;

/**
 * Represents a golf ball in the 3D golf game.
 */
public class GolfBall {
    private final ModelInstance ballInstance;
    private Vector3 startPosition;
    private Vector3 initialPosition;
    private Vector3 targetPosition;
    private Vector3 currentPosition;

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    private Vector3 velocity;

    public boolean isMoving() {
        return isMoving;
    }

    public boolean isMoving;
    private int startIdx = -1;
    private final Terrain terrain;
    private final WinLabel winLabel;

    private double[][] trajectoryVec;
    private double physicsStep = 1f;
    private final double stepBias;

    /**
     * Constructs a GolfBall instance.
     *
     * @param texturePath The path to the texture for the golf ball.
     * @param terrain     The terrain on which the golf ball is placed.
     * @param winLabel    The label to display when the ball reaches the hole.
     */
    public GolfBall(String texturePath, Terrain terrain, WinLabel winLabel, double stepBias) {
        this.winLabel = winLabel;
        this.terrain = terrain;
        Texture ballTexture = new Texture(Gdx.files.internal(texturePath));
        Material ballMaterial = new Material(TextureAttribute.createDiffuse(ballTexture));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, ballMaterial);
        builder.sphere(1f, 1f, 1f, 32, 32);
        ballInstance = new ModelInstance(modelBuilder.end());

        startPosition = new Vector3();
        targetPosition = new Vector3();
        currentPosition = new Vector3();
        isMoving = false;
        velocity = new Vector3();
        double confPhysicsStep = 1;
        if(terrain.getHeight()*terrain.getWidth() > 4000)
            physicsStep = (double) (terrain.getHeight() * terrain.getWidth()) /2000* confPhysicsStep;
        this.stepBias = stepBias;
    }

    /**
     * Gets the model instance of the golf ball.
     *
     * @return The model instance of the golf ball.
     */
    public ModelInstance getInstance() {
        return ballInstance;
    }

    /**
     * Sets the position of the golf ball.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    public void setPosition(float x, float y, float z) {
        startPosition.set(x, y, z);
        currentPosition.set(x, y, z);
        ballInstance.transform.setToTranslation(currentPosition);
    }

    /**
     * Resets the golf ball to the start position.
     */
    public void toTheStartPosition() {
        isMoving = false;
        ballInstance.transform.setToTranslation(new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z));
        currentPosition = new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z);
        startPosition = new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z);
    }

    /**
     * Gets the current position of the golf ball.
     *
     * @return The current position of the golf ball.
     */
    public Vector3 getPosition() {
        return currentPosition;
    }

    /**
     * Initiates the kicking turn for the golf ball.
     */
    public void kickingTurn() {
        isMoving = true;
        startIdx = -1;
        initialPosition = startPosition;
        kickBall();
    }

    /**
     * Kicks the ball along the trajectory.
     */
    public void kickBall() {
        System.out.println(startIdx);
        if (startIdx >= trajectoryVec.length - 1) {
            isMoving = false;
            initialPosition = startPosition;
            return;
        }
        startIdx++;
        velocity = new Vector3((float) trajectoryVec[startIdx][2], 0, (float) trajectoryVec[startIdx][3]);
        targetPosition = new Vector3((float) trajectoryVec[startIdx][0], 0, (float) trajectoryVec[startIdx][1]);
        //System.out.println(startPosition.x + " " + startPosition.z + " " + startPosition.x + " " + targetPosition.x + " " + targetPosition.y + " " + targetPosition.z);
        isMoving = true;
    }

    /**
     * Updates the position and state of the golf ball.
     *
     * @param delta The time elapsed since the last update.
     */
    public void update(float delta) {
        if(stepBias > 5) {
            goToTheTarget(delta);
            return;
        }
        if (isMoving) {
            // Update the current position based on the velocity and delta time
            Vector3 displacement = new Vector3(velocity).scl(delta);
            currentPosition.add(displacement);
            startPosition = currentPosition;
            // Update the ball's transform to the new position
            ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));
            // Check for obstacles and reset position if necessary
            if (currentPosition.x < -terrain.getWidth() || currentPosition.x > terrain.getWidth() || currentPosition.z < -terrain.getHeight() ||
                    currentPosition.z > terrain.getHeight() || checkNearestObstacles(currentPosition.x, currentPosition.z) < 1f) {
                toTheStartPosition();
            }
            if (checkNearestHole(currentPosition.x, currentPosition.z) < 1f) {
                winLabel.show();
                hideBall();
            }
        }
        // Check if the ball was kicked and has reached the target position
        if (new Vector3(currentPosition.x, 0, currentPosition.z).dst(new Vector3(targetPosition.x, 0, targetPosition.z)) < 0.1f* physicsStep *velocity.len()*stepBias) {
            currentPosition = targetPosition;
            ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));
            kickBall();
        }
    }
    private void goToTheTarget(float delta){
        if (isMoving) {
            // Calculate the distance to the target position
            Vector3 toTarget = new Vector3(targetPosition).sub(currentPosition);
            float distanceToTarget = toTarget.len();

            // Normalize the direction to the target and scale by the speed (velocity magnitude)
            Vector3 direction = new Vector3(toTarget).nor();
            double speed = 3f;
            Vector3 velocity = new Vector3(direction).scl((float) speed);

            // Calculate the displacement based on the velocity and delta time
            Vector3 displacement = new Vector3(velocity).scl(delta);

            // If the displacement is larger than the distance to the target, adjust to stop at the target
            //if (displacement.len() > distanceToTarget) {
            //    displacement.setLength(distanceToTarget);
            //}

            // Update the current position based on the displacement
            currentPosition.add(displacement);
            startPosition = currentPosition;
            // Update the ball's transform to the new position
            ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));

            // Check for obstacles and reset position if necessary
            if (currentPosition.x < -terrain.getWidth() || currentPosition.x > terrain.getWidth() || currentPosition.z < -terrain.getHeight() ||
                    currentPosition.z > terrain.getHeight() || checkNearestObstacles(currentPosition.x, currentPosition.z) < 1f) {
                toTheStartPosition();
            }
            if (checkNearestHole(currentPosition.x, currentPosition.z) < 1f) {
                winLabel.show();
                hideBall();
            }
            // Check if the ball has reached the target position
            if (distanceToTarget < 0.1f * physicsStep * velocity.len()) {
                currentPosition.set(targetPosition);
                ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));
                kickBall();
            }
        }
    }

    /**
     * Hides the golf ball by moving it out of view.
     */
    private void hideBall() {
        currentPosition = new Vector3(0, -100, 0);
        startPosition = new Vector3(0, -100, 0);
        initialPosition = new Vector3(0, -100, 0);
        isMoving = false;
        ballInstance.transform.setToTranslation(currentPosition);
    }

    /**
     * Checks for the nearest obstacles to the given coordinates.
     *
     * @param x The x-coordinate.
     * @param z The z-coordinate.
     * @return The distance to the nearest obstacle.
     */
    public double checkNearestObstacles(double x, double z) {
        double smallestDistance = 10000.0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((int) (x) + terrain.getWidth() + i > 0 && (int) (x) + terrain.getWidth() + i < terrain.getWidth() * 2 && (int) z + terrain.getHeight() + j > 0 && (int) z + terrain.getHeight() + j < terrain.getHeight() * 2) {
                    if (!terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("0"))
                        if (!terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("hole"))
                            smallestDistance = Math.min(smallestDistance, new Vector3((float) x + terrain.getWidth(), 0, (float) z + terrain.getHeight()).dst(new Vector3((int) (x) + terrain.getWidth() + i, 0, (int) z + terrain.getHeight() + j)));
                }
            }
        }
        //System.out.println(smallestDistance);
        return smallestDistance;
    }
    public Terrain getTerrain(){
        return terrain;
    }

    /**
     * Checks for the nearest hole to the given coordinates.
     *
     * @param x The x-coordinate.
     * @param z The z-coordinate.
     * @return The distance to the nearest hole.
     */
    private double checkNearestHole(double x, double z) {
        double smallestDistance = 10000.0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((int) (x) + terrain.getWidth() + i > 0 && (int) (x) + terrain.getWidth() + i < terrain.getWidth() * 2 && (int) z + terrain.getHeight() + j > 0 && (int) z + terrain.getHeight() + j < terrain.getHeight() * 2) {
                    if (terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("hole"))
                        smallestDistance = Math.min(smallestDistance, new Vector3((float) x + terrain.getWidth(), 0, (float) z + terrain.getHeight()).dst(new Vector3((int) (x) + terrain.getWidth() + i, 0, (int) z + terrain.getHeight() + j)));
                }
            }
        }
        //System.out.println(smallestDistance);
        return smallestDistance;
    }

    /**
     * Sets the trajectory vector for the golf ball.
     *
     * @param trajectoryVec The trajectory vector.
     */
    public void setTrajectoryVec3(double[][] trajectoryVec) {
        this.trajectoryVec = trajectoryVec;
    }

    public static void setRandomBallPosition(GolfBall x, HashMap<String, Double> parameters) {
        Random random = new Random();
        double ballX;
        double ballZ = 0;
        int width = Terrain.getInstance().getWidth();
        int height = Terrain.getInstance().getHeight();
        if (parameters.get("xBall").equals(Double.NaN)) {
            do {
                ballX = random.nextInt(width * 2 - 1);
            } while (!Terrain.getInstance().getObstaclesCoordinates()[(int) ballX][(int) ballZ].equals("0"));
        } else {
            ballX = parameters.get("xBall") + width;
        }
        if (parameters.get("zBall").equals(Double.NaN)) {
            do {
                ballZ = random.nextInt(height * 2 - 1);
            } while (!Terrain.getInstance().getObstaclesCoordinates()[(int) ballX][(int) ballZ].equals("0"));
        } else {
            ballZ = (int) (double) parameters.get("zBall") + height;
        }
        float ballY = (float) Terrain.getInstance().getHeightCoordinates()[(int) ballX][(int) ballZ];
        x.setPosition((float) (ballX - width), ballY + 0.5f, (float) (ballZ - height));
    }
}
