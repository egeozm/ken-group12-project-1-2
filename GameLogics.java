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
            resetBallPositionToPrevious();
            return true;
        }

        return false;
    }

    private void resetBallPositionToPrevious() {
        double[] currentPosition = ball.getPosition();
        // Subtract 1 from each coordinate to approximate the previous position
        double previousX = currentPosition[0] - 1;
        double previousY = currentPosition[1] - 1;
        double previousZ = currentPosition[2] - 1;

        ball.setPosition(previousX, previousY, previousZ);
        ball.setVelocity(0, 0); // Reset velocity to zero
    }
}
