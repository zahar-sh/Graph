package com.example.graph.core;

import java.util.Comparator;

public class SortedMatrixGraph<N, W> extends ArrayMatrixGraph<N, W> {

    private final Comparator<? super N> comparator;

    public SortedMatrixGraph(Comparator<? super N> comparator) {
        this.comparator = comparator;
    }

    public SortedMatrixGraph(Comparator<? super N> comparator, int initialCapacity) {
        super(initialCapacity);
        this.comparator = comparator;
    }

    public SortedMatrixGraph(Comparator<? super N> comparator, Graph<? extends N, ? extends W> graph) {
        super(graph.size());
        this.comparator = comparator;
        putAll(graph);
    }

    public Comparator<? super N> getComparator() {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(N node) {
        return ArrayHelper.binarySearch((N[]) nodes, 0, size, comparator, node);
    }

    @Override
    public boolean add(N node) {
        int index = indexOf(node);
        if (containsNodeByIndex(index)) {
            return false;
        }
        index = (-index) - 1;
        int size = this.size;
        int newSize = size + 1;
        ensureCapacity(newSize);
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        ArrayHelper.insert(nodes, size, node, index);
        ArrayHelper.insert(matrix, size, new Object[matrix.length], index);
        this.size = size + 1;
        return true;
    }

    @Override
    public boolean replace(N node, N newNode) {
        int nodeIndex = indexOf(node);
        if (!containsNodeByIndex(nodeIndex)) {
            return false;
        }
        int newNodeIndex = indexOf(newNode);
        if (containsNodeByIndex(newNodeIndex)) {
            return false;
        }
        newNodeIndex = (-newNodeIndex) - 2;
        int size = this.size;
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        nodes[nodeIndex] = newNode;
        if (nodeIndex < newNodeIndex) {
            ArrayHelper.shiftAndSwapLeft(nodes, nodeIndex, newNodeIndex);
            ArrayHelper.shiftAndSwapLeft(matrix, nodeIndex, newNodeIndex);
            for (int i = 0; i < size; i++) {
                Object[] row = matrix[i];
                ArrayHelper.shiftAndSwapLeft(row, nodeIndex, newNodeIndex);
            }
        } else {
            ArrayHelper.shiftAndSwapRight(nodes, newNodeIndex, nodeIndex);
            ArrayHelper.shiftAndSwapRight(matrix, newNodeIndex, nodeIndex);
            for (int i = 0; i < size; i++) {
                Object[] row = matrix[i];
                ArrayHelper.shiftAndSwapRight(row, newNodeIndex, nodeIndex);
            }
        }
        return true;
    }

    @Override
    public SortedMatrixGraph<N, W> clone() {
        return (SortedMatrixGraph<N, W>) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
