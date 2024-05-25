package com.example;

import com.gui.GolfBall;
import com.gui.Terrain;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a basic bot that controls a ball's movement on a terrain,
 * navigating towards a target while avoiding obstacles.
 */
public class BasicBot {
    private final Ball ball;
    private final GolfBall golfBall;
    private final double timeStep;
    private final double maxVelocity;
    private final Terrain terrain;

    /**
     * Constructs a BasicBot instance.
     *
     * @param ball        The ball to be controlled by the bot.
     * @param golfBall    The golf ball which provides target position information.
     * @param timeStep    The time step for each simulation update.
     * @param maxVelocity The maximum velocity the bot can set for the ball.
     * @param terrain     The terrain on which the ball moves.
     */
    public BasicBot(Ball ball, GolfBall golfBall, double timeStep, double maxVelocity, Terrain terrain) {
        this.ball = ball;
        this.golfBall = golfBall;
        this.timeStep = timeStep;
        this.maxVelocity = maxVelocity;
        this.terrain = terrain;
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The distance between the two points.
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Calculates the average slope in the x direction between two points.
     *
     * @param x1 The starting x-coordinate.
     * @param y1 The starting y-coordinate.
     * @param x2 The ending x-coordinate.
     * @param y2 The ending y-coordinate.
     * @return The average x slope.
     */
    private double calculateXSlope(double x1, double y1, double x2, double y2) {
        double epsilon = 0.5;
        double x = x1;
        double y = y1;
        double xSlope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / epsilon;

        for (double i = 0; i < dist; i++) {
            xSlope += terrain.getSlope(x, y)[0];
            x = x1 + (x2 - x1) * (i / dist);
            y = y1 + (y2 - y1) * (i / dist);
        }

        return xSlope *dist;
    }

    /**
     * Calculates the average slope in the y direction between two points.
     *
     * @param x1 The starting x-coordinate.
     * @param y1 The starting y-coordinate.
     * @param x2 The ending x-coordinate.
     * @param y2 The ending y-coordinate.
     * @return The average y slope.
     */
    private double calculateYSlope(double x1, double y1, double x2, double y2) {
        double epsilon = 0.5;
        double x = x1;
        double y = y1;
        double ySlope = 0;
        double dist = calculateDistance(x1, y1, x2, y2) / epsilon;

        for (double i = 0; i < dist; i++) {
            ySlope += terrain.getSlope(x, y)[1];
            x = x1 + (x2 - x1) * (i / dist);
            y = y1 + (y2 - y1) * (i / dist);
        }

        return ySlope*dist;
    }

    /**
     * Checks if the path between two points is clear of obstacles.
     *
     * @param startX The starting x-coordinate.
     * @param startY The starting y-coordinate.
     * @param endX   The ending x-coordinate.
     * @param endY   The ending y-coordinate.
     * @return true if the path is clear, false otherwise.
     */
    private boolean isPathClear(double startX, double startY, double endX, double endY) {
        double stepSize = 0.1;
        double distance = calculateDistance(startX, startY, endX, endY);
        double steps = distance / stepSize;
        double x = startX;
        double y = startY;

        for (int i = 0; i < steps; i++) {
            x = (endX - startX) / steps;
            y = (endY - startY) / steps;
            if (golfBall.checkNearestObstacles(x, y) < 1f) {
                return false; // Return false if an obstacle is found
            }
        }

        return true; // Return true if the path is clear
    }

    /**
     * Finds a position near an obstacle to navigate around it.
     *
     * @param startX     The starting x-coordinate.
     * @param startY     The starting y-coordinate.
     * @param obstacleX  The x-coordinate of the obstacle.
     * @param obstacleY  The y-coordinate of the obstacle.
     * @return An array containing the x and y coordinates of the new position.
     */
    private double[] findNearbyPosition(double startX, double startY, double obstacleX, double obstacleY) {
        double distanceToObstacle = calculateDistance(startX, startY, obstacleX, obstacleY);
        double stepDistance = Math.min(1.0, distanceToObstacle / 2); // Move half the distance or 1 unit, whichever is smaller

        double directionX = (obstacleX - startX) / distanceToObstacle;
        double directionY = (obstacleY - startY) / distanceToObstacle;

        double nextX = startX + directionX * stepDistance;
        double nextY = startY + directionY * stepDistance;

        return new double[]{nextX, nextY};
    }

    /**
     * Finds the next position towards the target, adjusting for obstacles.
     *
     * @param startX  The starting x-coordinate.
     * @param startY  The starting y-coordinate.
     * @param targetX The target x-coordinate.
     * @param targetY The target y-coordinate.
     * @return An array containing the x and y coordinates of the next position.
     */
    private double[] findNextPosition(double startX, double startY, double targetX, double targetY) {
        double distanceToTarget = calculateDistance(startX, startY, targetX, targetY);
        double stepDistance = Math.min(1.0, distanceToTarget / 2); // Move half the distance or 1 unit, whichever is smaller

        double directionX = (targetX - startX) / distanceToTarget;
        double directionY = (targetY - startY) / distanceToTarget;

        double nextX = startX + directionX * stepDistance;
        double nextY = startY + directionY * stepDistance;

        // Check for obstacles along the new path
        if (!isPathClear(startX, startY, nextX, nextY)) {
            // If a path is not clear, find the nearest obstacle and aim near it
            return findNearbyPosition(startX, startY, nextX, nextY);
        }

        return new double[]{nextX, nextY};
    }

    /**
     * Calculates the next move for the bot.
     */
    public double[] calculateNextMove(double currentX,double currentY, double targetX,double targetY) {


        while (!isPathClear(currentX, currentY, targetX, targetY)) {
            double[] nextPosition = findNextPosition(currentX, currentY, targetX, targetY);
            targetX = nextPosition[0];
            targetY = nextPosition[1];
        }

        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        double distance = calculateDistance(currentX, currentY, targetX, targetY);
        double requiredVx = (deltaX / distance) * maxVelocity/2;
        double requiredVy = (deltaY / distance) * maxVelocity/2;

        double xSlope = calculateXSlope(currentX, currentY, targetX, targetY);
        double ySlope = calculateYSlope(currentX, currentY, targetX, targetY);
        double friction = terrain.getKineticFriction(currentX, currentY);
        requiredVx += (distance*distance)*0.001*(xSlope *  friction*timeStep);
        requiredVy += (distance*distance)*0.001*(ySlope * friction*timeStep);

        double speed = Math.sqrt(requiredVx * requiredVx + requiredVy * requiredVy);
        if (speed > maxVelocity) {
            double scalingFactor = maxVelocity / speed;
            requiredVx *= scalingFactor;
            requiredVy *= scalingFactor;
        }

    return  new double []{requiredVx, requiredVy};
    }

    /**
     * Checks the current status of the bot.
     *
     * @return "Reached Target" if the bot has reached the target, "Stuck"
     * if the bot is stuck, or "Moving" if the bot is still moving.
     */
    public String checkStatus() {
        double[] currentState = ball.getCurrentState();
        double currentX = currentState[0];
        double currentY = currentState[1];

        Vector3 targetPosition = golfBall.getPosition();
        double targetX = targetPosition.x;
        double targetY = targetPosition.z;

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

    /**
     * Makes the bot play until it reaches the target or gets stuck.
     */
    public void playUntilTarget() {
        while (true) {
            //calculateNextMove();
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
