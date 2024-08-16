package com.example.graph.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MapGraph<N, W> extends AbstractGraph<N, W> implements Cloneable {

    private Map<N, Map<N, W>> nodes;

    private final Function<N, Map<N, W>> edgesCreator;

    public MapGraph() {
        this(new HashMap<>(), from -> new HashMap<>());
    }

    public MapGraph(Comparator<? super N> comparator) {
        this(new TreeMap<>(comparator), from -> new TreeMap<>(comparator));
    }

    public MapGraph(Map<N, Map<N, W>> nodes, Function<N, Map<N, W>> edgesCreator) {
        this.nodes = nodes;
        this.edgesCreator = edgesCreator;
    }

    public Map<N, Map<N, W>> getNodes() {
        return nodes;
    }

    private Map<N, W> createEdges(N from) {
        return nodes.computeIfAbsent(from, edgesCreator);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean isEmptyEdges() {
        for (Map<N, W> edges : nodes.values()) {
            if (!edges.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmptyChildren(N from) {
        Map<N, W> edges = nodes.get(from);
        return edges == null || edges.isEmpty();
    }

    @Override
    public boolean isEmptyParents(N to) {
        for (Map<N, W> edges : nodes.values()) {
            if (edges.containsKey(to)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsNode(N node) {
        return nodes.containsKey(node);
    }

    @Override
    public boolean containsEdge(N from, N to) {
        Map<N, W> edges = nodes.get(from);
        return edges != null && edges.containsKey(to);
    }

    @Override
    public boolean containsWeight(W weight) {
        for (Map<N, W> edges : nodes.values()) {
            if (edges.containsValue(weight)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(N node) {
        createEdges(node);
        return false;
    }

    @Override
    public boolean replace(N node, N newNode) {
        nodes.values().forEach(edges -> edges.put(newNode, edges.remove(node)));
        return nodes.put(newNode, nodes.remove(node)) != null;
    }

    @Override
    public boolean remove(N node) {
        nodes.values().forEach(edges -> edges.remove(node));
        return nodes.remove(node) != null;
    }

    @Override
    public W get(N from, N to) {
        Map<N, W> edges = nodes.get(from);
        return edges != null
                ? edges.get(to)
                : null;
    }

    @Override
    public W put(N from, N to, W weight) {
        Map<N, W> edges = createEdges(from);
        createEdges(to);
        return edges.put(to, weight);
    }

    @Override
    public W remove(N from, N to) {
        Map<N, W> edges = nodes.get(from);
        return edges != null
                ? edges.remove(to)
                : null;
    }

    @Override
    public void clear() {
        clearEdges();
        nodes.clear();
    }

    @Override
    public void clearEdges() {
        nodes.values().forEach(Map::clear);
    }

    @Override
    public void clearChildren(N from) {
        Map<N, W> edges = nodes.get(from);
        if (edges != null) {
            edges.clear();
        }
    }

    @Override
    public void clearParents(N to) {
        nodes.values().forEach(edges -> {
            edges.entrySet().removeIf(edge -> {
                N t = edge.getKey();
                return t.equals(to);
            });
        });
    }

    @Override
    public void replaceAll(EdgeFunction<? super N, ? super W, ? extends W> function) {
        nodes.forEach((from, edges) -> {
            edges.replaceAll((to, weight) -> {
                return function.apply(from, to, weight);
            });
        });
    }

    @Override
    public boolean removeNodeIf(Predicate<? super N> filter) {
        Predicate<Map.Entry<N, ?>> predicate = node -> filter.test(node.getKey());
        nodes.values().forEach(edges -> edges.entrySet().removeIf(predicate));
        return nodes.entrySet().removeIf(predicate);
    }

    private boolean removeEdgeIf(N from, Map<N, W> edges, EdgePredicate<? super N, ? super W> filter) {
        return edges.entrySet().removeIf(edge -> {
            N to = edge.getKey();
            W weight = edge.getValue();
            return filter.test(from, to, weight);
        });
    }

    @Override
    public boolean removeEdgeIf(EdgePredicate<? super N, ? super W> filter) {
        boolean removed = false;
        for (Map.Entry<N, Map<N, W>> node : nodes.entrySet()) {
            N from = node.getKey();
            Map<N, W> edges = node.getValue();
            removed = removed || removeEdgeIf(from, edges, filter);
        }
        return removed;
    }

    @Override
    public boolean removeChildIf(N from, EdgePredicate<? super N, ? super W> filter) {
        Map<N, W> edges = nodes.get(from);
        return edges != null && removeEdgeIf(from, edges, filter);
    }

    @Override
    public boolean removeParentIf(N to, EdgePredicate<? super N, ? super W> filter) {
        boolean removed = false;
        for (Map.Entry<N, Map<N, W>> node : nodes.entrySet()) {
            N from = node.getKey();
            Map<N, W> edges = node.getValue();
            removed = removed || removeEdgeIf(from, edges, (f, t, weight) -> {
                return t.equals(to) && filter.test(f, t, weight);
            });
        }
        return removed;
    }

    @Override
    public void forEachNode(Consumer<? super N> action) {
        nodes.keySet().forEach(action);
    }

    @Override
    public void forEachEdge(EdgeConsumer<? super N, ? super W> action) {
        nodes.forEach((from, edges) -> {
            edges.forEach((to, weight) -> {
                action.accept(from, to, weight);
            });
        });
    }

    @Override
    public void forEachEdge(EdgeConsumer<? super N, ? super W> edgeConsumer, Consumer<? super N> emptyNodeConsumer) {
        nodes.forEach((from, edges) -> {
            if (edges.isEmpty()) {
                emptyNodeConsumer.accept(from);
            } else {
                edges.forEach((to, weight) -> {
                    edgeConsumer.accept(from, to, weight);
                });
            }
        });
    }

    @Override
    public void forEachChild(N from, EdgeConsumer<? super N, ? super W> action) {
        Map<N, W> edges = nodes.get(from);
        if (edges != null) {
            edges.forEach((to, weight) -> {
                action.accept(from, to, weight);
            });
        }
    }

    @Override
    public void forEachParent(N to, EdgeConsumer<? super N, ? super W> action) {
        nodes.forEach((from, edges) -> {
            if (edges.containsKey(to)) {
                edges.forEach((t, weight) -> {
                    action.accept(from, t, weight);
                });
            }
        });
    }

    @Override
    public void forEachWeight(Consumer<? super W> action) {
        nodes.values().forEach(edges -> edges.values().forEach(action));
    }

    @Override
    public void putAll(Graph<? extends N, ? extends W> graph) {
        if (graph instanceof MapGraph) {
            MapGraph<? extends N, ? extends W> mapGraph = (MapGraph<? extends N, ? extends W>) graph;
            mapGraph.nodes.forEach((from, edges) -> {
                createEdges(from).putAll(edges);
            });
        } else {
            super.putAll(graph);
        }
    }

    @Override
    public W getOrDefault(N from, N to, W weight) {
        Map<N, W> edges = nodes.get(from);
        return edges != null
                ? edges.getOrDefault(to, weight)
                : weight;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapGraph<N, W> clone() {
        MapGraph<N, W> clone;
        try {
            clone = (MapGraph<N, W>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        Map<N, Map<N, W>> clonedNodes;
        if (nodes instanceof HashMap) {
            HashMap<N, Map<N, W>> hashMap = (HashMap<N, Map<N, W>>) nodes;
            clonedNodes = (Map<N, Map<N, W>>) hashMap.clone();
            clonedNodes.replaceAll((from, edges) -> {
                return (Map<N, W>) ((HashMap<N, W>) edges).clone();
            });
        } else {
            TreeMap<N, Map<N, W>> treeMap = (TreeMap<N, Map<N, W>>) nodes;
            clonedNodes = (Map<N, Map<N, W>>) treeMap.clone();
            clonedNodes.replaceAll((from, edges) -> {
                return (Map<N, W>) ((TreeMap<N, W>) edges).clone();
            });
        }
        clone.nodes = clonedNodes;
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapGraph<?, ?> mapGraph = (MapGraph<?, ?>) o;
        return nodes.equals(mapGraph.nodes);
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        nodes.forEach((from, edges) -> {
            if (edges.isEmpty()) {
                sb.append('{').append(from).append('}');
                sb.append(',').append(' ');
            } else {
                sb.append('{');
                edges.forEach((to, weight) -> {
                    sb.append(from).append('-').append(to).append(':').append(weight);
                    sb.append(',').append(' ');
                });
                sb.insert(sb.length() - 2, '}');
            }
        });
        sb.setLength(sb.length() - 2);
        sb.append(']');
        return sb.toString();
    }
}
