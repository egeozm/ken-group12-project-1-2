public class LotkaVolterraTester{
    private double preyPop;
    private double predatorPop;
    private double alpha;
    private double beta;
    private double gamma;
    private double delta;

    public LotkaVolterraTester(double initialPreyPop, double initialPredatorPop, double alpha, double beta, double gamma, double delta) {
        this.preyPop = initialPreyPop;
        this.predatorPop = initialPredatorPop;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }

    public void simulate(int steps, double timePassed, double initialPredatorPop, double initialPreyPop) {

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

            preyChange = ((alpha * preyPop) - (beta * preyPop * predatorPop)) * timePassed;
            predatorChange = ((delta * preyPop * predatorPop) - (gamma * predatorPop)) * timePassed;
            preyPop += preyChange;
            predatorPop += predatorChange;

            if (preyPop < 0) {
                System.out.println("Simulation stopped because prey population became negative.");
                return;
            } else if (predatorPop < 0) {
                System.out.println("Simulation stopped because predator population became negative.");
                return;
            }

            System.out.println("Prey Population = " + preyPop + ", Predator Population = " + predatorPop);
        }
        double finalPreyChange = preyPop - initialPreyPop;
        double finalPredatorChange = predatorPop - initialPredatorPop;
        System.out.println("-------");
        System.out.println("Overall change in prey population: " + finalPreyChange);
        System.out.println("Overall change in predator population: " + finalPredatorChange);
    }
    

    public static void main(String[] args) {
        double initialPreyPop = 100;
        double initialPredatorPop = 20;
        double alpha = 0.1; // prey growth rate
        double beta = 0.02; // predation rate
        double gamma = 0.2; // predator death rate
        double delta = 0.005; // reproduction rate of predators
        int steps = 100; // number of times the Lotka Volterra equations are used to get the new populations
        double timeInterval = 0.01; // unit of time for the population growth/decrease

        LotkaVolterraTester tester = new LotkaVolterraTester(initialPreyPop, initialPredatorPop, alpha, beta, gamma, delta);
        tester.simulate(steps, timeInterval, initialPredatorPop, initialPreyPop);
    }
}

