package com.bots.advanced.graph.GraphSA;

import com.gui.objects.GolfBall;
import com.gui.terrain.Terrain;
import com.ode.Ball;

import java.util.*;

class Position {
    double x, y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distanceTo(Position other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class Velocity {
    double vx, vy;

    Velocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public String toString() {
        return "(vx = " + vx + ", vy = " + vy + ")";
    }
}

public class AdvancedSA {
    private static final double HOLE_RADIUS = 0.1;
    private static final int MAX_DEPTH = 2;
    private static final int NUM_VELOCITY_ITERATIONS = 60;
    private static final double MAX_SPEED = 16.0;
    private static final long TIME_LIMIT_NS = 5_000_000_000L;
    private static final double INITIAL_TEMPERATURE = 1000;
    private static final double COOLING_RATE = 0.99;

    private GolfBall golfBall;
    private final double targetX;
    private final double targetY;
    private final double terrainWidth;
    private final double terrainHeight;
    private final Map<String, Boolean> obstacleCache = new HashMap<>();
    private long startTime;

    public AdvancedSA(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
    }

    public List<double[]> findOptimalPath() {
        startTime = System.nanoTime();
        Position startPosition = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        Position holePosition = new Position(targetX, targetY);

        List<Velocity> bestPath = new ArrayList<>();
        double bestCost = Double.MAX_VALUE;
        double temperature = INITIAL_TEMPERATURE;

        List<Velocity> currentPath = generateRandomPath();
        double currentCost = computeCost(startPosition, currentPath, holePosition);

        while (System.nanoTime() - startTime < TIME_LIMIT_NS) {
            List<Velocity> nextPath = generateNeighbor(currentPath);
            double nextCost = computeCost(startPosition, nextPath, holePosition);

            if (nextCost == Double.MAX_VALUE) {
                continue;  // Skip paths that encounter obstacles
            }

            if (acceptanceProbability(currentCost, nextCost, temperature) > Math.random()) {
                currentPath = nextPath;
                currentCost = nextCost;
            }

            if (currentCost < bestCost) {
                bestPath = new ArrayList<>(currentPath);
                bestCost = currentCost;
            }

            temperature *= COOLING_RATE;
        }

        return convertPathToOutputFormat(bestPath);
    }

    private List<Velocity> generateRandomPath() {
        Random random = new Random();
        List<Velocity> path = new ArrayList<>();
        for (int i = 0; i < MAX_DEPTH; i++) {
            double vx = randomVelocityComponent(random);
            double vy = randomVelocityComponent(random);
            path.add(new Velocity(vx, vy));
        }
        return path;
    }

    private List<Velocity> generateNeighbor(List<Velocity> path) {
        Random random = new Random();
        List<Velocity> neighbor = new ArrayList<>(path);
        int index = random.nextInt(MAX_DEPTH);
        double vx = randomVelocityComponent(random);
        double vy = randomVelocityComponent(random);
        neighbor.set(index, new Velocity(vx, vy));
        return neighbor;
    }

    private double randomVelocityComponent(Random random) {
        return -MAX_SPEED + (random.nextInt(NUM_VELOCITY_ITERATIONS) * 2.0 * MAX_SPEED / NUM_VELOCITY_ITERATIONS);
    }

    private double computeCost(Position start, List<Velocity> path, Position hole) {
        Position currentPosition = simulatePath(start, path);
        if (currentPosition == null) {
            return Double.MAX_VALUE;  // Return maximum cost if an obstacle is encountered
        }
        return currentPosition.distanceTo(hole);
    }

    private Position simulatePath(Position start, List<Velocity> path) {
        Position position = new Position(start.x, start.y);
        for (Velocity velocity : path) {
            Position previousPosition = new Position(position.x, position.y);
            position = simulateShot(position, velocity);
            if (position == null || previousPosition.distanceTo(position) < 0.5) {
                return null;  // Return null if an obstacle is encountered
            }
        }
        return position;
    }

    private Position simulateShot(Position start, Velocity velocity) {
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(start.x, start.y, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, start.x, start.y, velocity.vx, velocity.vy, 30);

        if (trajectory.length == 0) {
            return new Position(start.x, start.y);
        }

        for (int i = 0; i < trajectory.length - 1; i++) {
            double x1 = trajectory[i][0];
            double y1 = trajectory[i][1];
            double x2 = trajectory[i + 1][0];
            double y2 = trajectory[i + 1][1];

            if (checkObstaclesBetweenPoints(x1, y1, x2, y2) || !isWithinBounds(new Position(x2, y2))) {
                return null;  // Return null if an obstacle is encountered
            }
        }

        double[] finalState = trajectory[trajectory.length - 1];
        return new Position(finalState[0], finalState[1]);
    }

    private boolean checkObstaclesBetweenPoints(double x1, double y1, double x2, double y2) {
        int steps = 5;
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

            double obstacleDistance = golfBall.checkNearestObstacles(xt, yt);

            if (obstacleDistance < 1f) {  // Increase sensitivity to obstacles
                obstacleCache.put(key, true);
                return true;
            } else {
                obstacleCache.put(key, false);
            }
        }
        return false;
    }

    private boolean isWithinBounds(Position position) {
        return position.x >= -terrainWidth && position.x <= terrainWidth && position.y >= -terrainHeight && position.y <= terrainHeight;
    }

    private double acceptanceProbability(double currentCost, double nextCost, double temperature) {
        if (nextCost < currentCost) {
            return 1.0;
        }
        return Math.exp((currentCost - nextCost) / temperature);
    }

    private List<double[]> convertPathToOutputFormat(List<Velocity> path) {
        List<double[]> outputPath = new ArrayList<>();
        for (Velocity v : path) {
            outputPath.add(new double[]{v.vx, v.vy});
        }
        return outputPath;
    }
}
