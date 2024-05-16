public class Ball {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private PhysicsCoefficients coefficients;
    private Terrain terrain;

    public Ball(double x, double y, PhysicsCoefficients coefficients, Terrain terrain) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.coefficients = coefficients;
        this.terrain = terrain;
    }

    public void updateBallStateRungeKutta(Ball ball, double dt) {
        DifferentialEquation system = (t, state) -> {
            double x = state[0];
            double y = state[1];
            double vx = state[2];
            double vy = state[3];

            double[] slope = terrain.getSlope(x, y);
            double friction = terrain.getKineticFriction(x, y);

            double speed = Math.sqrt(vx * vx + vy * vy);
            double epsilon = 1e-6;
            if (speed < epsilon) {
                speed = epsilon;
            }

            double fx = -coefficients.GRAVITATIONAL_CONSTANT * slope[0] - friction * vx / speed;
            double fy = -coefficients.GRAVITATIONAL_CONSTANT * slope[1] - friction * vy / speed;

            return new double[]{vx, vy, fx, fy};
        };

        double[] state = { ball.getX(), ball.getY(), ball.getVx(), ball.getVy() };
        double[] newState = DifferentialEquation.RK4Method.solve(system, state, dt);

        setState(newState[0], newState[1], newState[2], newState[3]);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setState(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
}

