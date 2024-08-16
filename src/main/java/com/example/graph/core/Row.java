package com.example.graph.core;

import java.util.ArrayList;

public class Row<N, W> extends ArrayList<Cell<N, W>> {

    public Row(int initialCapacity) {
        super(initialCapacity);
    }

    public Row() {
        super();
    }
}
