package com.example.graph.core;

import java.util.List;
import java.util.Objects;

public class Path<N, W> {
    private final List<N> path;
    private final W totalWeight;

    public Path(List<N> path, W totalWeight) {
        this.path = path;
        this.totalWeight = totalWeight;
    }

    public List<N> getPath() {
        return path;
    }

    public W getTotalWeight() {
        return totalWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path<?, ?> path1 = (Path<?, ?>) o;
        return Objects.equals(path, path1.path) && Objects.equals(totalWeight, path1.totalWeight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, totalWeight);
    }

    @Override
    public String toString() {
        return path + ": " + totalWeight;
    }
}
