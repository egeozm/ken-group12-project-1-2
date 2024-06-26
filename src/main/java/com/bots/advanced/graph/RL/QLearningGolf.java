package com.bots.advanced.graph.RL;

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

public class QLearningGolf {
    private static final double HOLE_RADIUS = 0.1;
    private static final int MAX_DEPTH = 3;
    private static final int NUM_VELOCITY_ITERATIONS = 20;
    private static final double MAX_SPEED = 20.0;
    private static final long TIME_LIMIT_NS = 45_000_000_000L;
    private static final double ALPHA = 0.1;
    private static final double GAMMA = 0.9;
    private static final double EPSILON = 0.1;

    private GolfBall golfBall;
    private final double targetX;
    private final double targetY;
    private final double terrainWidth;
    private final double terrainHeight;
    private final Map<String, Boolean> obstacleCache = new HashMap<>();
    private Map<String, Double> Q = new HashMap<>();
    private Random random = new Random();

    public QLearningGolf(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
    }

    public List<double[]> process() {
        Position startPosition = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        Position holePosition = new Position(targetX, targetY);

        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < TIME_LIMIT_NS) {
            QLearningEpisode(startPosition, holePosition);
        }

        return getBestPath(startPosition, holePosition);
    }

    private void QLearningEpisode(Position start, Position hole) {
        Position currentPosition = start;
        List<Velocity> path = new ArrayList<>();
        int depth = 0;

        while (depth < MAX_DEPTH && currentPosition.distanceTo(hole) > HOLE_RADIUS) {
            Velocity action = chooseAction(currentPosition);
            Position newPosition = simulateShot(currentPosition, action);
            double reward = calculateReward(newPosition, hole);
            double maxQ = getMaxQ(newPosition);

            String stateAction = currentPosition.toString() + action.toString();
            double oldQ = Q.getOrDefault(stateAction, 0.0);
            double newQ = oldQ + ALPHA * (reward + GAMMA * maxQ - oldQ);
            Q.put(stateAction, newQ);

            currentPosition = newPosition;
            path.add(action);
            depth++;
        }
    }

    private Velocity chooseAction(Position state) {
        if (random.nextDouble() < EPSILON) {
            // Explore: choose a random action
            double vx = -MAX_SPEED + 2 * MAX_SPEED * random.nextDouble();
            double vy = -MAX_SPEED + 2 * MAX_SPEED * random.nextDouble();
            return new Velocity(vx, vy);
        } else {
            // Exploit: choose the best action based on Q-values
            double bestQ = Double.NEGATIVE_INFINITY;
            Velocity bestAction = null;
            for (int i = 0; i < NUM_VELOCITY_ITERATIONS; i++) {
                for (int j = 0; j < NUM_VELOCITY_ITERATIONS; j++) {
                    double vx = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * i;
                    double vy = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * j;
                    Velocity action = new Velocity(vx, vy);
                    String stateAction = state.toString() + action.toString();
                    double q = Q.getOrDefault(stateAction, 0.0);
                    if (q > bestQ) {
                        bestQ = q;
                        bestAction = action;
                    }
                }
            }
            return bestAction;
        }
    }

    private double getMaxQ(Position state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < NUM_VELOCITY_ITERATIONS; i++) {
            for (int j = 0; j < NUM_VELOCITY_ITERATIONS; j++) {
                double vx = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * i;
                double vy = -MAX_SPEED + 2 * MAX_SPEED / NUM_VELOCITY_ITERATIONS * j;
                Velocity action = new Velocity(vx, vy);
                String stateAction = state.toString() + action.toString();
                double q = Q.getOrDefault(stateAction, 0.0);
                if (q > maxQ) {
                    maxQ = q;
                }
            }
        }
        return maxQ;
    }

    private double calculateReward(Position position, Position hole) {
        if (position.distanceTo(hole) <= HOLE_RADIUS) {
            return 1000; // High reward for reaching the hole
        } else {
            return -position.distanceTo(hole); // Negative reward proportional to distance from hole
        }
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

            if (checkObstaclesBetweenPoints(x1, y1, x2, y2)) {
                return new Position(start.x, start.y);
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

            if (golfBall != null) {
                double obstacleDistance = golfBall.checkNearestObstacles(xt, yt);

                if (obstacleDistance < 1f) {
                    obstacleCache.put(key, true);
                    return true;
                } else {
                    obstacleCache.put(key, false);
                }
            }
        }
        return false;
    }

    private List<double[]> getBestPath(Position start, Position hole) {
        List<double[]> bestPath = new ArrayList<>();
        Position currentPosition = start;

        for (int depth = 0; depth < MAX_DEPTH && currentPosition.distanceTo(hole) > HOLE_RADIUS; depth++) {
            Velocity bestAction = chooseAction(currentPosition);
            bestPath.add(new double[]{bestAction.vx, bestAction.vy});
            currentPosition = simulateShot(currentPosition, bestAction);
        }

        return bestPath;
    }
}
