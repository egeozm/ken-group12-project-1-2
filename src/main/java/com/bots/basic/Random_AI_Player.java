package com.bots.basic;

import com.ode.Ball;
import com.ode.PhysicsCoefficients;
import com.gui.objects.GolfBall;
import com.gui.terrain.Terrain;

import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// Import the Random_Generator class


/**
 * AI_Player class represents an artificial intelligence player for golf ball simulation.
 * It uses Particle Swarm Optimization (PSO) to find the best initial velocities for a golf ball
 * to reach a target position.
 */
public class Random_AI_Player {

    private static final double MAX_SPEED = 5.0;
    private static final int SWARM_SIZE = 10; // Reduced swarm size
    private static final int MAX_ITERATIONS = 100; // Reduced iterations
    private static final double W = 0.5;  // Inertia weight
    private static final double C1 = 1.0; // Cognitive coefficient
    private static final double C2 = 1.5; // Social coefficient

    private GolfBall golfBall;
    private Ball ball;
    private final double targetX;
    private final double targetY;
    private final ExecutorService executorService;
    private final Map<String, Boolean> obstacleCache;
    private final Random_Generator randomGenerator; // Add the Random_Generator

    /**
     * Constructor for AI_Player using a Ball object.
     *
     * @param ball         Ball object representing the golf ball.
     * @param targetX      Target X-coordinate to reach.
     * @param targetY      Target Y-coordinate to reach.
     * @param coefficients Physics coefficients for the simulation.
     */
    public Random_AI_Player(Ball ball, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.ball = ball;
        this.targetX = targetX;
        this.targetY = targetY;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.obstacleCache = new HashMap<>();
        this.randomGenerator = new Random_Generator(); // Initialize the Random_Generator
    }

    /**
     * Function to find the best initial velocities to reach the target using PSO.
     */
    public void findHoleInOne() {
        // Initialize swarm
        List<Particle> swarm = new ArrayList<>();
        for (int i = 0; i < SWARM_SIZE; i++) {
            Particle particle = new Particle();
            particle.position[0] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.position[1] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.velocity[0] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.velocity[1] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.bestPosition[0] = particle.position[0];
            particle.bestPosition[1] = particle.position[1];
            particle.bestDistance = distance(particle.position[0], particle.position[1], targetX, targetY);
            swarm.add(particle);
        }

        // PSO loop
        Particle globalBest = new Particle();
        globalBest.bestDistance = Double.MAX_VALUE;

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            for (Particle particle : swarm) {
                // Update velocity
                for (int j = 0; j < 2; j++) {
                    particle.velocity[j] = W * particle.velocity[j]
                            + C1 * randomGenerator.create_random(1) * (particle.bestPosition[j] - particle.position[j])
                            + C2 * randomGenerator.create_random(1) * (globalBest.bestPosition[j] - particle.position[j]);
                    // Update position
                    particle.position[j] += particle.velocity[j];
                }

                // Update best position
                double currentDistance = distance(particle.position[0], particle.position[1], targetX, targetY);
                if (currentDistance < particle.bestDistance) {
                    particle.bestDistance = currentDistance;
                    particle.bestPosition[0] = particle.position[0];
                    particle.bestPosition[1] = particle.position[1];
                }

                // Update global best
                if (currentDistance < globalBest.bestDistance) {
                    globalBest.bestDistance = currentDistance;
                    globalBest.bestPosition[0] = particle.position[0];
                    globalBest.bestPosition[1] = particle.position[1];
                }
            }
        }

        System.out.println("Best position: " + globalBest.bestPosition[0] + ", " + globalBest.bestPosition[1]);
        System.out.println("Best distance: " + globalBest.bestDistance);
    }

    /**
     * Method to get the best initial velocities to reach the target.
     */
    public double[] getBestVxVy() {
        findHoleInOne();
        Particle globalBest = new Particle();
        globalBest.bestDistance = Double.MAX_VALUE;

        // Initialize swarm and find global best
        for (int i = 0; i < SWARM_SIZE; i++) {
            Particle particle = new Particle();
            particle.position[0] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.position[1] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.velocity[0] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.velocity[1] = randomGenerator.create_random_velocity(MAX_SPEED, 1); // Use random velocity
            particle.bestPosition[0] = particle.position[0];
            particle.bestPosition[1] = particle.position[1];
            particle.bestDistance = distance(particle.position[0], particle.position[1], targetX, targetY);
            if (particle.bestDistance < globalBest.bestDistance) {
                globalBest = particle;
            }
        }

        return new double[]{globalBest.bestPosition[0], globalBest.bestPosition[1]};
    }

    /**
     * Simulate the shot for a given velocity and returns the distance to the target.
     */
    public double[] simulateShot(double vx, double vy) {
        ball = new Ball(golfBall.getTerrain());
        double[][] trajectory = ball.getTrajectoryArray(0.1, golfBall.getPosition().x, golfBall.getPosition().z, vx, vy, 30);

        if (trajectory.length == 0) {
            return new double[]{golfBall.getPosition().x, golfBall.getPosition().z};
        }

        boolean obstacleFound = false;

        for (int i = 0; i < trajectory.length - 1; i++) {
            double x1 = trajectory[i][0];
            double y1 = trajectory[i][1];
            double x2 = trajectory[i + 1][0];
            double y2 = trajectory[i + 1][1];

            if (checkObstaclesBetweenPoints(x1, y1, x2, y2)) {
                obstacleFound = true;
                break;
            }
        }

        double[] finalState = trajectory[trajectory.length - 1];
        return new double[]{finalState[0], finalState[1]};
    }

    /**
     * Check for obstacles between two points.
     */
    public boolean checkObstaclesBetweenPoints(double x1, double y1, double x2, double y2) {
        int steps = 5; // Reduced number of steps for interpolation
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double xt = x1 + t * (x2 - x1);
            double yt = y1 + t * (y2 - y1);
            String key = xt + "," + yt;

            Boolean cachedResult = obstacleCache.get(key);
            if (cachedResult != null) {
                if (cachedResult) {
                    return true;
                }
                continue;
            }

            if (golfBall != null) {
                double obstacleDistance = golfBall.checkNearestObstacles(xt, yt);

                if (obstacleDistance < 5f) { // Adjusted obstacle distance threshold
                    obstacleCache.put(key, true);
                    return true;
                } else {
                    obstacleCache.put(key, false);
                }
            } else {
                System.err.println("golfBall is null");
            }
        }
        return false;
    }

    /**
     * Calculate the Euclidean distance between two points.
     *
     * @param x1 First point x-coordinate.
     * @param y1 First point y-coordinate.
     * @param x2 Second point x-coordinate.
     * @param y2 Second point y-coordinate.
     * @return Distance between the points.
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Inner class representing a particle in the Particle Swarm Optimization (PSO) algorithm.
     */
    private static class Particle {
        double[] position = new double[2];
        double[] velocity = new double[2];
        double[] bestPosition = new double[2];
        double bestDistance;
    }

    /**
     * Main method for testing the AI_Player.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // test the bot
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));

        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

        Terrain terrain = Terrain.getInstance(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getKineticFrictionSand());

        Ball ball = new Ball(terrain);
        ball.setState(-5, 0, 0, 0);

        double targetX = -8.0;
        double targetY = 1.0;

        Random_AI_Player bot = new Random_AI_Player(ball, targetX, targetY, coefficients);
        bot.findHoleInOne();
    }
}
