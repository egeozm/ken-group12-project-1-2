public class Ball {
    private double xPos, yPos, zPos;
    private double xVelocity;
    private double zVelocity;
    private PhysicsCoefficients physics;
    private String currentTerrain;

    public Ball(double x, double y, double z, PhysicsCoefficients physics) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.physics = physics;
        this.currentTerrain = "sand"; // default terrain
        physics.setFriction(currentTerrain);
    }

    public void updatePosition(double timeStep) {
        // Apply friction based on current terrain
        double friction = physics.KF * physics.normalForce;
        xVelocity += friction * timeStep;
        zVelocity += friction * timeStep;

        // Update position
        xPos += xVelocity * timeStep;
        zPos += zVelocity * timeStep;
    }

    public void setCurrentTerrain(String terrain) {
        if (!terrain.equals(currentTerrain)) {
            currentTerrain = terrain;
            physics.setFriction(currentTerrain);
        }
    }

    public double[] getPosition() {
        return new double[]{xPos, yPos, zPos};
    }

    public static void main(String[] args) {
        double initialX = 0.0;
        double initialY = 0.0;
        double initialZ = 0.0;
        PhysicsCoefficients physics = new PhysicsCoefficients();
        Ball ball = new Ball(initialX, initialY, initialZ, physics);

        double timeStep = 0.1;
        double totalTime = 1.0;

        System.out.println("Start coordinates:");
        double[] startPos = ball.getPosition();
        System.out.println("X: " + startPos[0] + ", Y: " + startPos[1] + ", Z: " + startPos[2]);

        for (double t = 0; t < totalTime; t += timeStep) {
            ball.updatePosition(timeStep);
        }

        System.out.println("\nEnd coordinates after " + totalTime + " seconds:");
        double[] endPos = ball.getPosition();
        System.out.println("X: " + endPos[0] + ", Y: " + endPos[1] + ", Z: " + endPos[2]);
    }
}
