package com.example;

import com.gui.GolfBall;
import com.gui.Terrain;

import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class AI_Player {

    private static final double MAX_SPEED = 5.0;
    private static final int SWARM_SIZE = 10; // Reduced swarm size
    private static final int MAX_ITERATIONS = 100; // Reduced iterations
    private static final double W = 0.5;  // Inertia weight
    private static final double C1 = 1.0; // Cognitive coefficient
    private static final double C2 = 1.5; // Social coefficient
    private static final double CHECK_INTERVAL = 5; // Check obstacles every 5th point

    private GolfBall golfBall;
    private Ball ball;
    private double targetX;
    private double targetY;
    private PhysicsCoefficients coefficients;
    private double initialBallX;
    private double initialBallY;
    private boolean obstacleFound;
    private ExecutorService executorService;
    private Map<String, Boolean> obstacleCache;

    public AI_Player(Ball ball, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.ball = ball;
        this.targetX = targetX;
        this.targetY = targetY;
        this.coefficients = coefficients;
        this.initialBallX = ball.getX();
        this.initialBallY = ball.getY();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.obstacleCache = new HashMap<>();
    }

    public AI_Player(GolfBall golfBall, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.coefficients = coefficients;
        this.initialBallX = golfBall.getPosition().x;
        this.initialBallY = golfBall.getPosition().z;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.obstacleCache = new HashMap<>();
    }

    public double[] getBestVxVy() throws InterruptedException, ExecutionException {
        Particle[] swarm = new Particle[SWARM_SIZE];
        final double[][] globalBestPosition = {new double[2]};
        final double[] globalBestDistance = {Double.MAX_VALUE};

        Random rand = new Random();

        // Initialize the swarm
        for (int i = 0; i < SWARM_SIZE; i++) {
            swarm[i] = new Particle();
            swarm[i].position[0] = rand.nextDouble() * MAX_SPEED - MAX_SPEED / 2;
            swarm[i].position[1] = rand.nextDouble() * MAX_SPEED - MAX_SPEED / 2;
            swarm[i].velocity[0] = rand.nextDouble() - 0.5;
            swarm[i].velocity[1] = rand.nextDouble() - 0.5;
            swarm[i].bestPosition = swarm[i].position.clone();
            swarm[i].bestDistance = Double.MAX_VALUE;
        }

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            List<Future<Particle>> futures = new ArrayList<>();
            for (Particle particle : swarm) {
                futures.add(executorService.submit(() -> {
                    double[] result = simulateShot(particle.position[0], particle.position[1]);
                    if (obstacleFound) {
                        return particle;
                    }
                    double distanceToTarget = distance(result[0], result[1], targetX, targetY);

                    if (distanceToTarget < particle.bestDistance) {
                        particle.bestDistance = distanceToTarget;
                        particle.bestPosition = particle.position.clone();
                    }

                    if (distanceToTarget < globalBestDistance[0]) {
                        synchronized (globalBestPosition) {
                            if (distanceToTarget < globalBestDistance[0]) {
                                globalBestDistance[0] = distanceToTarget;
                                globalBestPosition[0] = particle.position.clone();
                            }
                        }
                    }

                    // Update velocity
                    for (int d = 0; d < 2; d++) {
                        double r1 = rand.nextDouble();
                        double r2 = rand.nextDouble();
                        particle.velocity[d] = W * particle.velocity[d] + C1 * r1 * (particle.bestPosition[d] - particle.position[d])
                                + C2 * r2 * (globalBestPosition[0][d] - particle.position[d]);
                        particle.position[d] += particle.velocity[d];
                    }
                    return particle;
                }));
            }

            for (Future<Particle> future : futures) {
                future.get();
            }

            if (globalBestDistance[0] <= coefficients.getTargetRadius()) {
                System.out.println("Hole in one");
                break;
            }
        }

        executorService.shutdown();
        return globalBestPosition[0];
    }

    public void findHoleInOne() {
        try {
            double[] bestVxVy = getBestVxVy();
            System.out.println("Best initial velocity: vx = " + bestVxVy[0] + ", vy = " + bestVxVy[1]);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private double[] simulateShot(double vx, double vy) {
        ball = new Ball(golfBall.getTerrain());
        double[][] trajectory = ball.getTrajectoryArray(0.1, golfBall.getPosition().x, golfBall.getPosition().z, vx, vy, 30);

        if (trajectory.length == 0) {
            return new double[]{golfBall.getPosition().x, golfBall.getPosition().z};
        }

        obstacleFound = false;

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

    private boolean checkObstaclesBetweenPoints(double x1, double y1, double x2, double y2) {
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

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private class Particle {
        double[] position = new double[2];
        double[] velocity = new double[2];
        double[] bestPosition = new double[2];
        double bestDistance;
    }

    public static void main(String[] args) {
        // test the bot
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));

        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

        Terrain terrain = new Terrain(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getKineticFrictionSand());

        Ball ball = new Ball(terrain);
        ball.setState(-5, 0, 0, 0);

        double targetX = -8.0;
        double targetY = 1.0;

        AI_Player bot = new AI_Player(ball, targetX, targetY, coefficients);
        bot.findHoleInOne();
    }
}
