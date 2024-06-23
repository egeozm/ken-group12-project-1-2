package com.ode;

import com.gui.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class UpdatedBall {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final Terrain terrain;

    public UpdatedBall(Terrain terrain) {
        this.vx = 0;
        this.vy = 0;
        this.terrain = terrain;
    }

}
