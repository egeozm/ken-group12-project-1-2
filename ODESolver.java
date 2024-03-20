public class ODESolver {
    public static double EulerSolver(double x0, double y0, double h, double x){
        while (x0 < x) {
            y0 += h * (x0*y0);
            x0 += h;
        }
        return y0;
    }
}
