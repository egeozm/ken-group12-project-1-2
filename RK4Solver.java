public class RK4Solver {

    private static final double T_0 = 0.0;    // Initial time
    private static final double Y_0 = 0.5;    // Initial value of y
    private static final double T_FINAL = 2.0;// Final time
    private static final double DT = 0.2;     // Step size

    // The derivative function f(t, y)
    private static double f(double t, double y) {
        return y - Math.pow(t, 2) + 1;
    }

    // The RK4 step computation
    private static double rk4Step(double t, double y, double dt) {
        double k1 = dt * f(t, y);
        double k2 = dt * f(t + dt / 2.0, y + k1 / 2.0);
        double k3 = dt * f(t + dt / 2.0, y + k2 / 2.0);
        double k4 = dt * f(t + dt, y + k3);
        return y + (k1 + 2.0 * (k2 + k3) + k4) / 6.0;
    }

    public static void test() {
        double t = T_0;
        double y = Y_0;

        // Print the initial condition
        System.out.printf("y(%1.1f) = %f\n", t, y);

        // Perform the RK4 steps and print the results
        while (t < T_FINAL) {
            y = rk4Step(t, y, DT);
            t += DT;
            System.out.printf("y(%1.1f) = %f\n", t, y);
        }
    }

    public static void main(String[] args) {
        test();
    }
}
