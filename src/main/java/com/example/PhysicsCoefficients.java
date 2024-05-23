package com.example;

public class PhysicsCoefficients {

    public static final double GRAVITATIONAL_CONSTANT = 9.81;

    public static final double MASS_OF_GOLF_BALL = 0.0459;

    // range: 0.05 - 0.1
    private double kineticFrictionGrass;

    // range: 0.1 - 0.2
    private double staticFrictionGrass;

    // should be higher than grass kinetic friction
    private double kineticFrictionSand;

    // should be higher than grass static friction
    private double staticFrictionSand;

    // maximum speed of ball in m/s
    public static final double MAXIMUM_SPEED = 5.0;

    // range: 0.05m - 0.15m
    private double targetRadius;

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
     * Gets the static friction coefficient for grass.
     *
     * @return The static friction coefficient for grass.
     */
    public double getStaticFrictionGrass() {
        return staticFrictionGrass;
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
        this.staticFrictionGrass = staticFrictionGrass;
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
     * Gets the static friction coefficient for sand.
     *
     * @return The static friction coefficient for sand.
     */
    public double getStaticFrictionSand() {
        return staticFrictionSand;
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
        this.staticFrictionSand = staticFrictionSand;
    }

    /**
     * Gets the target radius.
     *
     * @return The target radius.
     */
    public double getTargetRadius() {
        return targetRadius;
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
        this.targetRadius = targetRadius;
    }
}