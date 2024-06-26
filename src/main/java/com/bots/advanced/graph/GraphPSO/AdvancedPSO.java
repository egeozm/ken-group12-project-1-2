package com.bots.advanced.graph.GraphPSO;

import com.gui.objects.GolfBall;
import com.gui.terrain.Terrain;
import com.ode.Ball;

import java.util.*;

class PSOPosition {
    double x, y;

    PSOPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distanceTo(PSOPosition other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class PSOVelocity {
    double vx, vy;

    PSOVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    void constrainToMaxSpeed(double maxSpeed) {
        double speed = Math.sqrt(vx * vx + vy * vy);
        if (speed > maxSpeed) {
            double scale = maxSpeed / speed;
            vx *= scale;
            vy *= scale;
        }
    }

    @Override
    public String toString() {
        return "(vx = " + vx + ", vy = " + vy + ")";
    }
}

class PSOParticle {
    List<PSOVelocity> velocities;
    List<PSOVelocity> bestVelocities;
    double bestDistanceToHole;

    PSOParticle(int maxDepth) {
        velocities = new ArrayList<>();
        bestVelocities = new ArrayList<>();
        for (int i = 0; i < maxDepth; i++) {
            velocities.add(new PSOVelocity(0, 0));
            bestVelocities.add(new PSOVelocity(0, 0));
        }
        bestDistanceToHole = Double.MAX_VALUE;
    }
}

public class AdvancedPSO {
    private static final double HOLE_RADIUS = 0.1;  // Define a small radius for the hole
    private static final int MAX_DEPTH = 2; // Define maximum depth of recursive search
    private static final int NUM_VELOCITY_ITERATIONS = 60; // Define the number of velocity iterations
    private static final double MAX_SPEED = 16.0; // Define the maximum speed for velocity
    private static final long TIME_LIMIT_NS = 15_000_000_000L; // Time limit in nanoseconds (15 seconds)
    private static final double INERTIA_WEIGHT = 0.5;
    private static final double COGNITIVE_COEFF = 1.5;
    private static final double SOCIAL_COEFF = 1.5;

    private GolfBall golfBall;
    private final double targetX;
    private final double targetY;
    private final double terrainWidth;
    private final double terrainHeight;
    private final Map<String, Boolean> obstacleCache = new HashMap<>();
    private List<PSOParticle> particles;
    private List<PSOVelocity> globalBestVelocities;
    private double globalBestDistanceToHole;
    private long startTime;
    private boolean obstacleFound;

    public AdvancedPSO(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
        this.globalBestVelocities = new ArrayList<>();
        this.globalBestDistanceToHole = Double.MAX_VALUE;
        for (int i = 0; i < MAX_DEPTH; i++) {
            globalBestVelocities.add(new PSOVelocity(0, 0));
        }
    }

    public List<double[]> optimize() {
        startTime = System.nanoTime();
        initializeParticles();
        PSOPosition startPosition = new PSOPosition(golfBall.getPosition().x, golfBall.getPosition().z);
        PSOPosition holePosition = new PSOPosition(targetX, targetY);

        int iteration = 0;
        while (System.nanoTime() - startTime < TIME_LIMIT_NS) {
            for (PSOParticle particle : particles) {
                double distanceToHole = evaluatePath(startPosition, holePosition, particle.velocities);
                if (distanceToHole < particle.bestDistanceToHole) {
                    particle.bestDistanceToHole = distanceToHole;
                    particle.bestVelocities = new ArrayList<>(particle.velocities);
                }
                if (distanceToHole < globalBestDistanceToHole) {
                    globalBestDistanceToHole = distanceToHole;
                    globalBestVelocities = new ArrayList<>(particle.velocities);
                }
            }

            updateParticles();

            iteration++;
        }

        return getBestPath();
    }

    private void initializeParticles() {
        particles = new ArrayList<>();
        for (int i = 0; i < NUM_VELOCITY_ITERATIONS; i++) {
            PSOParticle particle = new PSOParticle(MAX_DEPTH);
            for (int j = 0; j < MAX_DEPTH; j++) {
                double vx = -MAX_SPEED + 2 * MAX_SPEED * Math.random();
                double vy = -MAX_SPEED + 2 * MAX_SPEED * Math.random();
                particle.velocities.set(j, new PSOVelocity(vx, vy));
            }
            particles.add(particle);
        }
    }

    private double evaluatePath(PSOPosition start, PSOPosition hole, List<PSOVelocity> velocities) {
        PSOPosition currentPosition = start;
        for (PSOVelocity velocity : velocities) {
            currentPosition = simulateShot(currentPosition, velocity);
            if (obstacleFound) {
                return Double.MAX_VALUE;
            }
        }
        return currentPosition.distanceTo(hole);
    }

    private void updateParticles() {
        for (PSOParticle particle : particles) {
            for (int i = 0; i < MAX_DEPTH; i++) {
                PSOVelocity currentVelocity = particle.velocities.get(i);
                PSOVelocity bestVelocity = particle.bestVelocities.get(i);
                PSOVelocity globalVelocity = globalBestVelocities.get(i);

                double newVx = INERTIA_WEIGHT * currentVelocity.vx +
                        COGNITIVE_COEFF * Math.random() * (bestVelocity.vx - currentVelocity.vx) +
                        SOCIAL_COEFF * Math.random() * (globalVelocity.vx - currentVelocity.vx);
                double newVy = INERTIA_WEIGHT * currentVelocity.vy +
                        COGNITIVE_COEFF * Math.random() * (bestVelocity.vy - currentVelocity.vy) +
                        SOCIAL_COEFF * Math.random() * (globalVelocity.vy - currentVelocity.vy);

                PSOVelocity newVelocity = new PSOVelocity(newVx, newVy);
                newVelocity.constrainToMaxSpeed(MAX_SPEED);
                particle.velocities.set(i, newVelocity);
            }
        }
    }

    private PSOPosition simulateShot(PSOPosition start, PSOVelocity velocity) {
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(start.x, start.y, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, start.x, start.y, velocity.vx, velocity.vy, 30);

        if (trajectory.length == 0) {
            return new PSOPosition(start.x, start.y);
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

        if (obstacleFound) {
            return new PSOPosition(start.x, start.y);
        }

        double[] finalState = trajectory[trajectory.length - 1];
        return new PSOPosition(finalState[0], finalState[1]);
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

                if (obstacleDistance < 1f || !(xt >= terrainWidth*-1 && xt <= terrainWidth && yt >= terrainHeight*-1 && yt <= terrainHeight)) { // Increase sensitivity to obstacles
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

    public List<double[]> getBestPath() {
        List<double[]> path = new ArrayList<>();
        for (PSOVelocity v : globalBestVelocities) {
            path.add(new double[]{v.vx, v.vy});
        }

        // Output the best path
        System.out.println("Best path:");
        for (double[] v : path) {
            System.out.println("vx = " + v[0] + ", vy = " + v[1]);
        }

        return path;
    }
}
