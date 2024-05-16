import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.sound.midi.Soundbank;

public class Ball {
    private double initialX;
    private double initialY;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double [][] statesStored;
    private Terrain terrain;

    public Ball(Terrain terrain) {
        this.vx = 0;
        this.vy = 0;
        this.terrain = terrain;
    }

    public static void main(String[] args) {
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));
        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);
        Terrain terrain = new Terrain(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getStaticFrictionGrass(), coefficients.getKineticFrictionSand(), coefficients.getStaticFrictionSand());

        Ball ball = new Ball(terrain);

        double timeStep = 0.1; 
        double[][] trajectory = ball.getTrajectoryArray(timeStep, steps, 4.0, 4.0, 0.3, 0);
        for (double[] state : trajectory) {
            System.out.println("xPos: " + state[0] + ", yPos: " + state[1] + ", xVel: " + state[2] + ", yVel: " + state[3]);
        }
    }

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

        double[] state = { getX(), getY(), getVx(), getVy() };
        double[] newState = DifferentialEquation.RK4Method.solve(system, state, timeStep);

        setState(newState[0], newState[1], newState[2], newState[3]);

        if (terrain.isWater(getX(), getY())) {
            resetToInitialState();
        }

    }

    public double[][] getTrajectoryArray(double timeStep, double x, double y, double vx, double vy) {
        setState(x, y, vx, vy);

        double[][] trajectory = new double[steps][4]; 
        while (vx != 0 && vy !=0) {
            updateBallStateRungeKutta(timeStep);
            trajectory[i][0] = getX();
            trajectory[i][1] = getY();
            trajectory[i][2] = getVx();
            trajectory[i][3] = getVy();
        }
        return trajectory;
    }

    public void resetToInitialState(){
        x = initialX;
        y = initialY;
        vx = 0;
        vy = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setState(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
}


