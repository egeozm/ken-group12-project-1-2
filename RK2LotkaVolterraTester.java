public class RK2LotkaVolterraTester {
    private double x0; // prey population
    private double y0; // predator population
    private double alpha; // prey population growth rate
    private double beta; // prey population death rate from predators
    private double gamma; // predator population death rate
    private double delta; // reproduction rate of predators

    public RK2LotkaVolterraTester(double initialPreyPop, double initialPredatorPop, double alpha, double beta, double gamma, double delta) {
        this.x0 = initialPreyPop;
        this.y0 = initialPredatorPop;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }

    public void simulate(int steps, double timePassed, double initialPreyPop, double initialPredatorPop) {

        if (timePassed <= 0) {
            System.out.println("Time interval must be positive.");
            return;
        }

        if (steps <= 0) {
            System.out.println("Steps must be at least one.");
            return;
        }

        double currentTime = 0;
        double[] currentState = {x0, y0};

        for (int i = 0; i < steps; i++) {
            double[] derivatives = calculateDerivatives(currentTime, currentState);
            currentState = integrateRK2(currentTime, currentState, derivatives, timePassed);
            currentTime += timePassed;

            // Ensure populations are not negative
            if (currentState[0] < 0) {
                System.out.println("Simulation stopped because prey population became negative.");
                return;
            } else if (currentState[1] < 0) {
                System.out.println("Simulation stopped because predator population became negative.");
                return;
            }

            System.out.println("Prey Population = " + currentState[0] + ", Predator Population = " + currentState[1]);
        }

        double finalPreyChange = currentState[0] - initialPreyPop;
        double finalPredatorChange = currentState[1] - initialPredatorPop;
        System.out.println("-------");
        System.out.println("Overall change in prey population: " + finalPreyChange);
        System.out.println("Overall change in predator population: " + finalPredatorChange);
    }

    private double[] calculateDerivatives(double time, double[] state) {
        double preyChange = ((alpha * state[0]) - (beta * state[0] * state[1]));
        double predatorChange = ((delta * state[0] * state[1]) - (gamma * state[1]));
        return new double[]{preyChange, predatorChange};
    }

    private double[] integrateRK2(double currentTime, double[] currentState, double[] derivatives, double stepSize) {
        double[] nextState = new double[currentState.length];

        for (int i = 0; i < currentState.length; i++) {
            double k1 = stepSize * derivatives[i];
            double k2 = stepSize * calculateDerivatives(currentTime + 0.5 * stepSize, nextState)[i];
            nextState[i] = currentState[i] + 0.5 * (k1 + k2);
        }

        return nextState;
    }

    public static void main(String[] args) {
        double initialPreyPop = 100;
        double initialPredatorPop = 20;
        double preyGrowthRate = 0.1;
        double predationRate = 0.02;
        double predatorDeathRate = 0.2;
        double reproductionRate = 0.005;
        int steps = 300;
        double timeInterval = 1;

        RK2LotkaVolterraTester tester = new RK2LotkaVolterraTester(initialPreyPop, initialPredatorPop, preyGrowthRate, predationRate, predatorDeathRate, reproductionRate);
        tester.simulate(steps, timeInterval, initialPreyPop, initialPredatorPop);
    }
}
