package com.example.graph.core;

public interface EdgePredicate<N, W> {
    boolean test(N from, N to, W weight);
}
