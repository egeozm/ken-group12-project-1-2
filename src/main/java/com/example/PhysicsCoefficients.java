package com.example;

/**
 * This class defines the physics coefficients for different surfaces such as grass and sand.
 * It includes coefficients for kinetic and static friction on these surfaces and ensures that they are within valid ranges.
 */
public class PhysicsCoefficients {

    /**
     * The gravitational constant used in physics calculations.
     */
    public static final double GRAVITATIONAL_CONSTANT = 9.81;

    private double kineticFrictionGrass; // range: 0.05 - 0.1
    private double kineticFrictionSand;  // should be higher than grass kinetic friction

    /**
     * Constructs a PhysicsCoefficients instance with specified values for kinetic and static friction on grass and sand.
     *
     * @param kineticFrictionGrass The kinetic friction coefficient for grass must be between 0.05 and 0.1.
     * @param staticFrictionGrass  The static friction coefficient for grass must be between 0.1 and 0.2.
     * @param kineticFrictionSand  The kinetic friction coefficient for sand must be higher than kinetic friction for grass and less than 1.
     * @param staticFrictionSand   The static friction coefficient for sand must be higher than kinetic friction for sand and less than 1.
     * @param targetRadius         The radius of the target area must be between 0.05m and 0.15m.
     */
    public PhysicsCoefficients(double kineticFrictionGrass, double staticFrictionGrass, double kineticFrictionSand, double staticFrictionSand, double targetRadius) {
        setKineticFrictionGrass(kineticFrictionGrass);
        setStaticFrictionGrass(staticFrictionGrass);
        setKineticFrictionSand(kineticFrictionSand);
        setStaticFrictionSand(staticFrictionSand);
        setTargetRadius(targetRadius);
    }

    /**
     * Gets the kinetic friction coefficient for grass.
     *
     * @return The kinetic friction coefficient for grass.
     */
    public double getKineticFrictionGrass() {
        return kineticFrictionGrass;
    }

    /**
     * Sets the kinetic friction coefficient for grass.
     *
     * @param kineticFrictionGrass The kinetic friction coefficient for grass must be between 0.05 and 0.1.
     * @throws IllegalArgumentException if the coefficient is out of range.
     */
    public void setKineticFrictionGrass(double kineticFrictionGrass) {
        if (kineticFrictionGrass < 0.05 || kineticFrictionGrass > 0.1) {
            throw new IllegalArgumentException("Kinetic friction on grass must be between 0.05 and 0.1");
        }
        this.kineticFrictionGrass = kineticFrictionGrass;
    }

    /**
     * Sets the static friction coefficient for grass.
     *
     * @param staticFrictionGrass The static friction coefficient for grass must be between 0.1 and 0.2.
     * @throws IllegalArgumentException if the coefficient is out of range.
     */
    public void setStaticFrictionGrass(double staticFrictionGrass) {
        if (staticFrictionGrass < 0.1 || staticFrictionGrass > 0.2) {
            throw new IllegalArgumentException("Static friction on grass must be between 0.1 and 0.2");
        }
        // range: 0.1 - 0.2
    }

    /**
     * Gets the kinetic friction coefficient for sand.
     *
     * @return The kinetic friction coefficient for sand.
     */
    public double getKineticFrictionSand() {
        return kineticFrictionSand;
    }

    /**
     * Sets the kinetic friction coefficient for sand.
     *
     * @param kineticFrictionSand The kinetic friction coefficient for sand must be higher than kinetic friction on grass and less than 1.
     * @throws IllegalArgumentException if the coefficient is out of range.
     */
    public void setKineticFrictionSand(double kineticFrictionSand) {
        if (kineticFrictionSand <= kineticFrictionGrass || kineticFrictionSand >= 1) {
            throw new IllegalArgumentException("Kinetic friction in sand must be higher than kinetic friction on grass and less than 1");
        }
        this.kineticFrictionSand = kineticFrictionSand;
    }

    /**
     * Sets the static friction coefficient for sand.
     *
     * @param staticFrictionSand The static friction coefficient for sand must be higher than kinetic friction for sand and less than 1.
     * @throws IllegalArgumentException if the coefficient is out of range.
     */
    public void setStaticFrictionSand(double staticFrictionSand) {
        if (staticFrictionSand <= kineticFrictionSand || staticFrictionSand >= 1) {
            throw new IllegalArgumentException("Static friction in sand must be higher than kinetic friction in sand and less than 1");
        }
        // should be higher than grass static friction
    }

    /**
     * Sets the target radius.
     *
     * @param targetRadius The radius of the target area must be between 0.05m and 0.15m.
     * @throws IllegalArgumentException if the radius is out of range.
     */
    public void setTargetRadius(double targetRadius) {
        if (targetRadius < 0.05 || targetRadius > 0.15) {
            throw new IllegalArgumentException("Target radius must be between 0.05m and 0.15m");
        }
        // range: 0.05m - 0.15m
    }
}
