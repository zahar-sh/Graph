package com.example.graph.core;

public interface EdgeFunction<N, W, R> {
    R apply(N from, N to, W weight);
}
