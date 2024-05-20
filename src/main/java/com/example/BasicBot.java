package com.example;

public class BasicBot {
    private Ball ball;
    private double timeStep;
    private double maxVelocity;
    private GameLogics gameLogics;
    private Terrain terrain;

    public BasicBot(Ball ball, double timeStep, double maxVelocity, GameLogics gameLogics, Terrain terrain) {
        this.ball = ball;
        this.timeStep = timeStep;
        this.maxVelocity = maxVelocity;
        this.gameLogics = gameLogics;
        this.terrain = terrain;
    }

    // Calculate the next move for the bot
    public void calculateNextMove() {
        // Get the current state of the ball (position and velocity)
        double[] currentState = ball.getCurrentState();
        double currentX = currentState[0];
        double currentY = currentState[1];
        double currentVx = currentState[2];
        double currentVy = currentState[3];

        // Determine the target position (goal)
        double targetX = gameLogics.getGoalPositionX();
        double targetY = gameLogics.getGoalPositionY();

        // Calculate the distance and direction to the target
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double requiredVx = (deltaX / distance) * maxVelocity;
        double requiredVy = (deltaY / distance) * maxVelocity;

        // Adjust velocity based on terrain slope and friction
        double[] slope = terrain.getSlope(currentX, currentY);
        double friction = terrain.getKineticFriction(currentX, currentY);
        requiredVx -= slope[0] * friction * timeStep;
        requiredVy -= slope[1] * friction * timeStep;

        // Ensure velocity does not exceed maximum allowed velocity
        double speed = Math.sqrt(requiredVx * requiredVx + requiredVy * requiredVy);
        if (speed > maxVelocity) {
            double scalingFactor = maxVelocity / speed;
            requiredVx *= scalingFactor;
            requiredVy *= scalingFactor;
        }

        // Set the ball's velocity
        ball.setVelocity(requiredVx, requiredVy);
    }

    // Check the current status of the bot
    public String checkStatus() {
        // Get the current state of the ball
        double[] currentState = ball.getCurrentState();
        double currentX = currentState[0];
        double currentY = currentState[1];

        double targetX = gameLogics.getGoalPositionX();
        double targetY = gameLogics.getGoalPositionY();

        // Check if the ball has reached the target
        if (Math.abs(currentX - targetX) < 0.1 && Math.abs(currentY - targetY) < 0.1) {
            return "Reached Target";
        }

        // Check if the ball is stuck
        double currentVx = currentState[2];
        double currentVy = currentState[3];
        if (Math.abs(currentVx) < 0.01 && Math.abs(currentVy) < 0.01) {
            return "Stuck";
        }

        return "Moving";
    }

    // Simulate the bot's movement until it reaches the target
    public void playUntilTarget() {
        while (true) {
            calculateNextMove();  // Calculate the next move
            ball.updateBallStateRungeKutta(timeStep);  // Update the ball's state
            String status = checkStatus();  // Check the status of the bot
            if ("Reached Target".equals(status)) {
                System.out.println("Bot has reached the target.");
                break;
            } else if ("Stuck".equals(status)) {
                System.out.println("Bot is stuck.");
                break;
            }
        }
    }
}
