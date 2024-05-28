package com.example;

import java.util.Arrays;

/**
 * ODESolversAccuracyTest class to compare the accuracy of Euler and RK4 methods
 * for solving different differential equations.
 */
public class ODESolversAccuracyTest {

    /**
     * Main method to execute the ODE solvers accuracy test.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Example 1: Exponential Decay Equation
        System.out.println("Exponential Decay Equation:");
        testEquation((t, state) -> {
            double k = 1.0;
            double[] derivatives = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                derivatives[i] = -k * state[i];
            }
            return derivatives;
        });

        // Example 2: Harmonic Oscillator Equation
        System.out.println("Harmonic Oscillator Equation:");
        testEquation((t, state) -> {
            double omega = 1.0;
            double[] derivatives = new double[2];
            derivatives[0] = state[1];               // dy/dt = v
            derivatives[1] = -omega * omega * state[0]; // dv/dt = -omega^2 * y
            return derivatives;
        });

        // Example 3: 2*x^2 + 3*x Equation
        System.out.println("2*x^2 + 3*x Equation:");
        testEquation((t, state) -> {
            double[] derivatives = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                derivatives[i] = 2 * t * t + 3 * t;
            }
            return derivatives;
        });
    }

    /**
     * Test the given differential equation using Euler and RK4 methods.
     *
     * @param equation The differential equation to test.
     */
    private static void testEquation(DifferentialEquation equation) {
        // Parameters for the ODE solving
        double t0 = 0.0;
        double[] y0 = {1.0, 0.0}; // Initial conditions for Harmonic Oscillator (y0, v0)
        double tEnd = 5.0;

        // Different step sizes to test
        double[] stepSizes = {0.1, 0.05, 0.01, 0.005, 0.001};

        // Iterate over each step size
        for (double stepSize : stepSizes) {
            System.out.println("Step Size: " + stepSize);
            System.out.println("------------------------");

            // Solve the ODE using Euler's method
            double[][] eulerResults = solveWithEuler(equation, t0, y0, tEnd, stepSize);
            double[] eulerErrors = calculateErrors(eulerResults, t0, tEnd, stepSize, y0);

            // Solve the ODE using RK4 method
            double[][] rk4Results = solveWithRK4(equation, t0, y0, tEnd, stepSize);
            double[] rk4Errors = calculateErrors(rk4Results, t0, tEnd, stepSize, y0);

            // Output the maximum errors for both methods
            System.out.printf("Euler Method Max Error:  %.16f%n", Arrays.stream(eulerErrors).max().orElse(0.0));
            System.out.printf("RK4 Method Max Error:    %.16f%n", Arrays.stream(rk4Errors).max().orElse(0.0));
            System.out.println();
        }
    }

    /**
     * Solves the ODE using Euler's method.
     *
     * @param eq   The differential equation.
     * @param t0   The initial time.
     * @param y0   The initial state.
     * @param tEnd The end time.
     * @param dt   The time step.
     * @return The numerical solution at each time step.
     */
    private static double[][] solveWithEuler(DifferentialEquation eq, double t0, double[] y0, double tEnd, double dt) {
        int n = (int) ((tEnd - t0) / dt);
        double[][] results = new double[n + 1][y0.length];
        results[0] = y0.clone();

        for (int i = 1; i <= n; i++) {
            results[i] = DifferentialEquation.EulerMethod.solve(eq, results[i - 1], dt);
        }

        return results;
    }

    /**
     * Solves the ODE using the RK4 method.
     *
     * @param eq   The differential equation.
     * @param t0   The initial time.
     * @param y0   The initial state.
     * @param tEnd The end time.
     * @param dt   The time step.
     * @return The numerical solution at each time step.
     */
    private static double[][] solveWithRK4(DifferentialEquation eq, double t0, double[] y0, double tEnd, double dt) {
        int n = (int) ((tEnd - t0) / dt);
        double[][] results = new double[n + 1][y0.length];
        results[0] = y0.clone();

        for (int i = 1; i <= n; i++) {
            results[i] = DifferentialEquation.RK4Method.solve(eq, results[i - 1], dt);
        }

        return results;
    }

    /**
     * Calculates the errors between the numerical and analytical solutions.
     *
     * @param numericalResults The numerical results.
     * @param t0               The initial time.
     * @param tEnd             The end time.
     * @param stepSize         The time step.
     * @param y0               The initial state.
     * @return The errors at each time step.
     */
    private static double[] calculateErrors(double[][] numericalResults, double t0, double tEnd, double stepSize, double[] y0) {
        int steps = (int) ((tEnd - t0) / stepSize) + 1;
        double[] errors = new double[steps];

        for (int i = 0; i < steps; i++) {
            double t = t0 + i * stepSize;
            double analytical = analyticalSolution(t, y0[0]);
            errors[i] = Math.abs(analytical - numericalResults[i][0]);
        }

        return errors;
    }

    /**
     * Analytical solution of the exponential decay differential equation.
     *
     * @param t  The time.
     * @param y0 The initial state.
     * @return The analytical solution at time t.
     */
    private static double analyticalSolution(double t, double y0) {
        double k = 1.0;
        return y0 * Math.exp(-k * t);
    }
}