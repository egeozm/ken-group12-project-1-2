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

        /*
        // Output the results
        System.out.println("Euler Method Results:");
        for (int i = 0; i <= n; i++) {
            System.out.println("y(" + (t0 + i * (tf - t0) / n) + ") = " + eulerResults[i]);
        }
         */

        System.out.println("RK4 Method Results:");
        for (int i = 0; i <= n; i++) {
            System.out.println("y(" + (t0 + i * (tf - t0) / n) + ") = " + rk4Results[i]);
        }
    }
}
