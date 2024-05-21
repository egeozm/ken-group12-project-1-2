package com.example;

import com.gui.GolfBall;
import com.gui.Terrain;

public class BasicBot {
    private Ball ball;
    private GolfBall golfBall;
    private double timeStep;
    private double maxVelocity;
    private GameLogics gameLogics;
    private Terrain terrain;

    public BasicBot(Ball ball, GolfBall golfBall, double timeStep, double maxVelocity, GameLogics gameLogics, Terrain terrain) {
        this.ball = ball;
        this.golfBall = golfBall;
        this.timeStep = timeStep;
        this.maxVelocity = maxVelocity;
        this.gameLogics = gameLogics;
        this.terrain = terrain;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private double calculateXSlope(double x1, double y1, double x2, double y2) {
        double epsilon = 0.5;
        double x = x1;
        double y = y1;
        double xslope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / epsilon;

        for (double i = 0; i < dist; i++) {
            xslope += terrain.getSlope(x, y)[0];
            x = x1 + (x2 - x1) * (i / dist);
            y = y1 + (y2 - y1) * (i / dist);
        }

        return xslope / dist;
    }

    private double calculateYSlope(double x1, double y1, double x2, double y2) {
        double epsilon = 0.5;
        double x = x1;
        double y = y1;
        double yslope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / epsilon;

        for (double i = 0; i < dist; i++) {
            yslope += terrain.getSlope(x, y)[1];
            x = x1 + (x2 - x1) * (i / dist);
            y = y1 + (y2 - y1) * (i / dist);
        }

        return yslope / dist;
    }

    private boolean isPathClear(double startX, double startY, double endX, double endY) {
        double stepsize = 0.1;
        double distance = calculateDistance(startX, startY, endX, endY);
        double steps = distance / stepsize;
        double x = startX;
        double y = startY;

        for (int i = 0; i < steps; i++) {
            x += (endX - startX) / steps;
            y += (endY - startY) / steps;
            if (golfBall.checkNearestObstacles(x, y) < 1f) {
                return false; // Return false if an obstacle is found
            }
        }

        return true; // Return true if the path is clear
    }

    private double[] findNearbyPosition(double startX, double startY, double obstacleX, double obstacleY) {
        double distanceToObstacle = calculateDistance(startX, startY, obstacleX, obstacleY);
        double stepDistance = Math.min(1.0, distanceToObstacle / 2); // Move half the distance or 1 unit, whichever is smaller

        double directionX = (obstacleX - startX) / distanceToObstacle;
        double directionY = (obstacleY - startY) / distanceToObstacle;

        double nextX = startX + directionX * stepDistance;
        double nextY = startY + directionY * stepDistance;

        return new double[]{nextX, nextY};
    }

    private double[] findNextPosition(double startX, double startY, double targetX, double targetY) {
        double distanceToTarget = calculateDistance(startX, startY, targetX, targetY);
        double stepDistance = Math.min(1.0, distanceToTarget / 2); // Move half the distance or 1 unit, whichever is smaller

        double directionX = (targetX - startX) / distanceToTarget;
        double directionY = (targetY - startY) / distanceToTarget;

        double nextX = startX + directionX * stepDistance;
        double nextY = startY + directionY * stepDistance;

        // Check for obstacles along the new path
        if (!isPathClear(startX, startY, nextX, nextY)) {
            // If path is not clear, find the nearest obstacle and aim near it
            return findNearbyPosition(startX, startY, nextX, nextY);
        }

        return new double[]{nextX, nextY};
    }

    // Calculate the next move for the bot
    public void calculateNextMove() {
        double[] currentState = ball.getCurrentState();
        double currentX = currentState[0];
        double currentY = currentState[1];

        double targetX = gameLogics.getGoalPositionX();
        double targetY = gameLogics.getGoalPositionY();

        while (!isPathClear(currentX, currentY, targetX, targetY)) {
            double[] nextPosition = findNextPosition(currentX, currentY, targetX, targetY);
            targetX = nextPosition[0];
            targetY = nextPosition[1];
        }

        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        double distance = calculateDistance(currentX, currentY, targetX, targetY);
        double requiredVx = (deltaX / distance) * maxVelocity;
        double requiredVy = (deltaY / distance) * maxVelocity;

        double Xslope = calculateXSlope(currentX, currentY, targetX, targetY);
        double Yslope = calculateYSlope(currentX, currentY, targetX, targetY);
        double friction = terrain.getKineticFriction(currentX, currentY);
        requiredVx -= Xslope * friction * timeStep;
        requiredVy -= Yslope * friction * timeStep;

        double speed = Math.sqrt(requiredVx * requiredVx + requiredVy * requiredVy);
        if (speed > maxVelocity) {
            double scalingFactor = maxVelocity / speed;
            requiredVx *= scalingFactor;
            requiredVy *= scalingFactor;
        }

        ball.setVelocity(requiredVx, requiredVy);
    }

    public String checkStatus() {
        double[] currentState = ball.getCurrentState();
        double currentX = currentState[0];
        double currentY = currentState[1];

        double targetX = gameLogics.getGoalPositionX();
        double targetY = gameLogics.getGoalPositionY();

        if (Math.abs(currentX - targetX) < 0.1 && Math.abs(currentY - targetY) < 0.1) {
            return "Reached Target";
        }

        double currentVx = currentState[2];
        double currentVy = currentState[3];
        if (Math.abs(currentVx) < 0.01 && Math.abs(currentVy) < 0.01) {
            return "Stuck";
        }

        return "Moving";
    }

    public void playUntilTarget() {
        while (true) {
            calculateNextMove();
            ball.updateBallStateRungeKutta(timeStep);
            String status = checkStatus();
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
