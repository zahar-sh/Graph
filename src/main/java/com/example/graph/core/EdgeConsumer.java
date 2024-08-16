package com.example.graph.core;

public interface EdgeConsumer<N, W> {
    void accept(N from, N to, W weight);
}
