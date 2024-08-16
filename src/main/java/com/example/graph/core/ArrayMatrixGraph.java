package com.example.graph.core;

import java.util.Objects;

public class ArrayMatrixGraph<N, W> extends AbstractMatrixGraph<N, W> implements Cloneable {

    private static final Object[] EMPTY_ARRAY = {};

    private static final Object[][] EMPTY_MATRIX = {};

    protected Object[] nodes;

    protected Object[][] matrix;

    protected int size;

    public ArrayMatrixGraph() {
        nodes = EMPTY_ARRAY;
        matrix = EMPTY_MATRIX;
    }

    public ArrayMatrixGraph(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("InitialCapacity: " + initialCapacity);
        } else if (initialCapacity == 0) {
            nodes = EMPTY_ARRAY;
            matrix = EMPTY_MATRIX;
        } else {
            nodes = new Object[initialCapacity];
            matrix = new Object[initialCapacity][];
        }
    }

    public ArrayMatrixGraph(Graph<? extends N, ? extends W> graph) {
        this(graph.size());
        putAll(graph);
    }

    private void resize(int length) {
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        int size = this.size;
        this.nodes = ArrayHelper.copy(nodes, size, length);

        Object[][] newMatrix = new Object[length][];
        for (int i = 0; i < size; i++) {
            newMatrix[i] = ArrayHelper.copy(matrix[i], size, length);
        }
        this.matrix = newMatrix;
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > nodes.length) {
            int length = ArrayHelper.calculateNewLength(minCapacity);
            resize(length);
        }
    }

    public void trimToSize() {
        if (size == 0) {
            nodes = EMPTY_ARRAY;
            matrix = EMPTY_MATRIX;
        } else {
            resize(size);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(N node) {
        if (containsNode(node)) {
            return false;
        }
        int size = this.size;
        int newSize = size + 1;

        ensureCapacity(newSize);

        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        nodes[size] = node;
        matrix[size] = new Object[matrix.length];
        this.size = newSize;
        return true;
    }

    @Override
    public int indexOf(N node) {
        return ArrayHelper.linearSearch(nodes, 0, size, node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public N getNode(int index) {
        return (N) nodes[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public N setNode(int index, N newNode) {
        N oldNode = (N) nodes[index];
        nodes[index] = newNode;
        return oldNode;
    }

    @Override
    public boolean removeByIndex(int index) {
        if (!containsNodeByIndex(index)) {
            return false;
        }
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        int size = this.size;
        int newSize = this.size - 1;
        ArrayHelper.remove(nodes, newSize, index);
        ArrayHelper.remove(matrix, newSize, index);
        for (int i = 0; i < size; i++) {
            Object[] row = matrix[i];
            ArrayHelper.remove(row, newSize, index);
        }
        this.size = newSize;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public W getByIndex(int from, int to) {
        if (!containsNodeByIndex(from) || !containsNodeByIndex(to)) {
            return null;
        }
        return (W) matrix[from][to];
    }

    @SuppressWarnings("unchecked")
    @Override
    public W putByIndex(int from, int to, W weight) {
        W oldWeight = (W) matrix[from][to];
        matrix[from][to] = weight;
        return oldWeight;
    }

    @SuppressWarnings("unchecked")
    @Override
    public W removeByIndex(int from, int to) {
        if (!containsNodeByIndex(from) || !containsNodeByIndex(to)) {
            return null;
        }
        W oldWeight = (W) matrix[from][to];
        matrix[from][to] = null;
        return oldWeight;
    }

    @Override
    public void clear() {
        clearEdges();
        ArrayHelper.fill(nodes, 0, size, null);
        ArrayHelper.fill(matrix, 0, size, null);
        size = 0;
    }

    @Override
    public void clearEdges() {
        Object[][] matrix = this.matrix;
        for (int i = 0, s = size(); i < s; i++) {
            Object[] row = matrix[i];
            ArrayHelper.fill(row, 0, s, null);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayMatrixGraph<N, W> clone() {
        ArrayMatrixGraph<N, W> clone;
        try {
            clone = (ArrayMatrixGraph<N, W>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        int size = this.size;
        clone.nodes = nodes.clone();
        Object[][] clonedMatrix = matrix.clone();
        for (int i = 0; i < size; i++) {
            clonedMatrix[i] = matrix[i].clone();
        }
        clone.matrix = clonedMatrix;
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayMatrixGraph<?, ?> that = (ArrayMatrixGraph<?, ?>) o;
        int size = this.size;
        if (size != that.size) {
            return false;
        }
        Object[] nodes1 = nodes;
        Object[] nodes2 = that.nodes;
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(nodes1[i], nodes2[i])) {
                return false;
            }
        }
        Object[][] matrix1 = matrix;
        Object[][] matrix2 = that.matrix;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!Objects.equals(matrix1[y][x], matrix2[y][x])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        int size = this.size;
        int hash = size == 0 ? 31 : size;
        for (int i = 0; i < size; i++) {
            Object node = nodes[i];
            hash = hash * 31 + (node == null ? 0 : node.hashCode());
        }
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Object weight = matrix[y][x];
                hash = hash * 31 + (weight == null ? 0 : weight.hashCode());
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        int size = this.size;
        if (size == 0) {
            return "[]";
        }
        Object[] nodes = this.nodes;
        Object[][] matrix = this.matrix;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int y = 0; y < size; y++) {
            boolean empty = true;
            Object from = nodes[y];
            sb.append('{');
            for (int x = 0; x < size; x++) {
                Object weight = matrix[y][x];
                if (weight != null) {
                    empty = false;
                    Object to = nodes[x];
                    sb.append(from).append('-').append(to).append(':').append(weight);
                    sb.append(',').append(' ');
                }
            }
            if (empty) {
                sb.append(from).append('}');
                sb.append(',').append(' ');
            } else {
                sb.insert(sb.length() - 2, '}');
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');
        return sb.toString();
    }
}
