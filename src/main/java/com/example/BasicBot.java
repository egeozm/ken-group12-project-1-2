package com.example;

import com.gui.Terrain;

/**
 * Represents a basic bot that controls a ball's movement on a terrain,
 * navigating towards a target while avoiding obstacles.
 */
public class BasicBot {
    private final double timeStep;
    private final double maxVelocity;
    private final Terrain terrain;

    /**
     * Constructs a BasicBot instance.
     *
     * @param timeStep    The time step for each simulation update.
     * @param maxVelocity The maximum velocity the bot can set for the ball.
     * @param terrain     The terrain on which the ball moves.
     */
    public BasicBot(double timeStep, double maxVelocity, Terrain terrain) {
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

        return xSlope * dist;
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

        return ySlope * dist;
    }

    /**
     * Calculates the next move for the bot.
     */
    public double[] calculateNextMove(double currentX, double currentY, double targetX, double targetY) {
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;
        double distance = calculateDistance(currentX, currentY, targetX, targetY);

        // If the distance is very small, set a minimum velocity
        if (distance < 0.1) {
            double minVelocity = 0.01; // Minimum velocity threshold to overcome static friction
            return new double[]{minVelocity * Math.signum(deltaX), minVelocity * Math.signum(deltaY)};
        }

        double requiredVx = (deltaX / distance) * maxVelocity / 2;
        double requiredVy = (deltaY / distance) * maxVelocity / 2;

        double xSlope = calculateXSlope(currentX, currentY, targetX, targetY);
        double ySlope = calculateYSlope(currentX, currentY, targetX, targetY);
        double friction = terrain.getKineticFriction(currentX, currentY);

        requiredVx += (distance * distance) * 0.001 * (xSlope * friction * timeStep);
        requiredVy += (distance * distance) * 0.001 * (ySlope * friction * timeStep);

        double speed = Math.sqrt(requiredVx * requiredVx + requiredVy * requiredVy);
        if (speed > maxVelocity) {
            double scalingFactor = maxVelocity / speed;
            requiredVx *= scalingFactor;
            requiredVy *= scalingFactor;
        }

        return new double[]{requiredVx, requiredVy};
    }


}
