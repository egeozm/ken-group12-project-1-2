package com.ode;

import com.gui.terrain.Terrain;

public class UpdatedBall {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final Terrain terrain;
    public double golfBallMass = 0.0459; // kg
    public double gravitationalConstant = PhysicsCoefficients.GRAVITATIONAL_CONSTANT;
    public double dhdx;
    public double dhdy;
    public double[] tempKF = new double[2];//placeholder, kinetic friction
    public int terrainType;//placeholder
//in arrays 0 is for x, 1 for y, 2 for z

    public UpdatedBall(Terrain terrain) {
        this.vx = 0;
        this.vy = 0;
        this.terrain = terrain;
    }


    public double nForceX(){
       return ((golfBallMass*gravitationalConstant)/(1+dhdx*dhdx+dhdy*dhdy))*(-dhdx);
    }

    public double nForceY(){
        return ((golfBallMass*gravitationalConstant)/(1+dhdx*dhdx+dhdy*dhdy))*(-dhdy);
    }

    public double friction(){
        return (-tempKF[terrainType]*golfBallMass*gravitationalConstant)
                /((Math.sqrt(1+dhdx*dhdx+dhdy*dhdy))*(Math.sqrt(vx*vx+vy*vy+Math.pow((dhdx*vx+dhdy*vy),2))));
    }

    public double accelX() {
        double netForceX = nForceX() - friction();
        return netForceX / golfBallMass; // Calculate acceleration in x-direction
    }

    public double accelY() {
        double netForceY = nForceY() - friction();
        return netForceY / golfBallMass; // Calculate acceleration in y-direction
    }
}
