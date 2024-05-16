package com.example;

public class DifferentialSolver {
    public static void main(String[] args) {
        DifferentialEquation eq = new DifferentialEquation() {
            @Override
            public double[] computeDerivatives(double t, double[] state) {
                double[] derivatives = new double[1];
                derivatives[0] = state[0] - t * t + 1;
                return derivatives;
            }
        };

        // Define the initial conditions and the time span
        double[] y0 = {0.5}; // Initial value of y
        double t0 = 0.0;
        double tf = 2.0;
        int n = 10; // Number of steps

        // Define the time step
        double dt = (tf - t0) / n;

        // Solve using both methods
        double[] rk4Results = new double[n + 1];
        double[] eulerResults = new double[n + 1];
        rk4Results[0] = y0[0];
        eulerResults[0] = y0[0];

        for (int i = 1; i <= n; i++) {
            rk4Results[i] = DifferentialEquation.RK4Method.solve(eq, new double[]{rk4Results[i - 1]}, dt)[0];
            eulerResults[i] = DifferentialEquation.EulerMethod.solve(eq, new double[]{eulerResults[i - 1]}, dt)[0];
        }

        double eulerFinalResult = eulerResults[n];
        double rk4FinalResult = rk4Results[n];

        System.out.println("Final Results:");
        System.out.printf("Euler: " + eulerFinalResult);
        System.out.printf("RK4: " + rk4FinalResult);
    }
}

