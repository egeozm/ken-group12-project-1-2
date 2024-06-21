package com.ode;

/**
 * This class demonstrates solving a simple differential equation using Euler and Runge-Kutta fourth order methods.
 */
public class DifferentialSolver {
    /**
     * The main method to run the differential equation solver.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Define a differential equation dy/dt = y - t^2 + 1
        DifferentialEquation eq = (t, state) -> {
            double[] derivatives = new double[1];
            derivatives[0] = state[0] - t * t + 1;
            return derivatives;
        };

        // Define the initial conditions and the time span
        double[] y0 = {0.5}; // Initial value of y
        double t0 = 0.0;     // Initial time
        double tf = 2.0;     // Final time
        int n = 10;          // Number of steps

        // Define the time step
        double dt = (tf - t0) / n;

        // Arrays to store the results
        double[] rk4Results = new double[n + 1];
        double[] eulerResults = new double[n + 1];
        rk4Results[0] = y0[0];
        eulerResults[0] = y0[0];

        // Solve using both methods
        for (int i = 1; i <= n; i++) {
            rk4Results[i] = DifferentialEquation.RK4Method.solve(eq, new double[]{rk4Results[i - 1]}, dt)[0];
            eulerResults[i] = DifferentialEquation.EulerMethod.solve(eq, new double[]{eulerResults[i - 1]}, dt)[0];
        }

        // Get the final results
        double eulerFinalResult = eulerResults[n];
        double rk4FinalResult = rk4Results[n];

        // Print the final results
        System.out.println("Final Results:");
        System.out.printf("Euler: %.5f%n", eulerFinalResult);
        System.out.printf("RK4: %.5f%n", rk4FinalResult);
    }
}
