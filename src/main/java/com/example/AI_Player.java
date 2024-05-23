package com.example;

import com.gui.Terrain;
import java.util.function.BiFunction;

public class AI_Player {

    private static final double MAX_SPEED = 5.0;
    private static final double INITIAL_DELTA_V = 0.5;
    private static final double MIN_DELTA_V = 0.001;
    private static final double IMPROVEMENT_THRESHOLD = 0.0001;

    private Ball ball;
    private double targetX;
    private double targetY;
    private PhysicsCoefficients coefficients;
    private double initialBallX;
    private double initialBallY;

    public AI_Player(Ball ball, double targetX, double targetY, PhysicsCoefficients coefficients) {
        this.ball = ball;
        this.targetX = targetX;
        this.targetY = targetY;
        this.coefficients = coefficients;
        this.initialBallX = ball.getX();
        this.initialBallY = ball.getY();
    }

    public void findHoleInOne() {
        double bestVx = 1.0;
        double bestVy = 1.0;
        double bestDistance = Double.MAX_VALUE;
        double deltaV = INITIAL_DELTA_V;

        for (int i = 0; i < 1000; i++) {
            double[] result = simulateShot(bestVx, bestVy);
            double distanceToTargetX = targetX - result[0];
            double distanceToTargetY = targetY - result[1];
            double distanceToTarget = distance(result[0], result[1], targetX, targetY);

            System.out.println("Iteration " + i + ": velocities vx = " + bestVx + ", vy = " + bestVy);
            System.out.println("End position: (" + result[0] + ", " + result[1] + ")");
            System.out.println("Distance to hole x: " + distanceToTargetX + ", y: " + distanceToTargetY);

            if (distanceToTarget < bestDistance) {
                bestDistance = distanceToTarget;

                if (bestDistance <= coefficients.getTargetRadius()) {
                    System.out.println("Hole in one");
                    break;
                }

                deltaV = INITIAL_DELTA_V;
            } else {
                deltaV = Math.min(MAX_SPEED, deltaV * 1.1);
            }

            double[] newVxVy = adjustVelocities(bestVx, bestVy, deltaV, distanceToTarget);

            if (newVxVy[2] < bestDistance) {
                bestVx = newVxVy[0];
                bestVy = newVxVy[1];
            } else {
                deltaV = Math.max(MIN_DELTA_V, deltaV * 0.5);
            }

            if (deltaV < IMPROVEMENT_THRESHOLD) {
                break;
            }
        }

        System.out.println("Best initial velocity: vx = " + bestVx + ", vy = " + bestVy);
    }

    private double[] adjustVelocities(double vx, double vy, double deltaV, double currentBestDistance) {
        double bestNewDistance = currentBestDistance;

        double[] bestVelocities = {vx, vy, currentBestDistance};

        double[][] tests = {
                {vx + deltaV, vy},
                {vx - deltaV, vy},
                {vx, vy + deltaV},
                {vx, vy - deltaV},
                {vx + deltaV, vy + deltaV},
                {vx - deltaV, vy - deltaV},
                {vx + deltaV, vy - deltaV},
                {vx - deltaV, vy + deltaV}
        };

        for (double[] test : tests) {
            double[] result = simulateShot(test[0], test[1]);
            double distanceToTarget = distance(result[0], result[1], targetX, targetY);

            if (distanceToTarget < bestNewDistance) {
                bestNewDistance = distanceToTarget;
                bestVelocities[0] = test[0];
                bestVelocities[1] = test[1];
                bestVelocities[2] = bestNewDistance;
            }
        }

        return bestVelocities;
    }

    private double[] simulateShot(double vx, double vy) {
        ball.setState(initialBallX, initialBallY, 0, 0);
        ball.setVelocity(vx, vy);
        double[][] trajectory = ball.getTrajectoryArray(0.1, ball.getX(), ball.getY(), vx, vy, 1000);

        if (trajectory.length == 0) {
            return new double[]{ball.getX(), ball.getY()};
        }

        double[] finalState = trajectory[trajectory.length - 1];
        return new double[]{finalState[0], finalState[1]};
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
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
