package com.gui;

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
import com.badlogic.gdx.math.Vector3;

import java.security.Key;

public class GolfBall {
    private ModelInstance ballInstance;
    private Vector3 startPosition;
    private Vector3 initialPosition;
    private Vector3 targetPosition;
    private Vector3 currentPosition;
    private Vector3 velocity;
    private boolean isMoving;
    private float speed = 1f;
    private float phisicsStep = 1f;
    private float interpolationAlpha;
    private int startIdx = -1;
    private boolean wasKicked = false;
    private Terrain terrain;
    private WinLabel winLabel;

    public double[][] getTrajectoryVec3() {
        return trajectoryVec;
    }

    public void setTrajectoryVec3(double[][] trajectoryVec) {
        this.trajectoryVec = trajectoryVec;
    }

    private double[][] trajectoryVec;

    public GolfBall(String texturePath, Terrain terrain, WinLabel winLabel) {
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
        interpolationAlpha = 0.0f;
        velocity = new Vector3();
        if(terrain.getWidth()*terrain.getHeight() > 4000)
            phisicsStep = terrain.getWidth()*terrain.getHeight()/2000;
    }

    public ModelInstance getInstance() {
        return ballInstance;
    }

    public void setPosition(float x, float y, float z) {
        startPosition.set(x, y, z);
        currentPosition.set(x, y, z);
        ballInstance.transform.setToTranslation(currentPosition);
    }
    public void toTheStartPosition() {
        isMoving = false;
        wasKicked = false;
        ballInstance.transform.setToTranslation(new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z));
        currentPosition = new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z);
        startPosition = new Vector3(initialPosition.x, (float) terrain.getHeight(initialPosition.x, initialPosition.z) + 0.5f, initialPosition.z);
    }

    public Vector3 getPosition() {
        return currentPosition;
    }
    public void kickingTurn(){
        wasKicked = true;
        isMoving = true;
        startIdx = -1;
        initialPosition = startPosition;
        kickBall();
    }
    public void kickBall() {
        System.out.println(startIdx);
        if(startIdx >= trajectoryVec.length-1) {
            isMoving = false;
            initialPosition = startPosition;
            return;
        }
        startIdx++;
        interpolationAlpha = 0.0f;
        velocity = new Vector3((float) trajectoryVec[startIdx][2], 0, (float) trajectoryVec[startIdx][3]);
        targetPosition = new Vector3((float) trajectoryVec[startIdx][0], 0, (float) trajectoryVec[startIdx][1]);
        isMoving = true;
    }

    public void update(float delta) {
        if (isMoving) {
            // Update the current position based on the velocity and delta time
            Vector3 displacement = new Vector3(velocity).scl(delta);
            currentPosition.add(displacement);
            startPosition = currentPosition;
            // Update the ball's transform to the new position
            ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));
            // Check for obstacles and reset position if necessary
            if (currentPosition.x < -terrain.getWidth() || currentPosition.x > terrain.getWidth() || currentPosition.z < -terrain.getHeight() ||
                    currentPosition.z > terrain.getHeight() ||  checkNearestObstacles(currentPosition.x, currentPosition.z) < 1f) {
                toTheStartPosition();
            }
            if (checkNearestHole(currentPosition.x, currentPosition.z) < 1f) {
                winLabel.show();
                hideBall();
            }

            // Stop the ball if its velocity is very low (to prevent it from moving indefinitely)
            //if (velocity.len() < 0.06f) {
            //    isMoving = false;
            //    velocity.set(0, 0, 0);
            //}
        }
        // Check if the ball was kicked and has reached the target position
        if (new Vector3(currentPosition.x, 0, currentPosition.z).dst(new Vector3(targetPosition.x, 0, targetPosition.z)) < 0.1f*phisicsStep*velocity.len()) {
            currentPosition = targetPosition;
            ballInstance.transform.setToTranslation(new Vector3(currentPosition.x, (float) terrain.getHeight(currentPosition.x, currentPosition.z) + 0.5f, currentPosition.z));
            //velocity.set(0, 0, 0);
            kickBall();
        }
    }
    private void hideBall(){
        currentPosition = new Vector3(0, -100, 0);
        startPosition = new Vector3(0, -100, 0);
        initialPosition = new Vector3(0, -100, 0);
        isMoving = false;
        ballInstance.transform.setToTranslation(currentPosition);
    }
    public double checkNearestObstacles(double x, double z){
        double smallestDistance = 10000.0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if((int) (x) + terrain.getWidth()+i > 0 && (int) (x) + terrain.getWidth()+i < terrain.getWidth()*2 && (int) z + terrain.getHeight()+j > 0 && (int) z + terrain.getHeight()+j < terrain.getHeight()*2) {
                    if (!terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("0"))
                        if (!terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("hole"))
                            smallestDistance = Math.min(smallestDistance, new Vector3((float) x+terrain.getWidth(), 0, (float) z+terrain.getHeight()).dst(new Vector3((int) (x) + terrain.getWidth() + i, 0, (int) z + terrain.getHeight() + j)));
                }
            }
        }
        //System.out.println(smallestDistance);
        return smallestDistance;
    }
    private double checkNearestHole(double x, double z){
        double smallestDistance = 10000.0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if((int) (x) + terrain.getWidth()+i > 0 && (int) (x) + terrain.getWidth()+i < terrain.getWidth()*2 && (int) z + terrain.getHeight()+j > 0 && (int) z + terrain.getHeight()+j < terrain.getHeight()*2) {
                    if (terrain.getObstaclesCoordinates()[(int) (x) + terrain.getWidth() + i][(int) z + terrain.getHeight() + j].equals("hole"))
                        smallestDistance = Math.min(smallestDistance, new Vector3((float) x+terrain.getWidth(), 0, (float) z+terrain.getHeight()).dst(new Vector3((int) (x) + terrain.getWidth() + i, 0, (int) z + terrain.getHeight() + j)));
                }
            }
        }
        //System.out.println(smallestDistance);
        return smallestDistance;
    }
    private static double calculateDifference(Vector3 a, Vector3 b){
        return a.x + a.z - b.x - b.z;
    }
    //awdawdaw
}
