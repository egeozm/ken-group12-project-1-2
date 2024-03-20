public class LotkaVolterraTester {
    
    // this is all placeholder stuff for now

    private double alpha, beta, gamma, delta;

    public LotkaVolterraTester(double alpha, double beta, double gamma, double delta) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }

    public double[] evaluate(double[] state) {
        double x = state[0];
        double y = state[1];
        double[] derivatives = new double[2];
        derivatives[0] = alpha * x - beta * x * y;
        derivatives[1] = delta * x * y - gamma * y;
        return derivatives;
    }

    public static void main(String[] args) {
        double[] initialConditions = {10.0, 5.0}; // start prey and predator populations

        double alpha = 0.0; // (placeholder value) growth rate of the prey population in the absence of predation
        double beta = 0.0; // (placeholder value) the rate at which predators consume prey
        double gamma = 0.0; // (placeholder value) death rate of the predator population in the absence of prey
        double delta = 0.0; // (placeholder value)reproduction rate of predators per prey consumed

        double stepSize = 0.0;
        double integrationTime = 0.0;

        LotkaVolterraTester lotkaVolterraSystem = new LotkaVolterraTester(alpha, beta, gamma, delta);

        // Test euler solver

        // Test higher order solver
    }
}
