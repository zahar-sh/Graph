package com.example.graph.core;

import java.util.ArrayList;

public class Matrix<N, W> extends ArrayList<Row<N, W>> {

    public Matrix(int initialCapacity) {
        super(initialCapacity);
    }

    public Matrix() {
        super();
    }
}
