package com.example.graph.core;

import java.util.List;

public interface FindCenterConsumer<N, W> {
    void accept(List<N> nodes, Matrix<N, W> matrix, Row<N, W> maxRow);
}
