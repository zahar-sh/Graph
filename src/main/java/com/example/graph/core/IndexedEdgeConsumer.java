package com.example.graph.core;

public interface IndexedEdgeConsumer<W> {
    void accept(int from, int to, W weight);
}
