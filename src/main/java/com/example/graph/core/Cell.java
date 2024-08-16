package com.example.graph.core;

import java.util.ArrayList;

public class Cell<N, W> extends ArrayList<Path<N, W>> {

    public Cell(int initialCapacity) {
        super(initialCapacity);
    }

    public Cell() {
        super();
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }
}
