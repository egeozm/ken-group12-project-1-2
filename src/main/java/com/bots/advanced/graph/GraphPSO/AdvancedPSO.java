package com.bots.advanced.graph.GraphPSO;

import com.ode.Ball;
import com.ode.PhysicsCoefficients;
import com.gui.objects.GolfBall;
import com.gui.terrain.Terrain;

import java.util.concurrent.*;
import java.util.*;

import static com.gui.terrain.Terrain.getInstance;

public class AdvancedPSO {

    private static final double MAX_SPEED = 20.0;
    private static final int MAX_DEPTH = 5;
    private static final int NUM_VELOCITY_STEPS = 4;

    private GolfBall golfBall;
    private Ball ball;
    private final double targetX;
    private final double targetY;
    private final PhysicsCoefficients coefficients;
    private boolean obstacleFound;
    private final ExecutorService executorService;
    private final Map<String, Boolean> obstacleCache;

    public AdvancedPSO(Ball ball, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.ball = ball;
        this.targetX = targetX;
        this.targetY = targetY;
        this.coefficients = coefficients;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.obstacleCache = new HashMap<>();
    }

    public AdvancedPSO(GolfBall golfBall, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.coefficients = coefficients;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.obstacleCache = new HashMap<>();
    }

    private List<Node> generateSuccessors(Node current) {
        List<Node> successors = new ArrayList<>();
        double velocityStep = MAX_SPEED / NUM_VELOCITY_STEPS;

        for (double vx = -MAX_SPEED; vx <= MAX_SPEED; vx += velocityStep) {
            for (double vy = -MAX_SPEED; vy <= MAX_SPEED; vy += velocityStep) {
                if (vx == 0 && vy == 0) continue;  // Ensure no zero velocity shots are generated
                double[] result = simulateShot(current.x, current.y, vx, vy);
                if (!obstacleFound) {
                    double distanceToTarget = distance(result[0], result[1], targetX, targetY);
                    double cost = current.cost + distance(current.x, current.y, result[0], result[1]);
                    if (isReachable(current.x, current.y, result[0], result[1], vx, vy)) {
                        Node successor = new Node(result[0], result[1], cost, distanceToTarget, current, vx, vy);
                        successors.add(successor);
                        System.out.println("Generated successor: x = " + successor.x + ", y = " + successor.y + ", cost = " + successor.cost + ", heuristic = " + successor.heuristic + ", vx = " + vx + ", vy = " + vy);
                    }
                }
            }
        }
        return successors;
    }

    private boolean isReachable(double startX, double startY, double endX, double endY, double vx, double vy) {
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(startX, startY, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, startX, startY, vx, vy, 30);

        if (trajectory.length == 0) return false;

        double finalX = trajectory[trajectory.length - 1][0];
        double finalY = trajectory[trajectory.length - 1][1];

        return Math.abs(finalX - endX) < 1e-2 && Math.abs(finalY - endY) < 1e-2;
    }

    private Node findBestPath() {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getTotalCost));
        Set<Node> closedList = new HashSet<>();
        Node bestNode = null;

        double initialVx = 0.1;
        double initialVy = 0.1;
        Node start = new Node(golfBall.getPosition().x, golfBall.getPosition().z, 0, distance(golfBall.getPosition().x, golfBall.getPosition().z, targetX, targetY), null, initialVx, initialVy);
        openList.add(start);
        bestNode = start;

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            System.out.println("Expanding node at position: x = " + current.x + ", y = " + current.y + " with cost = " + current.cost + " and heuristic = " + current.heuristic);

            if (bestNode == null || current.heuristic < bestNode.heuristic) {
                bestNode = current;
            }

            if (current.heuristic <= coefficients.getTargetRadius()) {
                return bestNode;  // Return the best node found so far
            }

            if (current.cost < MAX_DEPTH) {
                closedList.add(current);
                List<Node> successors = generateSuccessors(current);

                for (Node successor : successors) {
                    if (closedList.contains(successor)) continue;
                    openList.add(successor);
                }
            }
        }
        return bestNode;
    }

    public List<double[]> getBestPath() throws InterruptedException, ExecutionException {
        Node bestPath = findBestPath();
        if (bestPath == null) {
            System.out.println("No path found.");
            return new ArrayList<>();
        }

        List<double[]> path = new ArrayList<>();
        Node current = bestPath;

        while (current != null) {
            path.add(0, new double[]{current.x, current.y, current.vx, current.vy});
            current = current.parent;
        }

        System.out.println("Best path:");
        for (double[] step : path) {
            System.out.println("x = " + step[0] + ", y = " + step[1] + ", vx = " + step[2] + ", vy = " + step[3]);
        }
        return path;
    }

    private double[] simulateShot(double startX, double startY, double vx, double vy) {
        System.out.println("Simulating shot from x = " + startX + ", y = " + startY + " with vx = " + vx + ", vy = " + vy);
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(startX, startY, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, startX, startY, vx, vy, 30);

        if (trajectory.length == 0) {
            System.out.println("No trajectory found. Returning start position.");
            return new double[]{startX, startY};
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

        double[] finalState = trajectory[trajectory.length - 1];
        System.out.println("Final position after shot: x = " + finalState[0] + ", y = " + finalState[1]);
        return new double[]{finalState[0], finalState[1]};
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

                if (obstacleDistance < 5f) {
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

    private static class Node {
        double x, y;
        double cost;
        double heuristic;
        double vx, vy;
        Node parent;

        public Node(double x, double y, double cost, double heuristic, Node parent, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.heuristic = heuristic;
            this.parent = parent;
            this.vx = vx;
            this.vy = vy;
        }

        public double getTotalCost() {
            return this.cost + this.heuristic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Double.compare(node.x, x) == 0 && Double.compare(node.y, y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
