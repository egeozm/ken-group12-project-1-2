package com.bots.advanced.graph.genetic;

import com.gui.objects.GolfBall;
import com.gui.terrain.Terrain;
import com.ode.Ball;

import java.util.*;

public class VariableLengthChromosomeGeneticAlgorithm {
    private static final int INITIAL_POPULATION_SIZE = 280;
    private static final double MUTATION_RATE = 0.05; // Increased for better exploration
    private static final double MAX_SPEED = 13.0;
    private static final int MAX_DEPTH = 1;
    private static final long TIME_LIMIT_NS = 155_000_000_000L; // 5 seconds in nanoseconds

    private GolfBall golfBall;
    private double targetX;
    private double targetY;
    private double terrainWidth;
    private double terrainHeight;
    private Position holePosition;
    private Map<String, Boolean> obstacleCache;
    private Position startPos;
    private long startTime;
    private final Random random = new Random();

    public VariableLengthChromosomeGeneticAlgorithm(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
        this.holePosition = new Position(targetX, targetY);
        this.obstacleCache = new HashMap<>();
    }

    public VariableLengthChromosomeGeneticAlgorithm(Position startPos, double targetX, double targetY) {
        this.startPos = startPos;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
        this.holePosition = new Position(targetX, targetY);
        this.obstacleCache = new HashMap<>();
    }

    public List<double[]> findOptimalPath() {
        Position start = (startPos == null) ? new Position(golfBall.getPosition().x, golfBall.getPosition().z) : startPos;
        List<List<Velocity>> population = generateInitialPopulation(start);
        startTime = System.nanoTime();

        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            if (System.nanoTime() - startTime > TIME_LIMIT_NS) {
                System.out.println("Time limit reached, stopping generations.");
                break;
            }
            population = expandPopulation(population, start, depth);
        }

        List<Velocity> bestPath = findBestPath(population, start);
        if (bestPath == null) {
            System.out.println("No valid path found.");
            return Collections.emptyList();
        }
        return convertPathToOutputFormat(bestPath);
    }

    private List<List<Velocity>> generateInitialPopulation(Position start) {
        List<List<Velocity>> population = new ArrayList<>();
        while (population.size() < INITIAL_POPULATION_SIZE) {
            List<Velocity> chromosome = new ArrayList<>();
            chromosome.add(new Velocity(randomShot(), randomShot()));
            if (isValidPath(start, chromosome)) {
                population.add(chromosome);
            }
        }
        return population;
    }

    private double randomShot() {
        return -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
    }

    private List<List<Velocity>> expandPopulation(List<List<Velocity>> population, Position start, int depth) {
        List<List<Velocity>> newPopulation = new ArrayList<>();
        while (newPopulation.size() < INITIAL_POPULATION_SIZE) {
            for (List<Velocity> chromosome : population) {
                if (isValidPath(start, chromosome)) {
                    List<Velocity> newChromosome = new ArrayList<>(chromosome);
                    newChromosome.add(new Velocity(randomShot(), randomShot()));
                    if (random.nextDouble() < MUTATION_RATE) {
                        mutate(newChromosome);
                    }
                    if (isValidPath(start, newChromosome)) {
                        newPopulation.add(newChromosome);
                    }
                    if (newPopulation.size() >= INITIAL_POPULATION_SIZE) {
                        break;
                    }
                }
            }
        }
        return newPopulation;
    }

    private boolean isValidPath(Position start, List<Velocity> path) {
        return simulatePath(start, path) != null;
    }

    private void mutate(List<Velocity> chromosome) {
        int index = random.nextInt(chromosome.size());
        chromosome.set(index, new Velocity(randomShot(), randomShot()));
    }

    private List<Velocity> findBestPath(List<List<Velocity>> population, Position start) {
        List<Velocity> bestPath = null;
        double bestFitness = Double.MIN_VALUE;

        for (List<Velocity> path : population) {
            Position endPosition = simulatePath(start, path);
            if (endPosition != null) {
                double fitness = 1.0 / (endPosition.distanceTo(holePosition) + 1);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestPath = path;
                }
                printVelocityInfo(path, fitness);
            }
        }
        return bestPath;
    }

    private void printVelocityInfo(List<Velocity> path, double fitness) {
        for (Velocity velocity : path) {
            System.out.print(velocity + " ");
        }
        String status = fitness > 0.01 ? "Good" : "Bad";
        System.out.println(" - " + status + " fitness: " + fitness);
    }

    private Position simulatePath(Position start, List<Velocity> path) {
        Position position = new Position(start.x, start.y);
        for (Velocity velocity : path) {
            Position previousPosition = new Position(position.x, position.y);
            position = simulateShot(position, velocity);
            if (previousPosition.distanceTo(position) < 0.5) {
                return null;
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
                System.out.println("Obstacle found for velocity: " + velocity);
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

    private List<double[]> convertPathToOutputFormat(List<Velocity> path) {
        List<double[]> outputPath = new ArrayList<>();
        for (Velocity v : path) {
            outputPath.add(new double[]{v.vx, v.vy});
        }
        return outputPath;
    }

    public static void main(String[] args) {
        // Example usage:
        Position startPosition = new Position(0, 0);
        double targetX = 100;
        double targetY = 100;

        VariableLengthChromosomeGeneticAlgorithm vlga = new VariableLengthChromosomeGeneticAlgorithm(startPosition, targetX, targetY);
        List<double[]> optimalPath = vlga.findOptimalPath();

        if (optimalPath.isEmpty()) {
            System.out.println("No valid path found.");
        } else {
            System.out.println("Optimal Path: " + optimalPath);
        }
    }
}
