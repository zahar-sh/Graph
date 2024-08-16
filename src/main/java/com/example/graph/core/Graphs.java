package com.example.graph.core;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Graphs {

    public static <N, W> void depthFirstTraversal(Graph<N, W> graph, N start, Consumer<? super N> action) {
        Collection<N> visited = new ArrayList<>();
        Deque<N> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            N node = stack.pop();
            if (!visited.contains(node)) {
                visited.add(node);
                action.accept(node);
                graph.forEachChild(node, (from, to, weight) -> {
                    stack.push(to);
                });
            }
        }
    }

    public static <N, W> void breadthFirstTraversal(Graph<N, W> graph, N start, Consumer<? super N> action) {
        Collection<N> visited = new LinkedHashSet<>();
        Queue<N> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        action.accept(start);
        while (!queue.isEmpty()) {
            N node = queue.poll();
            graph.forEachChild(node, (from, to, weight) -> {
                if (!visited.contains(to)) {
                    visited.add(to);
                    action.accept(to);
                    queue.add(to);
                }
            });
        }
    }

    public static <N, W> void countLinks(Graph<N, W> graph, Map<N, LinksCount> linksCountMap) {
        graph.forEachEdge((from, to, weight) -> {
            linksCountMap.computeIfAbsent(from, n -> new LinksCount()).incrementChildren();
            linksCountMap.computeIfAbsent(to, n -> new LinksCount()).incrementParents();
        });
    }

    public static <N, W> void countLinks(MapGraph<N, W> graph, Map<N, LinksCount> linksCountMap) {
        Map<N, Map<N, W>> nodes = graph.getNodes();
        nodes.forEach((from, edges) -> {
            linksCountMap.computeIfAbsent(from, n -> new LinksCount()).setChildren(edges.size());
        });
        nodes.values().forEach(edges -> {
            edges.keySet().forEach(to -> {
                linksCountMap.get(to).incrementParents();
            });
        });
    }

    public static <N, W> void dijkstra(Graph<N, W> graph,
                                       N node,
                                       Comparator<? super W> comparator,
                                       BiFunction<? super W, ? super W, ? extends W> sum,
                                       Map<N, W> minWeights) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        Deque<PathNode<N>> stack = new ArrayDeque<>(graph.size());
        stack.push(new PathNode<>(null, node));
        do {
            PathNode<N> pathNode = stack.pop();
            N currentNode = pathNode.value;
            W fromWeight = minWeights.get(currentNode);
            graph.forEachChild(currentNode, (from, to, weight) -> {
                if (!contains(pathNode, to)) {
                    PathNode<N> newNode = new PathNode<>(pathNode, to);
                    stack.push(newNode);
                }
                W toWeight = minWeights.get(to);
                W totalWeight = fromWeight == null
                        ? weight
                        : sum.apply(fromWeight, weight);
                int cmp = nullsLast.compare(totalWeight, toWeight);
                if (cmp < 0) {
                    minWeights.put(to, totalWeight);
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> void findPaths(Graph<N, W> graph, N start, N end, Consumer<List<N>> output) {
        Deque<PathNode<N>> stack = new ArrayDeque<>(graph.size());
        stack.push(new PathNode<>(null, end));
        do {
            PathNode<N> pathNode = stack.pop();
            N currentNode = pathNode.value;
            graph.forEachParent(currentNode, (from, to, weight) -> {
                if (from.equals(start)) {
                    List<N> list = new ArrayList<>();
                    list.add(from);
                    list.add(to);
                    for (PathNode<N> n = pathNode.prev; n != null; n = n.prev) {
                        list.add(n.value);
                    }
                    output.accept(list);
                } else {
                    if (!contains(pathNode, from)) {
                        PathNode<N> newNode = new PathNode<>(pathNode, from);
                        stack.push(newNode);
                    }
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> void findMinPaths(Graph<N, W> graph,
                                           N start,
                                           N end,
                                           Map<N, W> minWeights,
                                           Comparator<? super W> comparator,
                                           BiFunction<? super W, ? super W, ? extends W> subtract,
                                           Consumer<List<N>> output) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        Deque<PathNode<N>> stack = new ArrayDeque<>(graph.size());
        stack.push(new PathNode<>(null, end));
        do {
            PathNode<N> pathNode = stack.pop();
            N currentNode = pathNode.value;
            W totalWeight = minWeights.get(currentNode);
            graph.forEachParent(currentNode, (from, to, weight) -> {
                W expectedWeight = minWeights.get(from);
                int cmp;
                if (expectedWeight == null) {
                    cmp = nullsLast.compare(totalWeight, weight);
                } else {
                    W actualWeight = subtract.apply(totalWeight, weight);
                    cmp = nullsLast.compare(actualWeight, expectedWeight);
                }
                if (cmp == 0) {
                    if (from.equals(start)) {
                        List<N> list = new ArrayList<>();
                        list.add(from);
                        list.add(to);
                        for (PathNode<N> n = pathNode.prev; n != null; n = n.prev) {
                            list.add(n.value);
                        }
                        output.accept(list);
                    } else {
                        if (!contains(pathNode, from)) {
                            PathNode<N> newNode = new PathNode<>(pathNode, from);
                            stack.push(newNode);
                        }
                    }
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> W calculateTotalWeight(Graph<N, W> graph, List<N> path, BiFunction<? super W, ? super W, ? extends W> sum) {
        if (path.isEmpty()) {
            return null;
        }
        W total = null;
        N prev = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            N next = path.get(i);
            W weight = graph.get(prev, next);
            total = total == null
                    ? weight
                    : weight == null
                    ? null
                    : sum.apply(total, weight);
            prev = next;
        }
        return total;
    }


    public static <N, W> void findCenter(Graph<N, W> graph,
                                         Comparator<? super W> comparator,
                                         BiFunction<? super W, ? super W, ? extends W> sum,
                                         FindCenterConsumer<N, W> findCenterConsumer,
                                         BiConsumer<? super N, ? super Cell<N, W>> output) {
        if (graph.isEmpty()) {
            return;
        }
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        List<N> nodes = new ArrayList<>();
        graph.forEachNode(nodes::add);
        Matrix<N, W> matrix = new Matrix<>(nodes.size());
        Row<N, W> max = new Row<>(nodes.size());
        buildMatrix(graph, matrix, nodes, nullsLast, sum);
        buildRows(matrix, max, nullsLast);
        if (findCenterConsumer != null) {
            findCenterConsumer.accept(nodes, matrix, max);
        }
        findMinRow(nodes, max, nullsLast, output);
    }

    private static <N, W> void findMinRow(List<N> nodes,
                                          Row<N, W> maxRow,
                                          Comparator<? super W> nullsLast,
                                          BiConsumer<? super N, ? super Cell<N, W>> output) {
        W min = null;
        for (Cell<N, W> minPaths : maxRow) {
            if (minPaths != null) {
                W w = minPaths.isEmpty()
                        ? null
                        : minPaths.get(0).getTotalWeight();
                int cmp = nullsLast.compare(min, w);
                if (cmp < 0) {
                    min = w;
                }
            }
        }

        final W constMin = min;
        Predicate<Cell<N, W>> predicate = paths -> {
            return paths != null && (!paths.isEmpty() && constMin == paths.get(0).getTotalWeight());
        };
        for (int i = 0; i < maxRow.size(); i++) {
            Cell<N, W> cell = maxRow.get(i);
            if (predicate.test(cell)) {
                output.accept(nodes.get(i), cell);
            }
        }
    }

    private static <N, W> void buildRows(Matrix<N, W> matrix, Row<N, W> max, Comparator<? super W> nullsLast) {
        max.addAll(matrix.get(0));
        for (Row<N, W> row : matrix) {
            for (int x = 0; x < row.size(); x++) {
                Cell<N, W> minPaths = row.get(x);
                Cell<N, W> paths = max.get(x);
                if (minPaths != null && !minPaths.isEmpty()) {
                    if (paths.isEmpty()) {
                        max.set(x, minPaths);
                    } else {
                        W minWeight = minPaths.get(0).getTotalWeight();
                        W currentWeight = paths.get(0).getTotalWeight();
                        int cmp = nullsLast.compare(currentWeight, minWeight);
                        if (cmp > 0) {
                            max.set(x, minPaths);
                        }
                    }
                }
            }
        }
    }

    private static <N, W> void buildMatrix(Graph<N, W> graph,
                                           Matrix<N, W> matrix,
                                           List<N> nodes,
                                           Comparator<? super W> nullsLast,
                                           BiFunction<? super W, ? super W, ? extends W> sum) {
        for (N from : nodes) {
            Row<N, W> row = new Row<>();
            matrix.add(row);

            for (N to : nodes) {
                Cell<N, W> paths = new Cell<>();
                findPaths(graph, from, to, path -> {
                    if (path != null) {
                        paths.add(new Path<>(path, calculateTotalWeight(graph, path, sum)));
                    }
                });

                if (paths.isEmpty()) {
                    row.add(Objects.equals(from, to) ? paths : null);
                } else {
                    W minWeight = paths.get(0).getTotalWeight();
                    for (int i = 1, size = paths.size(); i < size; i++) {
                        W currentWeight = paths.get(i).getTotalWeight();
                        int cmp = nullsLast.compare(currentWeight, minWeight);
                        if (cmp > 0) {
                            paths.removeRange(i, size);
                            break;
                        }
                    }
                    //paths.removeIf(p -> p.getTotalWeight() > min.getTotalWeight());
                    row.add(paths);
                }
            }
        }
    }

    public static <N, W> void getLinksCount(MatrixGraph<N, W> graph, LinksCount[] linksCounts) {
        graph.forEachEdgeByIndex((from, to, weight) -> {
            linksCounts[from].incrementChildren();
            linksCounts[to].incrementParents();
        });
    }

    public static <N, W> void dijkstra(MatrixGraph<N, W> graph,
                                       int index,
                                       Comparator<? super W> comparator,
                                       BiFunction<? super W, ? super W, ? extends W> sum,
                                       W[] minWeights) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        Deque<IndexedPathNode> stack = new ArrayDeque<>(graph.size());
        stack.push(new IndexedPathNode(null, index));
        do {
            IndexedPathNode pathNode = stack.pop();
            int currentIndex = pathNode.value;
            W fromWeight = minWeights[currentIndex];
            graph.forEachChildByIndex(currentIndex, (from, to, weight) -> {
                if (!contains(pathNode, to)) {
                    IndexedPathNode newNode = new IndexedPathNode(pathNode, to);
                    stack.push(newNode);
                }
                W toWeight = minWeights[to];
                W totalWeight = fromWeight == null
                        ? weight
                        : sum.apply(fromWeight, weight);
                int cmp = nullsLast.compare(totalWeight, toWeight);
                if (cmp < 0) {
                    minWeights[to] = totalWeight;
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> void findPaths(MatrixGraph<N, W> graph, int start, int end, Consumer<int[]> output) {
        Deque<IndexedPathNode> stack = new ArrayDeque<>(graph.size());
        stack.push(new IndexedPathNode(null, end));
        do {
            IndexedPathNode pathNode = stack.pop();
            int currentIndex = pathNode.value;
            graph.forEachParentByIndex(currentIndex, (from, to, weight) -> {
                if (from == start) {
                    int len = 2;
                    for (IndexedPathNode n = pathNode.prev; n != null; n = n.prev) {
                        len++;
                    }
                    int[] array = new int[len];
                    array[0] = from;
                    array[1] = to;
                    int destIndex = 2;
                    for (IndexedPathNode n = pathNode.prev; n != null; n = n.prev) {
                        array[destIndex++] = n.value;
                    }
                    output.accept(array);
                } else {
                    if (!contains(pathNode, from)) {
                        IndexedPathNode newNode = new IndexedPathNode(pathNode, from);
                        stack.push(newNode);
                    }
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> void findMinPaths(MatrixGraph<N, W> graph,
                                           int start,
                                           int end,
                                           W[] minWeights,
                                           Comparator<? super W> comparator,
                                           BiFunction<? super W, ? super W, ? extends W> subtract,
                                           Consumer<int[]> output) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        Deque<IndexedPathNode> stack = new ArrayDeque<>(graph.size());
        stack.push(new IndexedPathNode(null, end));
        do {
            IndexedPathNode pathNode = stack.pop();
            int currentIndex = pathNode.value;
            W totalWeight = minWeights[currentIndex];
            graph.forEachParentByIndex(currentIndex, (from, to, weight) -> {
                W expectedWeight = minWeights[from];
                int cmp;
                if (expectedWeight == null) {
                    cmp = nullsLast.compare(totalWeight, weight);
                } else {
                    W actualWeight = subtract.apply(totalWeight, weight);
                    cmp = nullsLast.compare(actualWeight, expectedWeight);
                }
                if (cmp == 0) {
                    if (from == start) {
                        int len = 2;
                        for (IndexedPathNode n = pathNode.prev; n != null; n = n.prev) {
                            len++;
                        }
                        int[] array = new int[len];
                        array[0] = from;
                        array[1] = to;
                        int destIndex = 2;
                        for (IndexedPathNode n = pathNode.prev; n != null; n = n.prev) {
                            array[destIndex++] = n.value;
                        }
                        output.accept(array);
                    } else {
                        if (!contains(pathNode, from)) {
                            IndexedPathNode newNode = new IndexedPathNode(pathNode, from);
                            stack.push(newNode);
                        }
                    }
                }
            });
        } while (!stack.isEmpty());
    }

    public static <N, W> W calculateTotalWeight(MatrixGraph<N, W> graph, int[] path, BiFunction<? super W, ? super W, ? extends W> sum) {
        if (path.length == 0) {
            return null;
        }
        W total = null;
        int prev = path[0];
        for (int i = 1; i < path.length; i++) {
            int next = path[i];
            W weight = graph.getByIndex(prev, next);
            if (weight != null) {
                total = total == null
                        ? weight
                        : sum.apply(total, weight);
            }
            prev = next;
        }
        return total;
    }


    public static <N, W> void countLinks(MatrixGraph<N, W> graph, Map<N, LinksCount> linksCountMap) {
        int size = graph.size();
        LinksCount[] linksCounts = new LinksCount[size];
        getLinksCount(graph, linksCounts);
        for (int i = 0; i < size; i++) {
            N node = graph.getNode(i);
            linksCountMap.put(node, linksCounts[i]);
        }
    }

    public static <N, W> void dijkstra(MatrixGraph<N, W> graph, N node, Comparator<? super W> comparator,
                                       BiFunction<? super W, ? super W, ? extends W> sum,
                                       Map<N, W> minWeights) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        int index = graph.indexOf(node);
        int size = graph.size();
        @SuppressWarnings("unchecked")
        W[] minWeightsArray = (W[]) new Object[size];
        dijkstra(graph, index, nullsLast, sum, minWeightsArray);
        for (int i = 0; i < size; i++) {
            N n = graph.getNode(i);
            W w = minWeightsArray[i];
            minWeights.put(n, w);
        }
    }

    public static <N, W> void findPaths(MatrixGraph<N, W> graph, N from, N to, Consumer<List<N>> output) {
        int startIndex = graph.indexOf(from);
        int endIndex = graph.indexOf(to);
        Consumer<int[]> pathConsumer = indexes -> {
            List<N> path = new ArrayList<>(indexes.length);
            for (int index : indexes) {
                path.add(graph.getNode(index));
            }
            output.accept(path);
        };
        findPaths(graph, startIndex, endIndex, pathConsumer);
    }

    public static <N, W> void findMinPaths(MatrixGraph<N, W> graph,
                                           N from,
                                           N to,
                                           Map<N, W> minWeights,
                                           Comparator<? super W> comparator,
                                           BiFunction<? super W, ? super W, ? extends W> subtract,
                                           Consumer<List<N>> output) {
        Comparator<? super W> nullsLast = Comparator.nullsLast(comparator);
        int startIndex = graph.indexOf(from);
        int endIndex = graph.indexOf(to);
        @SuppressWarnings("unchecked")
        W[] minWeightsArray = (W[]) new Object[minWeights.size()];
        minWeights.forEach((node, totalWeight) -> {
            int index = graph.indexOf(node);
            minWeightsArray[index] = totalWeight;
        });
        Consumer<int[]> pathConsumer = indexes -> {
            List<N> path = new ArrayList<>(indexes.length);
            for (int index : indexes) {
                path.add(graph.getNode(index));
            }
            output.accept(path);
        };
        findMinPaths(graph, startIndex, endIndex, minWeightsArray, nullsLast, subtract, pathConsumer);
    }

    public static <N, W> W calculateTotalWeight(MatrixGraph<N, W> graph, List<N> path, BiFunction<? super W, ? super W, ? extends W> sum) {
        int size = path.size();
        int[] pathArray = new int[size];
        for (int i = 0; i < size; i++) {
            pathArray[i] = graph.indexOf(path.get(i));
        }
        return calculateTotalWeight(graph, pathArray, sum);
    }

    private static <N> boolean contains(PathNode<N> pathNode, N node) {
        while (pathNode != null) {
            if (node.equals(pathNode.value)) {
                return true;
            }
            pathNode = pathNode.prev;
        }
        return false;
    }

    private static boolean contains(IndexedPathNode pathNode, int index) {
        while (pathNode != null) {
            if (index == pathNode.value) {
                return true;
            }
            pathNode = pathNode.prev;
        }
        return false;
    }

    private static class PathNode<T> {
        private final PathNode<T> prev;
        private final T value;

        PathNode(PathNode<T> prev, T value) {
            this.prev = prev;
            this.value = value;
        }
    }

    private static class IndexedPathNode {
        private final IndexedPathNode prev;
        private final int value;

        IndexedPathNode(IndexedPathNode prev, int value) {
            this.prev = prev;
            this.value = value;
        }
    }
}
