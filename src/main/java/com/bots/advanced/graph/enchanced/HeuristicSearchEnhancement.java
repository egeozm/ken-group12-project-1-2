package com.bots.advanced.graph.enchanced;

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

class ShotResult {
    Position position;
    int depth;
    List<Velocity> path;
    double distanceToHole;

    ShotResult(Position position, int depth, List<Velocity> path, double distanceToHole) {
        this.position = position;
        this.depth = depth;
        this.path = path;
        this.distanceToHole = distanceToHole;
    }
}

public class HeuristicSearchEnhancement {
    private static final double HOLE_RADIUS = 0.1;  // Define a small radius for the hole
    private static final int MAX_DEPTH = 3; // Define maximum depth of recursive search
    private static final int NUM_VELOCITY_ITERATIONS = 20; // Define the number of velocity iterations
    private static final double MAX_SPEED = 20.0; // Define the maximum speed for velocity
    private static final long TIME_LIMIT_NS = 45_000_000_000L; // Time limit in nanoseconds (15 seconds)

    private GolfBall golfBall;
    private final double targetX;
    private final double targetY;
    private final double terrainWidth;
    private final double terrainHeight;
    private final Map<String, Boolean> obstacleCache = new HashMap<>();
    private ShotResult finalShotResult;
    private double closestDistanceToHole = Double.MAX_VALUE;
    private List<Velocity> closestPath = new ArrayList<>();
    private long startTime;
    private boolean obstacleFound;
    private final List<List<Velocity>> allValidPaths = new ArrayList<>();

    public HeuristicSearchEnhancement(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
    }

    public List<double[]> process() {
        startTime = System.nanoTime();
        Position startPosition = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        Position holePosition = new Position(targetX, targetY);
        findBestPath(startPosition, holePosition, 0, new ArrayList<>());
        printAllValidPaths();
        return getBestPath();
    }

    private ShotResult findBestPath(Position start, Position hole, int depth, List<Velocity> path) {
        if (System.nanoTime() - startTime > TIME_LIMIT_NS) {
            return null;
        }

        if (depth > MAX_DEPTH) {
            double distanceToHole = start.distanceTo(hole);
            if (distanceToHole < closestDistanceToHole) {
                closestDistanceToHole = distanceToHole;
                closestPath = new ArrayList<>(path);
            }
            return null;
        }

        double distanceToHole = start.distanceTo(hole);
        if (distanceToHole <= HOLE_RADIUS) {
            ShotResult shotResult = new ShotResult(start, depth, new ArrayList<>(path), distanceToHole);
            if (finalShotResult == null || depth < finalShotResult.depth || (depth == finalShotResult.depth && distanceToHole < finalShotResult.distanceToHole)) {
                finalShotResult = shotResult;
            }
            allValidPaths.add(new ArrayList<>(path));  // Add valid path to the list
            return shotResult;
        }

        ShotResult bestResult = null;

        for (int i = 0; i < NUM_VELOCITY_ITERATIONS; i++) {
            for (int j = 0; j < NUM_VELOCITY_ITERATIONS; j++) {
                double vx = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * i;
                double vy = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * j;

                if (vx == 0 && vy == 0) continue;

                Position newPosition = simulateShot(start, new Velocity(vx, vy));
                if (isWithinBounds(newPosition) && !obstacleFound) {
                    path.add(new Velocity(vx, vy));

                    ShotResult result = findBestPath(newPosition, hole, depth + 1, path);

                    if (result != null && (bestResult == null || result.distanceToHole < bestResult.distanceToHole)) {
                        bestResult = result;
                    }

                    path.remove(path.size() - 1);
                }
            }
        }

        if (bestResult == null) {
            if (distanceToHole < closestDistanceToHole) {
                closestDistanceToHole = distanceToHole;
                closestPath = new ArrayList<>(path);
            }
        } else if (bestResult.distanceToHole < closestDistanceToHole) {
            closestDistanceToHole = bestResult.distanceToHole;
            closestPath = new ArrayList<>(path);
        }

        return bestResult;
    }

    private boolean isWithinBounds(Position position) {
        return position.x >= -terrainWidth && position.x <= terrainWidth && position.y >= -terrainHeight && position.y <= terrainHeight;
    }

    private Position simulateShot(Position start, Velocity velocity) {
        System.out.println("Simulating shot from " + start + " with " + velocity);
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(start.x, start.y, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, start.x, start.y, velocity.vx, velocity.vy, 30);

        if (trajectory.length == 0) {
            System.out.println("No trajectory found. Returning start position.");
            return new Position(start.x, start.y);
        }

        obstacleFound = false;

        for (int i = 0; i < trajectory.length - 1; i++) {
            double x1 = trajectory[i][0];
            double y1 = trajectory[i][1];
            double x2 = trajectory[i + 1][0];
            double y2 = trajectory[i + 1][1];

            if (checkObstaclesBetweenPoints(x1, y1, x2, y2)) {
                System.out.println("Obstacle found between (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ")");
                obstacleFound = true;
                break;
            }
        }

        if (obstacleFound) {
            System.out.println("Returning to start position due to obstacle.");
            return new Position(start.x, start.y); // Return the start position if an obstacle is found
        }

        double[] finalState = trajectory[trajectory.length - 1];
        System.out.println("Final position after shot: x = " + finalState[0] + ", y = " + finalState[1]);
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

            if (golfBall != null) {
                double obstacleDistance = golfBall.checkNearestObstacles(xt, yt);

                if (obstacleDistance < 1f) { // Increase sensitivity to obstacles
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

    private void printAllValidPaths() {
        System.out.println("All valid paths:");
        for (List<Velocity> path : allValidPaths) {
            System.out.println("Path:");
            for (Velocity v : path) {
                System.out.println(v);
            }
        }
    }

    public List<double[]> getBestPath() {
        if (finalShotResult == null && closestPath.isEmpty()) {
            System.out.println("No path found.");
            return new ArrayList<>();
        }

        List<double[]> path = new ArrayList<>();
        List<Velocity> resultPath = finalShotResult != null ? finalShotResult.path : closestPath;
        for (Velocity v : resultPath) {
            path.add(new double[]{v.vx, v.vy});
        }

        System.out.println("Best path:");
        for (double[] v : path) {
            System.out.println("vx = " + v[0] + ", vy = " + v[1]);
        }

        return path;
    }
}
