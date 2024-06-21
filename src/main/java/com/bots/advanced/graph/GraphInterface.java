package com.bots.advanced.graph;

import java.util.List;

public interface GraphInterface<T> {
    void addNode(T node);
    void addEdge(T node1, T node2);
    List<T> getNeighbors(T node);
    boolean hasNode(T node);
    boolean hasEdge(T node1, T node2);
}
