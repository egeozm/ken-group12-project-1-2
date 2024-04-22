public class PhysicsCoefficients {
    public double grassKF = 0.1;//kf is the coefficient of kinetic friction
    public double grassSF = 0.15;//sf is the coefficient of static friction
    public double sandKF = 0.6;
    public double sandSF = 0.9;
    public double waterKF = 0.1;
    public double waterSF = 0.15;
    public double gravity = 9.81;//m/s^2
    public double golfBallMass = 0.0459;//kg
    public double golfBallRadius = 0.021;//m
    public double vmax = 5;//m/s
    public double time = 0;
    public double KF = 0.01;
    public double SF = 0.01;
    public double slope = 0.0;//measured in radians i think

    public double normalForce = golfBallMass*gravity*Math.cos(slope);
    public void setFriction(String terrain) {//use this function when ball's shot and you need to set the friction for current terrain
        switch (terrain) {
            case "grass":
                SF = grassSF;
                KF = grassKF;
                break;
            case "sand":
                SF = sandSF;
                KF = sandKF;
                break;
            case "water":
                SF = waterSF+grassKF;
                KF = waterKF+grassSF;//lets assume the terrain below water is grass
                break;
            default:
                System.out.println("Invalid terrain");
                break;
        }
    }
    public double ballSpeed(){

    }
    
}
