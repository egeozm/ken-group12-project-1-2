package com.example;

import java.util.function.BiFunction;

public class Terrain {
    private BiFunction<Double, Double, Double> heightFunction;
    private double kineticFrictionGrass;
    private double staticFrictionGrass;
    private double kineticFrictionSand;
    private double staticFrictionSand;

    public Terrain(int i, int kineticFrictionGrass, int staticFrictionGrass) {
    }

    public static void main(String[] args) {
        // height function for the terrain to be generated
        BiFunction<Double, Double, Double> heightFunction = (x, y) -> 0.4 * (0.9 - Math.exp(-(x * x + y * y) / 8));
        PhysicsCoefficients coefficients = new PhysicsCoefficients(0.08, 0.15, 0.2, 0.25, 0.15);

        // this creates the terrain according to the height function
        Terrain terrain = new Terrain(heightFunction, coefficients.getKineticFrictionGrass(), coefficients.getStaticFrictionGrass(), coefficients.getKineticFrictionSand(), coefficients.getStaticFrictionSand());

        
    }

    public Terrain(BiFunction<Double, Double, Double> heightFunction, double kineticFrictionGrass, double staticFrictionGrass, double kineticFrictionSand, double staticFrictionSand) {
        this.heightFunction = heightFunction;
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.staticFrictionGrass = staticFrictionGrass;
        this.kineticFrictionSand = kineticFrictionSand;
        this.staticFrictionSand = staticFrictionSand;
    }

    public double getHeight(double x, double y) {
        double height = heightFunction.apply(x, y);
        return Math.max(-10, Math.min(10, height));
    }

    public double[] getSlope(double x, double y) {
        double epsilon = 1e-6;
        double height = getHeight(x, y);
        double dhdx = (getHeight(x + epsilon, y) - height) / epsilon;
        double dhdy = (getHeight(x, y + epsilon) - height) / epsilon;
        return new double[]{dhdx, dhdy};
    }

    /*public double[] getCurvature(double x, double y) {
        double epsilon = 1e-6;
        double height = getHeight(x, y);
        double d2hdx2 = (getHeight(x + epsilon, y) - 2 * height + getHeight(x - epsilon, y)) / (epsilon * epsilon);
        double d2hdy2 = (getHeight(x, y + epsilon) - 2 * height + getHeight(x, y - epsilon)) / (epsilon * epsilon);
        double d2hdxdy = (getHeight(x + epsilon, y + epsilon) - getHeight(x + epsilon, y - epsilon) - getHeight(x - epsilon, y + epsilon) + getHeight(x - epsilon, y - epsilon)) / (4 * epsilon * epsilon);
        return new double[]{d2hdx2, d2hdy2, d2hdxdy};
    }*/

    public boolean isSand(double x, double y) {
        boolean sandPit1 = (x - 4) * (x - 4) + (y - 2) * (y - 2) < 1.5; // circle at (4, 2)
        boolean sandPit2 = (x + 3) * (x + 3) + (y + 2) * (y + 2) < 2; // circle at (-3, -2)
        return sandPit1 || sandPit2;
    }

    public boolean isWater(double x, double y) {
        return getHeight(x, y) < 0;
    }

    public double getKineticFriction(double x, double y) {
        if (isSand(x, y)) {
            return kineticFrictionSand;
        }
        return kineticFrictionGrass;
    }

    public double getStaticFriction(double x, double y) {
        if (isSand(x, y)) {
            return staticFrictionSand;
        }
        return staticFrictionGrass;
    }

    public String getTerrainType(int i, int i1) {
        return "";
    }

    public void grassLand() {
    }
}


