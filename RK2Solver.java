public class RK2Solver {
    // change depending on the ODE
    // if ODE not in list then add it and it will be available to use
    static double calculateDerivative(double x, double y, int choice) {
        // ODE: dx/dy = 2xy
        // ODE: dx/dy = x^2 + y^2
        // ODE: dx/dy = -y
        // ODE: dx/dy = -x * y
        // ODE: dx/dy = Math.sin(x) * Math.cos(y)
        if (choice == 1){
            return 2 * x * y;
        } else if (choice == 2){
            return Math.pow(x, 2) + Math.pow(y, 2);
        } else if (choice == 3){
            return -y;
        } else if (choice == 4){
            return -x * y;
        } else if (choice == 5){
            return Math.sin(x) * Math.cos(y);
        } else if (choice == 6){
            double alpha = 0.1;
            double beta = 0.02;
            double gamma = 0.2;
            double delta = 0.005;
            return (alpha * x) - (beta * x * y) + (delta * x * y) - (gamma * y);
        } else {
            return 0;
        }
    }
 
    static double solveODE(double initialX, double initialY, double finalX, double stepSize, int choice) {
        int steps = (int) Math.ceil((finalX - initialX) / stepSize);
        double currentValue = initialY;

        for (int i = 0; i < steps; i++) {
            double k1 = stepSize * calculateDerivative(initialX, currentValue, choice);
            double k2 = stepSize * calculateDerivative(initialX + 0.5 * stepSize, currentValue + 0.5 * k1, choice);
            currentValue += (k1 + k2) / 2.0;
            initialX += stepSize;
        }
        return currentValue;
    }

    public static void main(String[] args) {
        // test the solver
        double initialX = 100;
        double initialY = 20; 
        double finalX = 150; 
        double stepSize = 0.2;
        int ODEChoice = 6;

        System.out.println("Value of y at x = " + finalX + ": " + solveODE(initialX, initialY, finalX, stepSize, ODEChoice));
    }
}
