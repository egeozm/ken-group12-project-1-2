public class FitzHughNagumoModel {
    private double v;
    private double w;
    private double I;
    private double a = 0.7;
    private double b = 0.8;
    private double epsilon = 0.08;
    private double dvdt;
    private double dwdt;

    public FitzHughNagumoModel(double vInitial, double wInitial, double currentI) {
        this.v = vInitial;
        this.w = wInitial;
        this.I = currentI;
    }

    public void eulerStepFHN(double dt) {
        dvdt = v - Math.pow(v, 3) / 3 - w + I;
        dwdt = epsilon * (v + a - b * w);

        v += dvdt * dt;
        w += dwdt * dt;
    }

    public static void main(String[] args) {
        double vInitial = 0.1; // Initial value of v
        double wInitial = 0.1; // Initial value of w
        double currentI = 0.5; // Current value of I
        double timeStep = 0.01; // Time step for Euler integration

        FitzHughNagumoModel model = new FitzHughNagumoModel(vInitial, wInitial, currentI);

        
        int numSteps = 200; // Number of integration steps
        for (int i = 0; i < numSteps; i++) {
            model.eulerStepFHN(timeStep);
            System.out.println("Membrane potential of the neuron: " + model.v + ", Recovery variable: " + model.w);
        }
    }
}
