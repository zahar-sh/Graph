package com.example.graph.controller;

import javafx.scene.canvas.GraphicsContext;

public class Arrow {

    private final double[] xPoints;

    private final double[] yPoints;

    public Arrow() {
        xPoints = new double[3];
        yPoints = new double[3];
    }

    public Arrow(int width, int height) {
        this();
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(int width) {
        xPoints[1] = width >> 1;
        xPoints[2] = -xPoints[1];
    }

    public void setHeight(int height) {
        yPoints[1] = height;
        yPoints[2] = height;
    }

    public int getWidth() {
        return (int) (xPoints[1]) << 1;
    }

    public int getHeight() {
        return (int) yPoints[1];
    }

    public void draw(GraphicsContext context, int x, int y, double angle) {
        context.translate(x, y);
        context.rotate(angle);

        context.fillPolygon(xPoints, yPoints, xPoints.length);

        context.rotate(-angle);
        context.translate(-x, -y);
    }
}