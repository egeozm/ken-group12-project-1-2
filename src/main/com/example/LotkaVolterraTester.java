package src.main;

public class LotkaVolterraTester {
    private double x0; // Initial prey population
    private double y0; // Initial predator population
    private double alpha; // Prey growth rate
    private double beta; // Predation rate
    private double gamma; // Predator death rate
    private double delta; // Reproduction rate of predators

    public LotkaVolterraTester(double initialPreyPop, double initialPredatorPop, double alpha, double beta, double gamma, double delta) {
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

        for (int i = 0; i < steps; i++) {
            double preyChange = 0;
            double predatorChange = 0;

            preyChange = ((alpha * x0) - (beta * x0 * y0)) * timePassed;
            predatorChange = ((delta * x0 * y0) - (gamma * y0)) * timePassed;
            x0 += preyChange;
            y0 += predatorChange;

            if (x0 < 0) {
                System.out.println("Simulation stopped because prey population became negative.");
                return;
            } else if (y0 < 0) {
                System.out.println("Simulation stopped because predator population became negative.");
                return;
            }

            System.out.println("Prey Population = " + x0 + ", Predator Population = " + y0);
        }
        double finalPreyChange = x0 - initialPreyPop;
        double finalPredatorChange = y0 - initialPredatorPop;
        System.out.println("-------");
        System.out.println("Overall change in prey population: " + finalPreyChange);
        System.out.println("Overall change in predator population: " + finalPredatorChange);
    }
    

    public static void main(String[] args) {
        double initialPreyPop = 100;
        double initialPredatorPop = 20;
        double preyGrowthRate = 0.1;
        double predationRate = 0.02;
        double predatorDeathRate = 0.2;
        double reproductionRate = 0.005;
        int steps = 100;
        double timeInterval = 0.01;

        LotkaVolterraTester tester = new LotkaVolterraTester(initialPreyPop, initialPredatorPop, preyGrowthRate, predationRate, predatorDeathRate, reproductionRate);
        tester.simulate(steps, timeInterval, initialPreyPop, initialPredatorPop);
    }
}
