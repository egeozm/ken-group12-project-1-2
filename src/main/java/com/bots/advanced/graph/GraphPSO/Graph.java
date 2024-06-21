package com.bots.advanced.graph.GraphPSO;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    /*public static List<Node> getNeighbors(Node node, double goalX, double goalY) {
        List<Node> neighbors = new ArrayList<>();
        double[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        for (double[] dir : directions) {
            double newX = node.x + dir[0];
            double newY = node.y + dir[1];
            Node neighbor = new Node(newX, newY, node.g + 1, heuristic(newX, newY, goalX, goalY));
            neighbors.add(neighbor);
        }
        return neighbors;
    }*/

    public static double heuristic(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
