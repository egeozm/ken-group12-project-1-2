package com.example;

import com.gui.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Represents a ball that can move over a terrain, influenced by physical forces such as friction and slopes.
 */
public class Ball {
    private double initialX;
    private double initialY;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final Terrain terrain;

    /**
     * Constructs a Ball instance with a given terrain.
     *
     * @param terrain The terrain over which the ball will move.
     */
    public Ball(Terrain terrain) {
        this.vx = 0;
        this.vy = 0;
        this.terrain = terrain;
    }

    /**
     * Main method for testing the Ball class.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // the height function is the function for the map generation
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));

        // set the coefficients depending on what the examiners give us
        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

        // this creates the terrain with the height function as the shape of the map
        double[][] trajectory = getDoubles(heightFunction, coefficients);
        for (double[] state : trajectory) {
            System.out.println("xPos: " + state[0] + ", yPos: " + state[1] + ", xVel: " + state[2] + ", yVel: " + state[3]);
        }
    }

    /**
     * Helper method to create a terrain and compute the ball trajectory.
     *
     * @param heightFunction The function defining the terrain height.
     * @param coefficients   The physical coefficients for the terrain.
     * @return The trajectory of the ball.
     */
    private static double[][] getDoubles(BiFunction<Double, Double, Double> heightFunction, PhysicsCoefficients coefficients) {
        Terrain terrain = new Terrain(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getKineticFrictionSand());
        Ball ball = new Ball(terrain);
        double timeStep = 0.1;
        // this is how you get the ball to move and also get the trajectory
        return ball.getTrajectoryArray(timeStep, 4.0, 4.0, 0.3, 0, 10);
    }

    /**
     * Updates the ball state using the Runge-Kutta method for numerical integration.
     *
     * @param timeStep The time step for the integration.
     */
    public void updateBallStateRungeKutta(double timeStep) {
        DifferentialEquation system = (t, state) -> {
            double x = state[0];
            double y = state[1];
            double vx = state[2];
            double vy = state[3];
            double[] slope = terrain.getSlope(x, y);
            double friction = terrain.getKineticFriction(x, y);
            double speed = Math.sqrt(vx * vx + vy * vy);
            double epsilon = 1e-6;
            if (speed < epsilon) {
                speed = epsilon;
            }
            double fx = -PhysicsCoefficients.GRAVITATIONAL_CONSTANT * slope[0] - friction * vx / speed;
            double fy = -PhysicsCoefficients.GRAVITATIONAL_CONSTANT * slope[1] - friction * vy / speed;
            return new double[]{vx, vy, fx, fy};
        };
        double[] state = {getX(), getY(), getVx(), getVy()};
        double[] newState = DifferentialEquation.RK4Method.solve(system, state, timeStep);
        setState(newState[0], newState[1], newState[2], newState[3]);
    }

    /**
     * Computes the trajectory of the ball over a given number of steps.
     *
     * @param timeStep The time step for each update.
     * @param x        The initial x position.
     * @param y        The initial y position.
     * @param vx       The initial x velocity.
     * @param vy       The initial y velocity.
     * @param maxSteps The maximum number of steps to compute.
     * @return A 2D array representing the trajectory of the ball.
     */
    public double[][] getTrajectoryArray(double timeStep, double x, double y, double vx, double vy, int maxSteps) {
        setState(x, y, vx, vy);
        initialX = x;
        initialY = y;
        int initialStep = 0;
        List<double[]> trajectory = new ArrayList<>();
        double epsilon = 1e-6;

        while ((Math.abs(vx) > epsilon || Math.abs(vy) > epsilon) && initialStep < maxSteps) {
            initialStep++;
            updateBallStateRungeKutta(timeStep);
            trajectory.add(new double[]{getX(), getY(), getVx(), getVy()});
            vx = getVx();
            vy = getVy();
        }

        return trajectory.toArray(new double[trajectory.size()][4]);
    }

    /**
     * Computes the trajectory of the ball until it comes to rest.
     *
     * @param timeStep The time step for each update.
     * @param x        The initial x position.
     * @param y        The initial y position.
     * @param vx       The initial x velocity.
     * @param vy       The initial y velocity.
     * @return A 2D array representing the trajectory of the ball.
     */
    public double[][] getTrajectoryArray(double timeStep, double x, double y, double vx, double vy) {
        setState(x, y, vx, vy);
        initialX = x;
        initialY = y;

        List<double[]> trajectory = new ArrayList<>();
        double epsilon = 1e-6;

        while (Math.abs(vx) > epsilon || Math.abs(vy) > epsilon) {
            updateBallStateRungeKutta(timeStep);
            trajectory.add(new double[]{getX(), getY(), getVx(), getVy()});
            vx = getVx();
            vy = getVy();
        }

        return trajectory.toArray(new double[trajectory.size()][4]);
    }

    /**
     * Resets the ball to its initial state.
     */
    public void resetToInitialState() {
        x = initialX;
        y = initialY;
        vx = 0;
        vy = 0;
    }

    /**
     * Gets the current x position of the ball.
     *
     * @return The current x position.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the current y position of the ball.
     *
     * @return The current y position.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the current x velocity of the ball.
     *
     * @return The current x velocity.
     */
    public double getVx() {
        return vx;
    }

    /**
     * Gets the current y velocity of the ball.
     *
     * @return The current y velocity.
     */
    public double getVy() {
        return vy;
    }

    /**
     * Sets the state of the ball.
     *
     * @param x  The x position.
     * @param y  The y position.
     * @param vx The x velocity.
     * @param vy The y velocity.
     */
    public void setState(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Gets the current state of the ball.
     *
     * @return An array containing the current x position, y position, x velocity, and y velocity.
     */
    public double[] getCurrentState() {
        return new double[]{this.x, this.y, this.vx, this.vy};
    }

    /**
     * Sets the velocity of the ball.
     *
     * @param vx The x velocity.
     * @param vy The y velocity.
     */
    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void updatePosition(double deltaTime) {
        // Not implemented
    }

    public void setCurrentTerrain(String currentTerrain) {
        // Not implemented
    }

    public double[] getPosition() {
        return new double[1];
    }

    public void setPosition(double previousX, double previousY, double previousZ) {
        // Not implemented
    }

    public double getPreviousZ() {
        return 0;
    }

    public double getPreviousY() {
        return 0;
    }

    public double getPreviousX() {
        return 0;
    }
}
