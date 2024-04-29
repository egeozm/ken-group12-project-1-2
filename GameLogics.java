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
            ball.updatePosition(deltaTime);
        }
    }

    private boolean handleCollisions() {
        double[] position = ball.getPosition();
        String currentTerrain = terrain.getTerrainType(position[0], position[1]);

        ball.setCurrentTerrain(currentTerrain);

        if (currentTerrain.equals("water")) {
            System.out.println("Ball in water, applying penalty!");
            resetBallPosition();
            return true;
        }

        return false;
    }

    private void resetBallPosition() {
        double[] previousPosition = ball.getPreviousPosition();
        ball.setPosition(previousPosition[0], previousPosition[1], previousPosition[2]);
        ball.setVelocity(0, 0); // Reset velocity to zero when the ball hits water
    }
}
