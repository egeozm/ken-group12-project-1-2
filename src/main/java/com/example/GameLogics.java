package com.example;

import java.util.Arrays;

public class GameLogics {
    private Ball ball;
    private Terrain terrain;
    private PhysicsCoefficients coefficients;
    private double goalPositionX;
    private double goalPositionY;

    public GameLogics(Ball ball, Terrain terrain, PhysicsCoefficients coefficients, double goalPositionX, double goalPositionY) {
        this.ball = ball;
        this.terrain = terrain;
        this.coefficients = coefficients;
        this.goalPositionX = goalPositionX;
        this.goalPositionY = goalPositionY;
    }

    // Get the X position of the goal
    public double getGoalPositionX() {
        return goalPositionX;
    }

    // Get the Y position of the goal
    public double getGoalPositionY() {
        return goalPositionY;
    }

    public void update(double deltaTime) {
        if (!handleCollisions()) {
            ball.updatePosition(deltaTime);
        }
    }

    private boolean handleCollisions() {
        double[] position = ball.getPosition();
        String currentTerrain = terrain.getTerrainType((int) position[0], (int) position[2]);
        ball.setCurrentTerrain(currentTerrain);

        // Check if the ball is in water or out of bounds
        if (currentTerrain.equals("water") || currentTerrain.equals("out of bounds")) {
            System.out.println("src.main.Ball in " + currentTerrain + ", applying penalty and resetting to previous safe position!");
            resetBallToPreviousPosition();
            return true;
        }

        return false;
    }

    private void resetBallToPreviousPosition() {
        double previousX = ball.getPreviousX();
        double previousY = ball.getPreviousY();
        double previousZ = ball.getPreviousZ();

        ball.setPosition(previousX, previousY, previousZ);
        ball.setVelocity(0, 0); // Reset the velocity to zero
    }

    public void updateGameState() {
        Terrain terrainn = new Terrain(10, 10, 10);
        terrainn.grassLand();

        int botX = 5; // Starting position for the bot
        int botY = 5; // Starting position
        BasicBot bot = new BasicBot(terrainn, botX, botY);
        int[] botDecision = bot.decideNextMove();
        System.out.println("Bot decision: " + Arrays.toString(botDecision));
    }


}
