package com.bots.advanced.graph.genetic;

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

public class GeneticAlgorithm {
    private static final int POPULATION_SIZE = 30;
    private static final int NUM_GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.01;
    private static final double MAX_SPEED = 10.0;
    private static final int MAX_DEPTH = 2;
    private static final long TIME_LIMIT_NS = 45_000_000_000L; // 5 seconds in nanoseconds

    private GolfBall golfBall;
    private double targetX;
    private double targetY;
    private double terrainWidth;
    private double terrainHeight;
    private Position holePosition;
    private Map<String, Boolean> obstacleCache;
    private Position startPos;
    private long startTime;

    public GeneticAlgorithm(GolfBall golfBall, double targetX, double targetY) {
        this.golfBall = golfBall;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
        this.holePosition = new Position(targetX, targetY);
        this.obstacleCache = new HashMap<>();
    }
    public GeneticAlgorithm(Position startPos, double targetX, double targetY) {
        this.startPos = startPos;
        this.targetX = targetX;
        this.targetY = targetY;
        this.terrainWidth = Terrain.getInstance().getWidth();
        this.terrainHeight = Terrain.getInstance().getHeight();
        this.holePosition = new Position(targetX, targetY);
        this.obstacleCache = new HashMap<>();
    }

    public List<double[]> findOptimalPath() {
        Position start;
        if(startPos == null)
            start = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        else
            start = startPos;
        List<List<Velocity>> population = initializePopulation();
        startTime = System.nanoTime();

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            if (System.nanoTime() - startTime > TIME_LIMIT_NS) {
                System.out.println("Time limit reached, stopping generations.");
                break;
            }
            List<Double> fitnessScores = evaluateFitness(population, start);
            printPopulation(population, fitnessScores);
            List<List<Velocity>> selectedIndividuals = selectIndividuals(population, fitnessScores);
            List<List<Velocity>> offspring = crossover(selectedIndividuals);
            mutate(offspring);
            population = offspring;
        }
        List<Velocity> bestIndividual = findBestIndividual(population, start);
        return convertPathToOutputFormat(bestIndividual);
    }

    // New method to get all valid paths
    public List<List<double[]>> getAllValidPaths() {
        List<List<double[]>> validPaths = new ArrayList<>();
        Position start;
        if(startPos == null)
            start = new Position(golfBall.getPosition().x, golfBall.getPosition().z);
        else
            start = startPos;
        List<List<Velocity>> population = initializePopulation();

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            List<Double> fitnessScores = evaluateFitness(population, start);
            List<List<Velocity>> selectedIndividuals = selectIndividuals(population, fitnessScores);
            List<List<Velocity>> offspring = crossover(selectedIndividuals);
            mutate(offspring);
            population = offspring;

            for (int i = 0; i < population.size(); i++) {
                List<Velocity> individual = population.get(i);
                double fitness = fitnessScores.get(i);
                if (fitness > 0.00001) {
                    validPaths.add(convertPathToOutputFormat(individual));
                }
            }
        }

        return validPaths;
    }

    public List<List<Velocity>> initializePopulation() {
        List<List<Velocity>> population = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Velocity> individual = new ArrayList<>();
            for (int j = 0; j < MAX_DEPTH; j++) {
                double vx = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
                double vy = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
                individual.add(new Velocity(vx, vy));
            }
            population.add(individual);
        }
        return population;
    }

    public List<Velocity> initializeIndividual() {
        Random random = new Random();
        List<Velocity> individual = new ArrayList<>();
        for (int j = 0; j < MAX_DEPTH; j++) {
            double vx = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
            double vy = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
            individual.add(new Velocity(vx, vy));
        }
        return individual;
    }

    public List<Double> evaluateFitness(List<List<Velocity>> population, Position start) {
        List<Double> fitnessScores = new ArrayList<>();
        for (List<Velocity> individual : population) {
            Position position = simulatePath(start, individual);
            if (position == null) {
                double distanceToHole = Double.MAX_VALUE;
                double fitness = 1.0 / (distanceToHole + 1);
                fitnessScores.add(fitness);
                continue;
            }
            double distanceToHole = position.distanceTo(holePosition);
            double fitness = 1.0 / (distanceToHole + 1);
            fitnessScores.add(fitness);
        }
        return fitnessScores;
    }

    public Position simulatePath(Position start, List<Velocity> path) {
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

    public Position simulateShot(Position start, Velocity velocity) {
        Ball tempBall = new Ball(golfBall.getTerrain());
        tempBall.setState(start.x, start.y, 0, 0);
        double[][] trajectory = tempBall.getTrajectoryArray(0.1, start.x, start.y, velocity.vx, velocity.vy, 30);

        if (trajectory.length == 0) {
            return new Position(start.x, start.y);
        }

        boolean obstacleFound = false;

        for (int i = 0; i < trajectory.length - 1; i++) {
            double x1 = trajectory[i][0];
            double y1 = trajectory[i][1];
            double x2 = trajectory[i + 1][0];
            double y2 = trajectory[i + 1][1];

            if (checkObstaclesBetweenPoints(x1, y1, x2, y2) || !isWithinBounds(new Position(x2, y2))) {
                obstacleFound = true;
                break;
            }
        }

        if (obstacleFound) {
            return new Position(start.x, start.y);
        }

        double[] finalState = trajectory[trajectory.length - 1];
        return new Position(finalState[0], finalState[1]);
    }

    public boolean checkObstaclesBetweenPoints(double x1, double y1, double x2, double y2) {
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

    public boolean isWithinBounds(Position position) {
        boolean withinBounds = position.x >= -terrainWidth && position.x <= terrainWidth && position.y >= -terrainHeight && position.y <= terrainHeight;
        return withinBounds;
    }

    public List<List<Velocity>> selectIndividuals(List<List<Velocity>> population, List<Double> fitnessScores) {
        List<List<Velocity>> selectedIndividuals = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int index = selectIndividual(fitnessScores, random);
            selectedIndividuals.add(population.get(index));
        }
        return selectedIndividuals;
    }

    public int selectIndividual(List<Double> fitnessScores, Random random) {
        double totalFitness = fitnessScores.stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0.0;
        for (int i = 0; i < fitnessScores.size(); i++) {
            cumulativeFitness += fitnessScores.get(i);
            if (cumulativeFitness >= randomValue) {
                return i;
            }
        }
        return fitnessScores.size() - 1;
    }

    public void printPopulation(List<List<Velocity>> population, List<Double> fitnessScores) {
        for (int i = 0; i < population.size(); i++) {
            List<Velocity> individual = population.get(i);
            double fitness = fitnessScores.get(i);
            String status = fitness > 0.001 ? "Good" : "Bad";
            System.out.print("Individual " + (i + 1) + " (" + status + "): ");
            for (Velocity velocity : individual) {
                System.out.print(velocity + " ");
            }
            System.out.println();
        }
    }

    public List<List<Velocity>> crossover(List<List<Velocity>> selectedIndividuals) {
        List<List<Velocity>> offspring = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < POPULATION_SIZE; i += 2) {
            List<Velocity> parent1 = selectedIndividuals.get(i);
            List<Velocity> parent2 = selectedIndividuals.get(i + 1);
            List<Velocity> child1 = new ArrayList<>();
            List<Velocity> child2 = new ArrayList<>();
            int crossoverPoint = random.nextInt(MAX_DEPTH);
            for (int j = 0; j < MAX_DEPTH; j++) {
                if (j < crossoverPoint) {
                    child1.add(parent1.get(j));
                    child2.add(parent2.get(j));
                } else {
                    child1.add(parent2.get(j));
                    child2.add(parent1.get(j));
                }
            }
            offspring.add(child1);
            offspring.add(child2);
        }
        return offspring;
    }

    public void mutate(List<List<Velocity>> population) {
        Random random = new Random();
        for (List<Velocity> individual : population) {
            for (int i = 0; i < MAX_DEPTH; i++) {
                if (random.nextDouble() < MUTATION_RATE) {
                    double vx = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
                    double vy = -MAX_SPEED + random.nextDouble() * 2 * MAX_SPEED;
                    individual.set(i, new Velocity(vx, vy));
                }
            }
        }
    }

    public List<Velocity> findBestIndividual(List<List<Velocity>> population, Position start) {
        List<Velocity> bestIndividual = null;
        double bestFitness = Double.MIN_VALUE;
        for (List<Velocity> individual : population) {
            Position position = simulatePath(start, individual);
            double distanceToHole = 0;
            if (position == null) {
                distanceToHole = Double.MAX_VALUE;
                double fitness = 1.0 / (distanceToHole + 1);
                continue;
            } else
                distanceToHole = position.distanceTo(holePosition);
            double fitness = 1.0 / (distanceToHole + 1);
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = individual;
            }
        }
        return bestIndividual;
    }

    public List<double[]> convertPathToOutputFormat(List<Velocity> path) {
        List<double[]> outputPath = new ArrayList<>();
        for (Velocity v : path) {
            outputPath.add(new double[]{v.vx, v.vy});
        }
        return outputPath;
    }
}
