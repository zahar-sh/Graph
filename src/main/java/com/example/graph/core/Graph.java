package com.example.graph.core;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Graph<N, W> {

    int size();

    boolean isEmpty();

    boolean isEmptyEdges();

    boolean isEmptyChildren(N from);

    boolean isEmptyParents(N to);

    boolean containsNode(N node);

    boolean containsEdge(N from, N to);

    boolean containsWeight(W weight);

    boolean add(N node);

    boolean replace(N node, N newNode);

    boolean remove(N node);

    W get(N from, N to);

    W put(N from, N to, W weight);

    W remove(N from, N to);

    void clear();

    void clearEdges();

    void clearChildren(N from);

    void clearParents(N to);

    void replaceAll(EdgeFunction<? super N, ? super W, ? extends W> function);

    boolean removeNodeIf(Predicate<? super N> filter);

    boolean removeEdgeIf(EdgePredicate<? super N, ? super W> filter);

    boolean removeChildIf(N from, EdgePredicate<? super N, ? super W> filter);

    boolean removeParentIf(N to, EdgePredicate<? super N, ? super W> filter);

    void forEachNode(Consumer<? super N> action);

    void forEachEdge(EdgeConsumer<? super N, ? super W> action);

    void forEachEdge(EdgeConsumer<? super N, ? super W> edgeConsumer, Consumer<? super N> emptyNodeConsumer);

    void forEachChild(N from, EdgeConsumer<? super N, ? super W> action);

    void forEachParent(N to, EdgeConsumer<? super N, ? super W> action);

    void forEachWeight(Consumer<? super W> action);

    void putAll(Graph<? extends N, ? extends W> graph);

    default W getOrDefault(N from, N to, W weight) {
        W oldWeight = get(from, to);
        if (oldWeight != null) {
            return oldWeight;
        }
        return weight;
    }

    default W putIfAbsent(N from, N to, W weight) {
        W oldWeight = get(from, to);
        if (oldWeight != null) {
            return oldWeight;
        }
        put(from, to, weight);
        return null;
    }

    default W computeIfAbsent(N from, N to, BiFunction<? super N, ? super N, ? extends W> mappingFunction) {
        W oldWeight = get(from, to);
        if (oldWeight != null) {
            return oldWeight;
        }
        W newWeight = mappingFunction.apply(from, to);
        if (newWeight != null) {
            put(from, to, newWeight);
            return newWeight;
        }
        return null;
    }

    default W computeIfPresent(N from, N to, EdgeFunction<? super N, ? super W, ? extends W> remappingFunction) {
        W oldWeight = get(from, to);
        if (oldWeight == null) {
            return null;
        }
        W newWeight = remappingFunction.apply(from, to, oldWeight);
        if (newWeight != null) {
            put(from, to, newWeight);
            return newWeight;
        }
        remove(from, to);
        return null;
    }

    default W compute(N from, N to, EdgeFunction<? super N, ? super W, ? extends W> remappingFunction) {
        W oldWeight = get(from, to);
        W newWeight = remappingFunction.apply(from, to, oldWeight);
        if (newWeight != null) {
            put(from, to, newWeight);
            return newWeight;
        }
        if (oldWeight != null) {
            remove(from, to);
        }
        return null;
    }

    default W merge(N from, N to, W weight, BiFunction<? super W, ? super W, ? extends W> remappingFunction) {
        W oldWeight = get(from, to);
        W newWeight = (oldWeight == null)
                ? weight
                : remappingFunction.apply(oldWeight, weight);
        if (newWeight != null) {
            put(from, to, newWeight);
            return newWeight;
        }
        if (oldWeight != null) {
            remove(from, to);
        }
        return null;
    }
}