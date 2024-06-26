package com.bots.advanced.graph.MonteCarlo;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.x, x) == 0 &&
                Double.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Velocity velocity = (Velocity) o;
        return Double.compare(velocity.vx, vx) == 0 &&
                Double.compare(velocity.vy, vy) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vx, vy);
    }
}

class Node {
    Position state;
    Node parent;
    List<Node> children;
    int visits;
    double reward;
    Velocity action;

    Node(Position state, Node parent, Velocity action) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.reward = 0.0;
    }
}

public class MonteCarloTreeSearch {
    private static final double HOLE_RADIUS = 0.1;
    private static final int DEFAULT_MAX_DEPTH = 5;
    private static final double MAX_SPEED = 1.0;
    private static final long TIME_LIMIT_NS = 15_000_000_000L; // 15 seconds in nanoseconds
    private static final int DEFAULT_MAX_ITERATIONS = 1000;
    private static final double DEFAULT_EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final int DEFAULT_ROLL_OUT_DEPTH = 10;
    private static final int DEFAULT_NUM_TRAJECTORY_POINTS = 30;
    private static final float DEFAULT_OBSTACLE_SENSITIVITY = 1.0f;

    private GolfBall golfBall;
    private Terrain terrain;
    private final double targetX;
    private final double targetY;
    private final double terrainWidth;
    private final double terrainHeight;
    private final Map<String, Boolean> obstacleCache = new HashMap<>();
    private long startTime;

    private int maxDepth;
    private int maxIterations;
    private double explorationConstant;
    private int rollOutDepth;
    private int numTrajectoryPoints;
    private float obstacleSensitivity;

    public MonteCarloTreeSearch(GolfBall golfBall, double targetX, double targetY) {
        this(golfBall, Terrain.getInstance(), targetX, targetY, DEFAULT_MAX_DEPTH, DEFAULT_MAX_ITERATIONS, DEFAULT_EXPLORATION_CONSTANT, DEFAULT_ROLL_OUT_DEPTH, DEFAULT_NUM_TRAJECTORY_POINTS, DEFAULT_OBSTACLE_SENSITIVITY);
    }

    public MonteCarloTreeSearch(GolfBall golfBall, Terrain terrain, double targetX, double targetY, int maxDepth, int maxIterations, double explorationConstant, int rollOutDepth, int numTrajectoryPoints, float obstacleSensitivity) {
        this.golfBall = golfBall;
        this.terrain = terrain;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = terrain.getWidth();
        this.terrainHeight = terrain.getHeight();
        this.maxDepth = maxDepth;
        this.maxIterations = maxIterations;
        this.explorationConstant = explorationConstant;
        this.rollOutDepth = rollOutDepth;
        this.numTrajectoryPoints = numTrajectoryPoints;
        this.obstacleSensitivity = obstacleSensitivity;

        if (golfBall == null) {
            throw new IllegalArgumentException("golfBall cannot be null");
        }
    }

    public List<double[]> findBestPath() {
        startTime = System.nanoTime();
        Position startPosition = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        Node rootNode = new Node(startPosition, null, null);

        int iteration = 0;
        while (System.nanoTime() - startTime < TIME_LIMIT_NS && iteration < maxIterations) {
            Node node = selectNode(rootNode);
            double reward = rollout(node.state, rollOutDepth - getDepth(node));
            backpropagate(node, reward);
            iteration++;
            if (iteration % 100 == 0) {
                System.out.println("Iteration: " + iteration + " Best reward so far: " + rootNode.reward);
            }
        }

        return extractBestPath(rootNode);
    }

    private Node selectNode(Node node) {
        while (!isTerminal(node.state) && getDepth(node) < maxDepth) {
            if (!fullyExpanded(node)) {
                return expand(node);
            } else {
                node = bestUCT(node);
            }
        }
        return node;
    }

    private Node expand(Node node) {
        Position state = node.state;
        Velocity action = getUntriedAction(node);
        Position newState = simulateShot(state, action);
        if (isWithinBounds(newState)) {
            Node childNode = new Node(newState, node, action);
            node.children.add(childNode);
            return childNode;
        } else {
            return node; // If the new state is out of bounds, return the same node without expanding
        }
    }

    private double rollout(Position state, int remainingDepth) {
        while (!isTerminal(state) && remainingDepth > 0) {
            Velocity action = getDirectedRandomAction(state);
            state = simulateShot(state, action);
            if (!isWithinBounds(state)) {
                break;
            }
            remainingDepth--;
        }
        return getReward(state);
    }

    private void backpropagate(Node node, double reward) {
        while (node != null) {
            node.visits++;
            node.reward += reward;
            node = node.parent;
        }
    }

    private Node bestUCT(Node node) {
        Node bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Node child : node.children) {
            double uctScore = child.reward / child.visits +
                    explorationConstant * Math.sqrt(Math.log(node.visits) / child.visits);
            if (uctScore > bestScore) {
                bestScore = uctScore;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private boolean fullyExpanded(Node node) {
        return node.children.size() == getPossibleActions().size();
    }

    private int getDepth(Node node) {
        int depth = 0;
        while (node.parent != null) {
            node = node.parent;
            depth++;
        }
        return depth;
    }

    private boolean isTerminal(Position state) {
        Position hole = new Position(targetX, targetY);
        return state.distanceTo(hole) <= HOLE_RADIUS;
    }

    private Velocity getUntriedAction(Node node) {
        Set<Velocity> triedActions = new HashSet<>();
        for (Node child : node.children) {
            triedActions.add(child.action);
        }

        for (Velocity action : getPossibleActions()) {
            if (!triedActions.contains(action)) {
                return action;
            }
        }

        return null; // Should not happen if fullyExpanded is correctly implemented
    }

    private Velocity getRandomAction() {
        Random random = new Random();
        double vx = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
        double vy = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
        return new Velocity(vx, vy);
    }

    private Velocity getDirectedRandomAction(Position state) {
        Random random = new Random();
        double directionX = targetX - state.x;
        double directionY = targetY - state.y;
        double norm = Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= norm;
        directionY /= norm;
        double variance = 0.5; // Adjust the randomness
        double vx = directionX * MAX_SPEED + variance * (-MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED);
        double vy = directionY * MAX_SPEED + variance * (-MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED);
        return new Velocity(vx, vy);
    }

    private List<Velocity> getPossibleActions() {
        List<Velocity> actions = new ArrayList<>();
        int numIterations = 20;
        for (int i = 0; i < numIterations; i++) {
            for (int j = 0; j < numIterations; j++) {
                double vx = -MAX_SPEED + 2 * MAX_SPEED / numIterations * i;
                double vy = -MAX_SPEED + 2 * MAX_SPEED / numIterations * j;
                actions.add(new Velocity(vx, vy));
            }
        }
        return actions;
    }

    private double getReward(Position state) {
        Position hole = new Position(targetX, targetY);
        double distance = state.distanceTo(hole);
        return -distance; // Negative distance to the hole as reward
    }

    private Position simulateShot(Position start, Velocity velocity) {
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(start.x, start.y, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, start.x, start.y, velocity.vx, velocity.vy, numTrajectoryPoints);

        if (trajectory.length == 0) {
            return new Position(start.x, start.y);
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

        if (obstacleFound) {
            return start;
        }

        double newX = trajectory[trajectory.length - 1][0];
        double newY = trajectory[trajectory.length - 1][1];
        return new Position(newX, newY);
    }

    private boolean checkObstaclesBetweenPoints(double x1, double y1, double x2, double y2) {
        int numSteps = 10;
        for (int i = 1; i <= numSteps; i++) {
            double t = (double) i / numSteps;
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

            if (obstacleDistance < obstacleSensitivity) { // Increased sensitivity to obstacles
                obstacleCache.put(key, true);
                return true;
            } else {
                obstacleCache.put(key, false);
            }
        }
        return false;
    }

    private boolean isWithinBounds(Position position) {
        return position.x >= 0 && position.x <= terrainWidth && position.y >= 0 && position.y <= terrainHeight;
    }

    private List<double[]> extractBestPath(Node rootNode) {
        List<Velocity> bestPath = new ArrayList<>();
        Node bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Node child : rootNode.children) {
            double score = child.reward / child.visits;
            if (score > bestScore) {
                bestScore = score;
                bestChild = child;
            }
        }

        while (bestChild != null) {
            bestPath.add(bestChild.action);
            bestChild = bestChild.children.isEmpty() ? null : bestChild.children.get(0);
        }

        List<double[]> path = new ArrayList<>();
        for (Velocity v : bestPath) {
            path.add(new double[]{v.vx, v.vy});
        }

        System.out.println("Extracted best path: " + path);
        return path;
    }
}
