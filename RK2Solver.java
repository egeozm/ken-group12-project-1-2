public class RK2Solver {
    //change depending on the ODE
    static double calculateDerivative(double x, double y) {
        // ODE: dx/dy = 2xy
        return 2 * x * y;
    }
 
    static double solveODE(double initialX, double initialY, double finalX, double stepSize) {
        int steps = (int) Math.ceil((finalX - initialX) / stepSize);
        double currentValue = initialY;

        for (int i = 0; i < steps; i++) {
            double k1 = stepSize * calculateDerivative(initialX, currentValue);
            double k2 = stepSize * calculateDerivative(initialX + 0.5 * stepSize, currentValue + 0.5 * k1);
            currentValue += (k1 + k2) / 2.0;
            initialX += stepSize;
        }
        return currentValue;
    }

    public static void main(String[] args) {
        // test the solver
        double initialX = 0;
        double initialY = 1; 
        double finalX = 2; 
        double stepSize = 0.2;

        System.out.println("Value of y at x = " + finalX + ": " + solveODE(initialX, initialY, finalX, stepSize));
    }
}
