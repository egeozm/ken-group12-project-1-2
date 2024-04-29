public class Ball {
    private double previousXPos, previousYPos, previousZPos; // for if the ball lands in water and has to be brought back
    private double xPos, yPos, zPos;
    private double xVelocity, yVelocity, zVelocity;
    private PhysicsCoefficients physics;
    private String currentTerrain;

    public Ball(double x, double y, double z, PhysicsCoefficients physics) {
        this.previousXPos = x;
        this.previousYPos = y;
        this.previousZPos = z;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.physics = physics;
        this.currentTerrain = "grass"; // we will have to get this somewhere else after but for now here it's ok
    }

    private class MotionEquation implements DifferentialEquation {
        @Override
        public double computeDerivative(double t, double y) {
            double frictionForce = physics.KF * physics.normalForce;
            double frictionAccel = frictionForce / physics.golfBallMass;
            double totalAccel = frictionAccel;

            return totalAccel;
        }
    }

    public void updatePosition(double timeStep) {
        DifferentialEquation motionEquation = new MotionEquation();

        double[] newXPosArray = DifferentialEquation.RK4Method.solve(motionEquation, xPos, 0, timeStep, 1);
        double[] newYPosArray = DifferentialEquation.RK4Method.solve(motionEquation, yPos, 0, timeStep, 1);
        double[] newZPosArray = DifferentialEquation.RK4Method.solve(motionEquation, zPos, 0, timeStep, 1);

        previousXPos = xPos;
        previousYPos = yPos;
        previousZPos = zPos;

        xPos = newXPosArray[1];
        yPos = newYPosArray[1];
        zPos = newZPosArray[1];
        xVelocity = (newXPosArray[1] - previousXPos) / timeStep;
        //yVelocity = (newYPosArray[1] - previousYPos) / timeStep;
        zVelocity = (newZPosArray[1] - previousZPos) / timeStep;

        if (currentTerrain.equals("water")) {
            xPos = previousXPos;
            yPos = previousYPos;
            zPos = previousZPos;
        }
    }

    public void setCurrentTerrain(String terrain) {
        this.currentTerrain = terrain;
    }


    public double[] getPosition(){
        return new double[]{xPos, yPos, zPos};
    }

    /*
    public double getXPos(){ 
        return xPos; 
    }

    public double getYPos(){ 
        return yPos; 
    }

    public double getZPos(){ 
        return zPos; 
    }
    */

    public double[] getVelocity(){ 
        return new double[]{xVelocity, zVelocity};
    }

    /*
    public double getZVelocity(){ 
        return zVelocity; 
    }
    */

    /*
    public double getYVelocity(){
        return yVelocity;
    }
    */

    public void setPosition(double x, double y, double z) {
        xPos = x;
        yPos = y;
        zPos = z;
    }

    public void setVelocity(double xVel, double zVel) {
        xVelocity = xVel;
        zVelocity = zVel;
    }
}
