package com.example.graph.core;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public interface MatrixGraph<N, W> extends Graph<N, W> {

    boolean isEmptyChildrenByIndex(int from);

    boolean isEmptyParentsByIndex(int to);

    boolean containsNodeByIndex(int index);

    boolean containsEdgeByIndex(int from, int to);

    int indexOf(N node);

    N getNode(int index);

    N setNode(int index, N node);

    boolean removeByIndex(int index);

    W getByIndex(int from, int to);

    W putByIndex(int from, int to, W weight);

    W removeByIndex(int from, int to);

    void clearChildrenByIndex(int from);

    void clearParentsByIndex(int to);

    void replaceAllByIndex(IndexedEdgeFunction<? super W, ? extends W> function);

    boolean removeNodeByIndexIf(IntPredicate filter);

    boolean removeEdgeByIndexIf(IndexedEdgePredicate<? super W> filter);

    boolean removeChildByIndexIf(int from, IndexedEdgePredicate<? super W> filter);

    boolean removeParentByIndexIf(int to, IndexedEdgePredicate<? super W> filter);

    void forEachEdgeByIndex(IndexedEdgeConsumer<? super W> action);

    void forEachEdgeByIndex(IndexedEdgeConsumer<? super W> edgeConsumer, IntConsumer emptyNodeConsumer);

    void forEachChildByIndex(int from, IndexedEdgeConsumer<? super W> action);

    void forEachParentByIndex(int to, IndexedEdgeConsumer<? super W> action);

    W getOrDefaultByIndex(int from, int to, W eight);
}
