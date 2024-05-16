package com.example;

public class DifferentialSolver {
    public static void main(String[] args) {
        DifferentialEquation eq = (t, y) -> y - t * t + 1;

        // Define the initial conditions and the time span
        double y0 = 0.5;
        double t0 = 0.0;
        double tf = 2.0;
        int n = 10; // You can vary this to see different results

        // Solve using both methods
        double[] rk4Results = DifferentialEquation.RK4Method.solve(eq, y0, t0, tf, n);
        double[] eulerResults = DifferentialEquation.EulerMethod.solve(eq,y0,t0,tf,n);

        // Output the results and calculate relative errors
        System.out.println("Step\tt\tEuler\t\tRK4\t\tRelative Error (%)");
        for (int i = 0; i <= n; i++) {
            double t = t0 + i * (tf - t0) / n;
            double euler = eulerResults[i];
            double rk4 = rk4Results[i];
            double error = Math.abs(rk4 - euler) / Math.max(Math.abs(rk4), 1E-10) * 100;  // Avoid division by zero

            System.out.printf("%d\t%.2f\t%.6f\t%.6f\t%.2f%%\n", i, t, euler, rk4, error);
        }
    }
}
