package com.bots.advanced.graph.GraphPSO;

import java.util.*;

public class Node {
    double x, y;
    double cost; // Cost to reach this node
    double heuristic; // Estimated cost to the goal
    Node parent; // To keep track of the path

    public Node(double x, double y, double cost, double heuristic, Node parent) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
    }

    public double getTotalCost() {
        return this.cost + this.heuristic;
    }
}
