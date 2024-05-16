
public class PhysicsCoefficients implements DifferentialEquation{
    public double grassKF = 0.10;//kf is the coefficient of kinetic friction
    public double grassSF = 0.15;//sf is the coefficient of static friction
    public double sandKF = 0.60;
    public double sandSF = 0.90;
    public double waterKF = 0.1;
    public double waterSF = 0.15;
    public double gravity = 9.81;//m/s^2
    public double golfBallMass = 0.0459;//kg
    public double golfBallRadius = 0.021;//m
    public double velocity = 0.0;//m/s
    public double vmax = 5.0;//m/s
    public double time = 0.0;
    public double KF = 0.01;
    public double SF = 0.01;
    public double slope = 0.0;//measured in radians, I think
    public double waterDensity = 1000.0;//kg/m^3
    public double airDensity = 1.225;//kg/m^3
    public double density = 1000.0;
    public double dragCoefficient = 0.47; // assumed drag coefficient for a golf ball
    public double crossSectionalArea = Math.PI * Math.pow(golfBallRadius, 2); // m^2

    public double normalForce = golfBallMass * gravity * Math.cos(slope);

    public void setFriction(String terrain) { //use this function when ball's shot, and you need to set the friction for current terrain
        switch (terrain) {
            case "grass":
                density = airDensity;
                SF = grassSF;
                KF = grassKF;
                break;
            case "sand":
                density = airDensity;
                SF = sandSF;
                KF = sandKF;
                break;
            case "water":// let's assume the terrain below water is sand
                density = waterDensity;
                SF = sandSF;
                KF = grassSF;
                break;
            default:
                System.out.println("Invalid terrain");
                break;
        }
    }

    @Override
    public double computeDerivative(double t, double v) {
        double sinTheta = Math.sin(slope);
        double dragForce = 0.5 * density * dragCoefficient * crossSectionalArea * Math.pow(v, 2);
        double frictionForce = KF * normalForce;
        double netForce = (golfBallMass * gravity * sinTheta) - dragForce - frictionForce;
        return netForce / golfBallMass;
    }

    public double updateVelocity(double dt) {
        double acceleration = computeDerivative(time, velocity);
        velocity += acceleration * dt;
        return velocity;
    }

    public double computeDistance(double initialVelocity, double dt) {
        double distance = 0.0;
        velocity = initialVelocity;
        
        while (velocity > 0) {
            double prevVelocity = velocity;
            velocity = updateVelocity(dt);
            distance += prevVelocity * dt;
            time += dt;
        }
        
        return distance;
    }
/* 
    public static void main(String[] args) {
        PhysicsCoefficients physics = new PhysicsCoefficients();
        physics.setFriction("grass");
        double initialVelocity = 5.0; // m/s
        double dt = 0.01; // time step in seconds
        double distance = physics.computeDistance(initialVelocity, dt);
        System.out.println("Distance traveled: " + distance + " meters");
    }
}
*/