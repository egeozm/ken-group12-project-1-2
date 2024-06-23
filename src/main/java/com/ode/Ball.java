package com.ode;

import com.gui.terrain.Terrain;
import com.ode.UpdatedBall;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Represents a ball that can move over a terrain, influenced by physical forces such as friction and slopes.
 */
public class Ball {
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
     * Helper method to create a terrain and compute the ball trajectory.
     *
     * @param heightFunction The function defining the terrain height.
     * @param coefficients   The physical coefficients for the terrain.
     * @return The trajectory of the ball.
     */
    private static double[][] getDoubles(BiFunction<Double, Double, Double> heightFunction, PhysicsCoefficients coefficients) {
        Terrain terrain = Terrain.getInstance(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getKineticFrictionSand());
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
     * Updates the ball state using the Runge-Kutta method for numerical integration with the more complex equation.
     *
     * @param timeStep The time step for the integration.
     */
    public void updateBallStateRungeKuttaCoolerEquation(double timeStep) {
        DifferentialEquation system = (t, state) -> {
            double x = state[0];
            double y = state[1];
            double vx = state[2];
            double vy = state[3];
            double[] slope = terrain.getSlope(x, y);
            double kfriction = terrain.getKineticFriction(x, y);
            double speed = Math.sqrt(vx * vx + vy * vy);
            double epsilon = 1e-6;
            if (speed < epsilon) {
                speed = epsilon;
            }

            UpdatedBall updatedBall = new UpdatedBall(terrain);
            double nForceX =  ((updatedBall.golfBallMass*PhysicsCoefficients.GRAVITATIONAL_CONSTANT)/(1+slope[0]*slope[0]+slope[1]*slope[1]))*(-slope[0]);
            double nForceY =  ((updatedBall.golfBallMass*PhysicsCoefficients.GRAVITATIONAL_CONSTANT)/(1+slope[0]*slope[0]+slope[1]*slope[1]))*(-slope[1]);
            double friction = (-kfriction*updatedBall.golfBallMass*PhysicsCoefficients.GRAVITATIONAL_CONSTANT)/((Math.sqrt(1+slope[0]*slope[0]+slope[0]*slope[1]))*(Math.sqrt(vx*vx+vy*vy+Math.pow((slope[0]*vx+slope[1]*vy),2))));
            double accelX = (nForceX - friction)/updatedBall.golfBallMass;
            double accelY = (nForceY - friction)/updatedBall.golfBallMass;

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


    public double[] getPosition() {
        return new double[1];
    }


}
