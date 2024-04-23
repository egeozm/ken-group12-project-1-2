public class GameLogics {
    private Ball ball;
    private Terrainn terrain;
    private PhysicsCoefficients coefficients;

    public GameLogics(Ball ball, Terrainn terrain, PhysicsCoefficients coefficients) {
        this.ball = ball;
        this.terrain = terrain;
        this.coefficients = coefficients;
    }

    public void update(double deltaTime) {
        if (!handleCollisions()) {
            Vector forces = calculateForces();
            updateBallState(forces, deltaTime);
        }
    }

    private boolean handleCollisions() {
        // Check for water or other hazards
        if (terrain.isWater(ball.getPosition())) {
            System.out.println("Ball in water, apply penalty!");
            resetBallPosition(); // Or handle according to game rules
            return true;
        }else if(terrain.isObstacle(ball.getPosition())){
            System.out.println("Ball hit an obstacle");
            resetBallPosition(); // Or handle according to game rules
            return true;
        }
        return false;
    }

    private Vector calculateForces() {
        double gravity = coefficients.gravity * ball.getMass();
        double normalForce = calculateNormalForce();
        double friction = coefficients.getFrictionCoefficient(currentTerrain()) * normalForce;
        // Calculate other forces if needed

        double totalForceX = -friction; // Simplified for example
        double totalForceY = gravity - normalForce; // Simplified for example
        return new Vector(totalForceX, totalForceY);
    }

    private void updateBallState(Vector forces, double deltaTime) {
        // Update velocity
        Vector acceleration = forces.multiply(1 / ball.getMass());
        ball.setVelocity(ball.getVelocity().add(acceleration.multiply(deltaTime)));

        // Update position
        ball.setPosition(ball.getPosition().add(ball.getVelocity().multiply(deltaTime)));
    }

    private double calculateNormalForce() {
        // Placeholder for normal force calculation
        return coefficients.gravity * ball.getMass(); // Simplified
    }

    private void resetBallPosition() {
        // Reset ball to last safe position or starting position
    }

    private String currentTerrain() {
        // Determine current terrain from the ball's position
        return "grass"; // Simplified
    }
}
