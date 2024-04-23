public class Ball {
    private double xPos, yPos, zPos;
    private double xVelocity, zVelocity;
    private PhysicsCoefficients physics;
    private String currentTerrain;

    public Ball(double x, double y, double z, PhysicsCoefficients physics) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.physics = physics;
        this.currentTerrain = "grass"; // will have to change that be updated somewhere else afterwards but for now it's ok here
    }

    public void updatePosition(double timeStep) {
        physics.setFriction(currentTerrain);

        double frictionForce = physics.KF * physics.normalForce;
        double frictionAccel = frictionForce / physics.golfBallMass;
        double drag = physics.drag(Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity));

        xVelocity;
        zVelocity;

        xVelocity;
        zVelocity;

        xPos;
        zPos;

        if (currentTerrain.equals("water")) {
            yPos = 0;
        } else {
            yPos;
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
