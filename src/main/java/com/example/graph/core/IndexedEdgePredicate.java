package com.example.graph.core;

public interface IndexedEdgePredicate<W> {
    boolean test(int from, int to, W weight);
}
