package com.gui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.bots.advanced.PSO.AI_Player;
import com.bots.advanced.PSO.Random_AI_Player;
import com.bots.advanced.graph.GraphPSO.AdvancedPSO;
import com.ode.Ball;
import com.bots.basic.BasicBot;
import com.ode.PhysicsCoefficients;
import com.gui.debbugers.Debugger;
import com.gui.labels.UILabel;
import com.gui.objects.GolfBall;
import com.gui.objects.Trajectory;
import com.gui.terrain.Terrain;
import com.gui.debbugers.Debugger3D;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InputHandler {

    private static GolfBall golfBall;
    private static GolfBall advancedAI;
    private static Trajectory trajectory;
    private static int kickPower;
    private static boolean kickingMode;
    private static Vector3 kickDirection;
    private static int numberOfShots;
    private static UILabel bottomLeftLabel;
    private static UILabel shotsLabel;
    private static Renderer3D renderer3D;
    private static Camera camera;
    private static Terrain terrain;
    private static Debugger3D debugger;
    private static int advancedPsoIdx = 0;
    private static final PhysicsCoefficients physicsCoefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

    public static void initialize(GolfBall golfBall, GolfBall advancedAI, Trajectory trajectory,
                                  int kickPower, boolean kickingMode, Vector3 kickDirection, int numberOfShots,
                                  UILabel bottomLeftLabel, UILabel shotsLabel, Renderer3D renderer3D,
                                  Camera camera, Terrain terrain, Debugger3D debugger) {
        InputHandler.golfBall = golfBall;
        InputHandler.advancedAI = advancedAI;
        InputHandler.trajectory = trajectory;
        InputHandler.kickPower = kickPower;
        InputHandler.kickingMode = kickingMode;
        InputHandler.kickDirection = kickDirection;
        InputHandler.numberOfShots = numberOfShots;
        InputHandler.bottomLeftLabel = bottomLeftLabel;
        InputHandler.shotsLabel = shotsLabel;
        InputHandler.renderer3D = renderer3D;
        InputHandler.camera = camera;
        InputHandler.terrain = terrain;
        InputHandler.debugger = debugger;
    }

    public static void handleInput() throws ExecutionException, InterruptedException {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            toggleKickingMode();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            executeBasicPSOMove();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            executeRandomMove();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            executeAdvancedPSOMove();
        }

        if (kickingMode) {
            handleKickingModeInput();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            debugger.toggleDebugger();
        }

        debugger.handleInput((PerspectiveCamera) camera, terrain.getHeightCoordinates());
    }

    private static void toggleKickingMode() {
        kickingMode = !kickingMode;
        if (kickingMode) {
            camera.position.set(golfBall.getInstance().transform.getTranslation(new Vector3()).add(0, 10, 10));
            camera.lookAt(golfBall.getInstance().transform.getTranslation(new Vector3()));
            camera.update();
        }
    }

    private static void executeBasicPSOMove() throws ExecutionException, InterruptedException {
        Ball ball = new Ball(terrain);
        AI_Player player2 = new AI_Player(advancedAI, terrain.getHoleX(), terrain.getHoleZ(), physicsCoefficients);
        double[] bestV = player2.getBestVxVy();
        double[][] trajectoryData = ball.getTrajectoryArray(0.1, advancedAI.getPosition().x, advancedAI.getPosition().z, bestV[0], bestV[1], 30);
        Debugger.printMatrix(trajectoryData);
        advancedAI.setTrajectoryVec3(trajectoryData);
        advancedAI.kickingTurn();
    }

    private static void executeRandomMove() throws ExecutionException, InterruptedException {
        Ball ball = new Ball(terrain);
        Random_AI_Player player2 = new Random_AI_Player(advancedAI, terrain.getHoleX(), terrain.getHoleZ(), physicsCoefficients);
        double[] bestV = player2.getBestVxVy();
        double[][] trajectoryData = ball.getTrajectoryArray(0.1, advancedAI.getPosition().x, advancedAI.getPosition().z, bestV[0], bestV[1], 30);
        Debugger.printMatrix(trajectoryData);
        advancedAI.setTrajectoryVec3(trajectoryData);
        advancedAI.kickingTurn();
    }
    private static void executeAdvancedPSOMove() throws ExecutionException, InterruptedException {
        Ball ball = new Ball(terrain);
        AdvancedPSO player2 = new AdvancedPSO(advancedAI, terrain.getHoleX(), terrain.getHoleZ(), physicsCoefficients);
        List<double[]> bestV = player2.getBestPath();
        System.out.println(bestV.size());
        executeNextMove(ball, player2, bestV);
    }

    private static void executeNextMove(Ball ball, AdvancedPSO player2, List<double[]> bestV) {
        if (advancedPsoIdx >= bestV.size()) {
            advancedPsoIdx = 0;
            return; // All moves executed
        }

        if (!advancedAI.isMoving()) {
            System.out.println("dawdawdawdawd");
            double[][] trajectoryData = ball.getTrajectoryArray(0.1, advancedAI.getPosition().x, advancedAI.getPosition().z, bestV.get(advancedPsoIdx)[2], bestV.get(advancedPsoIdx)[3], 30);
            Debugger.printMatrix(trajectoryData);
            advancedAI.setTrajectoryVec3(trajectoryData);
            advancedAI.kickingTurn();
            advancedPsoIdx++;
        }

        // Schedule the next check
        Executors.newSingleThreadScheduledExecutor().schedule(() -> executeNextMove(ball, player2, bestV), 100, TimeUnit.MILLISECONDS);
    }


    private static void handleKickingModeInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            increaseKickPower();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            decreaseKickPower();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            kickDirection.z -= 0.1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            kickDirection.z += 0.1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            kickDirection.x -= 0.1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            kickDirection.x += 0.1f;
        }

        camera.lookAt(golfBall.getInstance().transform.getTranslation(new Vector3()).add(kickDirection));
        camera.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeKick();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            executeAIKick();
        }
    }

    private static void increaseKickPower() {
        kickPower = Math.min(kickPower + 1, 100);
        trajectory.setKickingPower(kickPower);
        bottomLeftLabel.setText("Power: " + kickPower);
    }

    private static void decreaseKickPower() {
        kickPower = Math.max(kickPower - 1, 0);
        trajectory.setKickingPower(kickPower);
        bottomLeftLabel.setText("Power: " + kickPower);
    }

    private static void executeKick() {
        numberOfShots += 1;
        shotsLabel.setText("Shots done: " + numberOfShots);
        kickingMode = false;
        trajectory.toggleKickingMode();
        resetCamera();

        Ball ball = new Ball(terrain);
        double[][] trajectoryData = ball.getTrajectoryArray(0.1, golfBall.getPosition().x, golfBall.getPosition().z, trajectory.getDirection().x * kickPower, trajectory.getDirection().z * kickPower, 30);
        golfBall.setTrajectoryVec3(trajectoryData);
        golfBall.kickingTurn();
        kickPower = 1;
        trajectory.setKickingPower(kickPower);
        bottomLeftLabel.setText("Power: " + kickPower);
    }

    private static void executeAIKick() {
        kickingMode = false;
        trajectory.toggleKickingMode();
        resetCamera();

        double[] goalVelocity = goalVelocity();
        Ball ball = new Ball(terrain);
        BasicBot bot = new BasicBot(0.1, 10, terrain);
        double[] calculatedVelocity = bot.calculateNextMove(golfBall.getPosition().x, golfBall.getPosition().z, goalVelocity[0], goalVelocity[1]);
        double[][] trajectoryData = ball.getTrajectoryArray(0.1, golfBall.getPosition().x, golfBall.getPosition().z, calculatedVelocity[0], calculatedVelocity[1], 30);
        golfBall.setTrajectoryVec3(trajectoryData);
        golfBall.kickingTurn();
        kickPower = 1;
        trajectory.setKickingPower(kickPower);
        bottomLeftLabel.setText("Power: " + kickPower);
    }

    private static double[] goalVelocity() {
        return new double[]{terrain.getHoleX() - golfBall.getPosition().x, terrain.getHoleZ() - golfBall.getPosition().z};
    }

    private static void resetCamera() {
        Vector3 previousPosition = camera.position;
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(previousPosition.x, previousPosition.y, previousPosition.z);
        camera.lookAt(new Vector3(golfBall.getPosition().x, golfBall.getPosition().y, golfBall.getPosition().z));
        camera.up.set(Vector3.Y);
        camera.near = 1f;
        camera.far = 30000f;
        camera.update();
    }
    public static void trajectoryCameraToggle(){
        if (trajectory.isKickingMode()) {
            Vector3 ballPosition = golfBall.getInstance().transform.getTranslation(new Vector3());
            Vector3 direction = trajectory.getDirection();

            // Set the camera position behind the ball, orthogonal to the trajectory
            Vector3 cameraPosition = new Vector3(ballPosition).sub(direction.nor().scl(10)).add(0, 5, 0);
            camera.position.set(cameraPosition);
            camera.lookAt(ballPosition);
            camera.up.set(Vector3.Y);
            camera.update();
        }

    }
}
