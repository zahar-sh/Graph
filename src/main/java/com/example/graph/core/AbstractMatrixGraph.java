package com.example.graph.core;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

abstract class AbstractMatrixGraph<N, W> extends AbstractGraph<N, W> implements MatrixGraph<N, W> {

    @Override
    public boolean isEmptyChildrenByIndex(int from) {
        for (int x = 0, s = size(); x < s; x++) {
            W w = getByIndex(from, x);
            if (w != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmptyParentsByIndex(int to) {
        for (int y = 0, s = size(); y < s; y++) {
            W w = getByIndex(y, to);
            if (w != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsNodeByIndex(int index) {
        return index >= 0 && index < size();
    }

    @Override
    public boolean containsEdgeByIndex(int from, int to) {
        return getByIndex(from, to) != null;
    }


    @Override
    public void clearChildrenByIndex(int from) {
        for (int x = 0, s = size(); x < s; x++) {
            removeByIndex(from, x);
        }
    }

    @Override
    public void clearParentsByIndex(int to) {
        for (int y = 0, s = size(); y < s; y++) {
            removeByIndex(y, to);
        }
    }

    @Override
    public void replaceAllByIndex(IndexedEdgeFunction<? super W, ? extends W> function) {
        for (int y = 0, s = size(); y < s; y++) {
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    W newWeight = function.apply(y, x, w);
                    putByIndex(y, x, newWeight);
                }
            }
        }
    }

    @Override
    public boolean removeNodeByIndexIf(IntPredicate filter) {
        boolean removed = false;
        for (int i = size() - 1; i >= 0; i--) {
            if (filter.test(i)) {
                removed = true;
                removeByIndex(i);
            }
        }
        return removed;
    }

    @Override
    public boolean removeEdgeByIndexIf(IndexedEdgePredicate<? super W> filter) {
        boolean removed = false;
        for (int y = 0, s = size(); y < s; y++) {
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    if (filter.test(y, x, w)) {
                        removed = true;
                        removeByIndex(y, x);
                    }
                }
            }
        }
        return removed;
    }

    @Override
    public boolean removeChildByIndexIf(int from, IndexedEdgePredicate<? super W> filter) {
        boolean removed = false;
        for (int x = 0, s = size(); x < s; x++) {
            W w = getByIndex(from, x);
            if (w != null) {
                if (filter.test(from, x, w)) {
                    removed = true;
                    removeByIndex(from, x);
                }
            }
        }
        return removed;
    }

    @Override
    public boolean removeParentByIndexIf(int to, IndexedEdgePredicate<? super W> filter) {
        boolean removed = false;
        for (int y = 0, s = size(); y < s; y++) {
            W w = getByIndex(y, to);
            if (w != null) {
                if (filter.test(y, to, w)) {
                    removed = true;
                    removeByIndex(y, to);
                }
            }
        }
        return removed;
    }

    @Override
    public void forEachEdgeByIndex(IndexedEdgeConsumer<? super W> action) {
        for (int y = 0, s = size(); y < s; y++) {
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    action.accept(y, x, w);
                }
            }
        }
    }

    @Override
    public void forEachEdgeByIndex(IndexedEdgeConsumer<? super W> edgeConsumer, IntConsumer emptyNodeConsumer) {
        for (int y = 0, s = size(); y < s; y++) {
            boolean empty = true;
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    empty = false;
                    edgeConsumer.accept(y, x, w);
                }
            }
            if (empty) {
                emptyNodeConsumer.accept(y);
            }
        }
    }

    @Override
    public void forEachChildByIndex(int from, IndexedEdgeConsumer<? super W> action) {
        for (int x = 0, s = size(); x < s; x++) {
            W w = getByIndex(from, x);
            if (w != null) {
                action.accept(from, x, w);
            }
        }
    }

    @Override
    public void forEachParentByIndex(int to, IndexedEdgeConsumer<? super W> action) {
        for (int y = 0, s = size(); y < s; y++) {
            W w = getByIndex(y, to);
            if (w != null) {
                action.accept(y, to, w);
            }
        }
    }

    @Override
    public W getOrDefaultByIndex(int from, int to, W defaultWeight) {
        W oldWeight = getByIndex(from, to);
        if (oldWeight != null) {
            return oldWeight;
        }
        return defaultWeight;
    }

    @Override
    public boolean isEmptyEdges() {
        for (int y = 0, s = size(); y < s; y++) {
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isEmptyChildren(N from) {
        return isEmptyChildrenByIndex(indexOf(from));
    }

    @Override
    public boolean isEmptyParents(N to) {
        return isEmptyParentsByIndex(indexOf(to));
    }

    @Override
    public boolean containsNode(N node) {
        return containsNodeByIndex(indexOf(node));
    }

    @Override
    public boolean containsEdge(N from, N to) {
        return containsEdgeByIndex(indexOf(from), indexOf(to));
    }

    @Override
    public boolean containsWeight(W weight) {
        for (int y = 0, s = size(); y < s; y++) {
            for (int x = 0; x < s; x++) {
                W w = getByIndex(y, x);
                if (w != null) {
                    if (w.equals(weight)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        setNode(nodeIndex, newNode);
        return true;
    }

    @Override
    public boolean remove(N node) {
        return removeByIndex(indexOf(node));
    }

    @Override
    public W get(N from, N to) {
        return getByIndex(indexOf(from), indexOf(to));
    }

    @Override
    public W put(N from, N to, W weight) {
        add(from);
        add(to);
        return putByIndex(indexOf(from), indexOf(to), weight);
    }

    @Override
    public W remove(N from, N to) {
        return removeByIndex(indexOf(from), indexOf(to));
    }

    @Override
    public void clearChildren(N from) {
        clearChildrenByIndex(indexOf(from));
    }

    @Override
    public void clearParents(N to) {
        clearParentsByIndex(indexOf(to));
    }

    @Override
    public void replaceAll(EdgeFunction<? super N, ? super W, ? extends W> function) {
        replaceAllByIndex(createFunction(function));
    }

    @Override
    public boolean removeNodeIf(Predicate<? super N> filter) {
        return removeNodeByIndexIf(index -> filter.test(getNode(index)));
    }

    @Override
    public boolean removeEdgeIf(EdgePredicate<? super N, ? super W> filter) {
        return removeEdgeByIndexIf(createPredicate(filter));
    }

    @Override
    public boolean removeChildIf(N from, EdgePredicate<? super N, ? super W> filter) {
        return removeChildByIndexIf(indexOf(from), createPredicate(filter));
    }

    @Override
    public boolean removeParentIf(N to, EdgePredicate<? super N, ? super W> filter) {
        return removeParentByIndexIf(indexOf(to), createPredicate(filter));
    }

    @Override
    public void forEachNode(Consumer<? super N> action) {
        for (int i = 0, s = size(); i < s; i++) {
            N node = getNode(i);
            action.accept(node);
        }
    }

    @Override
    public void forEachEdge(EdgeConsumer<? super N, ? super W> action) {
        forEachEdgeByIndex(createConsumer(action));
    }

    @Override
    public void forEachEdge(EdgeConsumer<? super N, ? super W> edgeConsumer, Consumer<? super N> emptyNodeConsumer) {
        forEachEdgeByIndex(createConsumer(edgeConsumer), index -> emptyNodeConsumer.accept(getNode(index)));
    }

    @Override
    public void forEachChild(N from, EdgeConsumer<? super N, ? super W> action) {
        forEachChildByIndex(indexOf(from), createConsumer(action));
    }

    @Override
    public void forEachParent(N to, EdgeConsumer<? super N, ? super W> action) {
        forEachParentByIndex(indexOf(to), createConsumer(action));
    }

    @Override
    public void putAll(Graph<? extends N, ? extends W> graph) {
        graph.forEachNode(this::add);
        graph.forEachEdge((from, to, weight) -> {
            putByIndex(indexOf(from), indexOf(to), weight);
        });
    }

    @Override
    public W getOrDefault(N from, N to, W weight) {
        return getOrDefaultByIndex(indexOf(from), indexOf(to), weight);
    }

    protected <R> IndexedEdgeFunction<W, R> createFunction(EdgeFunction<? super N, ? super W, ? extends R> action) {
        return (from, to, weight) -> {
            return action.apply(getNode(from), getNode(to), weight);
        };
    }

    protected IndexedEdgeConsumer<W> createConsumer(EdgeConsumer<? super N, ? super W> action) {
        return (from, to, weight) -> {
            action.accept(getNode(from), getNode(to), weight);
        };
    }

    protected IndexedEdgePredicate<W> createPredicate(EdgePredicate<? super N, ? super W> action) {
        return (from, to, weight) -> {
            return action.test(getNode(from), getNode(to), weight);
        };
    }
}
