package com.example.graph.core;

import java.util.function.Consumer;

public abstract class AbstractGraph<N, W> implements Graph<N, W> {

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean removeChildIf(N from, EdgePredicate<? super N, ? super W> filter) {
        return removeEdgeIf((f, to, weight) -> {
            return from.equals(f) && filter.test(f, to, weight);
        });
    }

    @Override
    public boolean removeParentIf(N to, EdgePredicate<? super N, ? super W> filter) {
        return removeEdgeIf((from, t, weight) -> {
            return to.equals(t) && filter.test(from, t, weight);
        });
    }

    @Override
    public void forEachChild(N from, EdgeConsumer<? super N, ? super W> action) {
        forEachEdge((f, to, weight) -> {
            if (from.equals(f)) {
                action.accept(f, to, weight);
            }
        });
    }

    @Override
    public void forEachParent(N to, EdgeConsumer<? super N, ? super W> action) {
        forEachEdge((from, t, weight) -> {
            if (to.equals(t)) {
                action.accept(from, t, weight);
            }
        });
    }

    @Override
    public void forEachWeight(Consumer<? super W> action) {
        forEachEdge((from, to, weight) -> {
            action.accept(weight);
        });
    }

    @Override
    public void putAll(Graph<? extends N, ? extends W> graph) {
        graph.forEachEdge(this::put, this::add);
    }
}
