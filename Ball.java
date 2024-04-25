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

    public void updatePosition(double timeStep) {
        physics.setFriction(currentTerrain);

        double velocityMagnitude = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
        double dragForce = physics.drag(velocityMagnitude);
        double frictionForce = physics.KF * physics.normalForce;
        double frictionAccel = frictionForce / physics.golfBallMass;

        double dragAccel = dragForce / physics.golfBallMass;
        double totalAccel = frictionAccel + dragAccel;

        if (velocityMagnitude != 0) {
            xVelocity = 0; // still need to figure out how to calculate this using RK4
            yVelocity = 0; // still need to figure out how to calculate this using RK4
            zVelocity = 0; // still need to figure out how to calculate this using RK4
        }

        xPos += xVelocity * timeStep;
        yPos -= 0.5 * physics.gravity * timeStep * timeStep;
        zPos += zVelocity * timeStep;

        if (currentTerrain.equals("water")) {
            xPos = previousXPos;
            yPos = previousYPos;
            zPos = previousZPos;
        } else {
            previousXPos = xPos;
            previousYPos = yPos;
            previousZPos = zPos;
        }
    }

    public void setCurrentTerrain(String terrain) {
        this.currentTerrain = terrain;
    }

    public double getXPos(){ 
        return xPos; 
    }

    public double getYPos(){ 
        return yPos; 
    }

    public double getZPos(){ 
        return zPos; 
    }

    public double getXVelocity(){ 
        return xVelocity; 
    }

    public double getZVelocity(){ 
        return zVelocity; 
    }

    public double getYVelocity(){
        return yVelocity;
    }

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

