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

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private double calculateXSlope(double x1, double y1, double x2, double y2) {
        double stepsize = 0.5;
        double x = x1;
        double y = y1;
        int count = 0;
        double xslope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / stepsize;
        for (double i = 0; i < dist; i++) {
            xslope = +terrain.getSlope(x, y)[0];
            x = dist * (x2 - x1);
            y = dist * (y2 - y1);
            count++;
        }

        return xslope / count;
    }

    private double calculateYSlope(double x1, double y1, double x2, double y2) {
        double stepsize = 0.5;
        double x = x1;
        double y = y1;
        int count = 0;
        double yslope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / stepsize;
        for (double i = 0; i < dist; i++) {
            yslope = +terrain.getSlope(x, y)[0];
            x = dist * (x2 - x1);
            y = dist * (y2 - y1);
            count++;
        }

        return yslope / count;
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
        double deltaX = Math.abs(targetX - currentX);
        double deltaY = Math.abs(targetY - currentY);

        double distance = calculateDistance(currentX, currentY, targetX, targetY);
        double requiredVx = (deltaX / distance) * maxVelocity;
        double requiredVy = (deltaY / distance) * maxVelocity;

        // Adjust velocity based on terrain slope and friction
        double Xslope = calculateXSlope(currentX, currentY, targetX, targetY);
        double Yslope = calculateYSlope(currentX, currentY, targetX, targetY);
        double friction = terrain.getKineticFriction(currentX, currentY);
        requiredVx -= Xslope * friction * timeStep;
        requiredVy -= Yslope * friction * timeStep;

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