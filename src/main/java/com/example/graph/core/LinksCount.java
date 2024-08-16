package com.example.graph.core;

public class LinksCount {

    private int children;

    private int parents;

    public LinksCount() {
    }

    public LinksCount(int children, int parents) {
        this.children = children;
        this.parents = parents;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getParents() {
        return parents;
    }

    public void setParents(int parents) {
        this.parents = parents;
    }

    public void incrementChildren() {
        children++;
    }

    public void incrementParents() {
        parents++;
    }

    public void clear() {
        children = 0;
        parents = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinksCount that = (LinksCount) o;
        return children == that.children && parents == that.parents;
    }

    @Override
    public int hashCode() {
        int result = children;
        result = 31 * result + parents;
        return result;
    }

    @Override
    public String toString() {
        return "LinksCount{" +
                "children=" + children +
                ", parents=" + parents +
                '}';
    }
}

