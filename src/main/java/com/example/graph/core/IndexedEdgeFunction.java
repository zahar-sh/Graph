package com.example.graph.core;

public interface IndexedEdgeFunction<W, R> {
    R apply(int from, int to, W weight);
}
