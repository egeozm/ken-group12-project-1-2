public class RK2Solver {
    //derivative function for the given ODE changes depending on the ODE
    static double calculateDerivative(double x, double y) {
        return x + y - 2;
    }
 
    static double solveODE(double initialX, double initialY, double finalX, double stepSize) {
        int steps = (int) Math.ceil((finalX - initialX) / stepSize);
        double currentValue = initialY;

        for (int i = 0; i < steps; i++) {
            double k1 = stepSize * calculateDerivative(initialX, currentValue);
            double k2 = stepSize * calculateDerivative(initialX + 0.5 * stepSize, currentValue + 0.5 * k1);
            currentValue += (k1 + k2) / 6.0;
            initialX += stepSize;
        }
        return currentValue;
    }

    public static void main(String[] args) {
        // test the solver
        double initialX = 0, initialY = 1, finalX = 2, stepSize = 0.2;

        System.out.println("Value of y at x = " + finalX + ": " + solveODE(initialX, initialY, finalX, stepSize));
    }
}
