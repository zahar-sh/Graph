package com.example.graph.controller;

import com.example.graph.core.*;
import com.example.graph.exception.ValidationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;
import java.util.function.BiFunction;

public class MainController implements Controller {

    public static final String FXML = "main-view.fxml";

    private static final PseudoClass ERROR_CLASS = PseudoClass.getPseudoClass("error");

    @FXML
    private Pane root;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField addTextField;

    @FXML
    private TextField removeTextField;

    @FXML
    private TextField renameFromTextField;

    @FXML
    private TextField renameToTextField;

    @FXML
    private TextField containsFromTextField;

    @FXML
    private TextField containsToTextField;

    @FXML
    private TextField containsNodeTextField;

    @FXML
    private TextField containsWeightTextField;

    @FXML
    private TextField getFromTextField;

    @FXML
    private TextField getToTextField;

    @FXML
    private TextField putFromTextField;

    @FXML
    private TextField putToTextField;

    @FXML
    private TextField putWeightTextField;

    @FXML
    private TextField removeFromTextField;

    @FXML
    private TextField removeToTextField;

    @FXML
    private TextField getChildrenTextField;

    @FXML
    private TextField getParentsTextField;

    @FXML
    private TextField clearChildrenTextField;

    @FXML
    private TextField clearParentsTextField;

    @FXML
    private Button clearEdgesButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextField pathsFromTextField;

    @FXML
    private TextField pathsToTextField;

    @FXML
    private ComboBox<Path<Integer, Integer>> pathComboBox;

    @FXML
    private TextField minPathFromTextField;

    @FXML
    private TextField minPathToTextField;

    @FXML
    private ToggleButton centerButton;

    @FXML
    private ToggleButton linksButton;

    @FXML
    private TextField nodeWidthTextField;

    @FXML
    private TextField nodeHeightTextField;

    @FXML
    private ColorPicker nodeColorColorPicker;

    @FXML
    private ColorPicker selectedColorPicker;

    @FXML
    private TextField arrowWidthTextField;

    @FXML
    private TextField arrowHeightTextField;

    @FXML
    private TextField arrowPosFactorTextField;

    @FXML
    private TextField weightPosFactorTextField;

    @FXML
    private TextField lineWidthTextField;

    private Graph<Integer, Integer> graph;

    private Comparator<Integer> nodeComparator;

    private Comparator<Integer> weightComparator;

    private BiFunction<Integer, Integer, Integer> sum;

    private BiFunction<Integer, Integer, Integer> subtract;

    private Map<Integer, LinksCount> linksCountMap;

    private Map<Integer, Point> pointMap;

    private Size nodeSize;

    private Collection<Integer> selectedNodes;

    private Font font;

    private Arrow arrow;

    private double arrowPosFactor;

    private Color arrowColor;

    private double weightPosFactor;

    private Color textColor;

    private double lineWidth;

    //  difference between selected node pos and mouse pressed
    private Point delta;

    private Point selectedNodePos;

    private String centerText;

    @FXML
    private void initialize() {
        nodeComparator = Integer::compareTo;
        weightComparator = Integer::compareTo;
        sum = Integer::sum;
        subtract = (a, b) -> a - b;

        Graph<Integer, Integer> graph = new SortedMatrixGraph<>(nodeComparator);
        Integer n1 = 1;
        Integer n2 = 2;
        Integer n3 = 3;
        Integer n4 = 4;
        Integer n5 = 5;
        Integer n6 = 6;

        graph.add(n1);
        graph.add(n2);
        graph.add(n3);
        graph.add(n4);
        graph.add(n5);
        graph.add(n6);

        graph.put(n1, n2, 7);
        graph.put(n1, n3, 9);
        graph.put(n1, n6, 14);

        graph.put(n2, n3, 10);
        graph.put(n2, n4, 15);

        graph.put(n3, n6, 2);
        graph.put(n3, n4, 11);

        graph.put(n4, n5, 6);

        graph.put(n6, n5, 9);
        this.graph = graph;

        linksCountMap = new HashMap<>();

        Map<Integer, Point> pointMap = new HashMap<>();
        pointMap.put(n1, new Point(50, 50));
        pointMap.put(n2, new Point(200, 50));
        pointMap.put(n3, new Point(200, 200));
        pointMap.put(n4, new Point(400, 50));
        pointMap.put(n5, new Point(400, 300));
        pointMap.put(n6, new Point(50, 300));
        this.pointMap = pointMap;

        nodeSize = new Size(50, 50);

        selectedNodes = new ArrayList<>();

        font = Font.font("Consolas", FontWeight.BOLD, 14);
        lineWidth = 1.5;
        arrow = new Arrow(18, 35);
        arrowPosFactor = 0.8;
        arrowColor = Color.RED;
        weightPosFactor = 0.55;
        textColor = Color.BLACK;

        delta = new Point();

        linksButton.setSelected(false);
        pathComboBox.setItems(FXCollections.observableArrayList());
        nodeColorColorPicker.setValue(Color.CHOCOLATE);
        selectedColorPicker.setValue(Color.ORANGE);

        nodeWidthTextField.setText(Integer.toString(nodeSize.getWidth()));
        nodeHeightTextField.setText(Integer.toString(nodeSize.getHeight()));
        arrowWidthTextField.setText(Integer.toString(arrow.getWidth()));
        arrowHeightTextField.setText(Integer.toString(arrow.getHeight()));
        arrowPosFactorTextField.setText(Double.toString(arrowPosFactor));
        weightPosFactorTextField.setText(Double.toString(weightPosFactor));
        lineWidthTextField.setText(Double.toString(lineWidth));

        addTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::add)));
        removeTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::remove)));
        renameFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::rename)));
        renameToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::rename)));
        containsNodeTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::containsNode)));
        containsFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::containsEdge)));
        containsToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::containsEdge)));
        containsWeightTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::containsWeight)));
        getFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::get)));
        getToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::get)));
        putFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::put)));
        putToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::put)));
        putWeightTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::put)));
        removeFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::removeEdge)));
        removeToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::removeEdge)));
        getChildrenTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::selectChildren)));
        getParentsTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::selectParents)));
        clearChildrenTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::clearChildren)));
        clearParentsTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::clearParents)));
        clearEdgesButton.setOnAction(onAction(this::clearEdges));
        clearButton.setOnAction(onAction(this::clear));
        pathsFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::findPaths)));
        pathsToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::findPaths)));
        pathComboBox.setOnAction(onAction(this::selectPath));
        minPathFromTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::findMinPaths)));
        minPathToTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::findMinPaths)));
        centerButton.setOnAction(onAction(() -> {
            if (centerButton.isSelected()) {
                findCenter();
            } else {
                repaint();
            }
        }));
        linksButton.setOnAction(onAction(() -> {
            if (linksButton.isSelected()) {
                countLinks();
            } else {
                repaint();
            }
        }));

        nodeColorColorPicker.setOnAction(onAction(this::repaint));
        selectedColorPicker.setOnAction(onAction(this::repaint));
        nodeWidthTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setNodeWidth)));
        nodeHeightTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setNodeHeight)));
        arrowWidthTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setArrowWidth)));
        arrowHeightTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setArrowHeight)));
        arrowPosFactorTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setArrowPosFactor)));
        weightPosFactorTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setWeightPosFactor)));
        lineWidthTextField.setOnKeyPressed(ifEnterPressed(suppressValidationExceptions(this::setLineWidth)));

        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        pathsFromTextField.setText(Integer.toString(n1));
        pathsToTextField.setText(Integer.toString(n5));

        suppressValidationExceptions(this::findPaths).run();
        //repaint();
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    private Point newPoint() {
        int nodeWidth = nodeSize.getWidth();
        int nodeHeight = nodeSize.getHeight();
        return new Point((int) (canvas.getWidth() - nodeWidth) / 2,
                (int) (canvas.getHeight() - nodeHeight) / 2);
    }

    private void addPath(ObservableList<Path<Integer, Integer>> paths, List<Integer> path) {
        paths.add(new Path<>(path, Graphs.calculateTotalWeight(graph, path, sum)));
    }

    private void invalidateStatistics() {
        linksCountMap.values().forEach(LinksCount::clear);
        pathComboBox.getItems().clear();
        centerText = null;
    }

    private void printMatrix(StringBuilder sb, Matrix<Integer, Integer> matrix) {
        matrix.forEach(row -> {
            printRow(sb, row);
            sb.append('\n');
        });
    }

    private void printRow(StringBuilder sb, Row<Integer, Integer> row) {
        row.forEach(path -> {
            if (path == null) {
                sb.append('-');
            } else {
                sb.append(path.isEmpty() ? '0' : Integer.toString(path.get(0).getTotalWeight()));
            }
            sb.append(' ');
        });
    }

    private void repaint() {
        Platform.runLater(this::paint);
    }

    private void paint() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        context.setLineWidth(lineWidth);
        context.setFont(font);
        context.setStroke(Color.BLACK);

        if (centerButton.isSelected() && centerText != null) {
            context.fillText(centerText, 50, 50);
            return;
        }

        int nodeWidth = nodeSize.getWidth();
        int nodeHeight = nodeSize.getHeight();
        int centerX = nodeWidth / 2;
        int centerY = nodeHeight / 2;

        graph.forEachNode(from -> {
            Point fromPos = pointMap.get(from);
            int x1 = fromPos.getX() + centerX;
            int y1 = fromPos.getY() + centerY;
            graph.forEachChild(from, (f, to, weight) -> {
                Point toPos = pointMap.get(to);
                int x2 = toPos.getX() + centerX;
                int y2 = toPos.getY() + centerY;
                context.strokeLine(x1, y1, x2, y2);
            });
        });

        graph.forEachNode(from -> {
            Point fromPos = pointMap.get(from);
            int x1 = fromPos.getX() + centerX;
            int y1 = fromPos.getY() + centerY;
            graph.forEachChild(from, (f, to, weight) -> {
                Point toPos = pointMap.get(to);
                int x2 = toPos.getX() + centerX;
                int y2 = toPos.getY() + centerY;
                if (Objects.equals(to, f)) {
                    context.strokeOval(x2, y2, nodeWidth, nodeHeight);

                    int arrowY = toPos.getY() + nodeHeight;

                    context.setFill(arrowColor);
                    arrow.draw(context, x2, arrowY, -20);

                    context.setFill(textColor);
                    context.fillText(Integer.toString(weight), arrow.getWidth() + x2 + 10, arrow.getHeight() + arrowY);
                } else {
                    int dx = x2 - x1;
                    int dy = y2 - y1;

                    int arrowX = x1 + (int) (dx * arrowPosFactor);
                    int arrowY = y1 + (int) (dy * arrowPosFactor);

                    double angle = Math.toDegrees(Geometry.radiansOf(x2, y2, x1, y1));

                    context.setFill(arrowColor);
                    arrow.draw(context, arrowX, arrowY, angle);

                    int weightX = x1 + (int) (dx * weightPosFactor);
                    int weightY = y1 + (int) (dy * weightPosFactor);

                    context.setFill(textColor);
                    context.fillText(Integer.toString(weight), weightX, weightY);
                }
            });
        });

        Color nodeColor = nodeColorColorPicker.getValue();
        Color selectedNodeColor = selectedColorPicker.getValue();
        double nodeOffsetX = nodeSize.getWidth() / 2.3;
        double nodeOffsetY = nodeSize.getHeight() / 2.0;
        graph.forEachNode(node -> {
            Point nodePos = pointMap.get(node);
            int x = nodePos.getX();
            int y = nodePos.getY();
            Color color = selectedNodes.contains(node)
                    ? selectedNodeColor
                    : nodeColor;
            context.setFill(color);
            context.fillRect(x, y, nodeWidth, nodeHeight);

            context.setFill(Color.BLACK);
            context.strokeRect(x, y, nodeWidth, nodeHeight);
            context.fillText(Integer.toString(node), x + nodeOffsetX, y + nodeOffsetY);
            if (linksButton.isSelected()) {
                LinksCount linksCount = linksCountMap.get(node);
                if (linksCount != null) {
                    context.fillText(Integer.toString(linksCount.getParents()), x + nodeOffsetX / 2, y + nodeOffsetY / 2);
                    context.fillText(Integer.toString(linksCount.getChildren()), x + nodeOffsetX * 1.5, y + nodeOffsetY * 1.5);
                }
            }
        });
    }

    private void add() throws ValidationException {
        Integer newNode = getInt(addTextField);
        selectedNodes.clear();
        if (graph.add(newNode)) {
            pointMap.put(newNode, newPoint());
            selectedNodes.add(newNode);
            invalidateStatistics();
        }
        repaint();
    }

    private void remove() throws ValidationException {
        Integer node = getInt(removeTextField);
        if (graph.remove(node)) {
            pointMap.remove(node);
            selectedNodes.remove(node);
            invalidateStatistics();
            repaint();
        }
    }

    private void rename() throws ValidationException {
        Integer node = getInt(renameFromTextField);
        Integer newNode = getInt(renameToTextField);
        if (graph.replace(node, newNode)) {
            pointMap.put(newNode, pointMap.remove(node));
            selectedNodes.remove(node);
            selectedNodes.add(newNode);
            invalidateStatistics();
            repaint();
        }
    }

    private void containsNode() throws ValidationException {
        Integer node = getInt(containsNodeTextField);
        selectedNodes.clear();
        if (graph.containsNode(node)) {
            selectedNodes.add(node);
        }
        repaint();
    }

    private void containsEdge() throws ValidationException {
        Integer from = getInt(containsFromTextField);
        Integer to = getInt(containsToTextField);
        selectedNodes.clear();
        if (graph.containsEdge(from, to)) {
            selectedNodes.add(from);
            selectedNodes.add(to);
        }
        repaint();
    }

    private void containsWeight() throws ValidationException {
        Integer weight = getInt(containsWeightTextField);
        graph.containsWeight(weight);
    }

    private void get() throws ValidationException {
        Integer from = getInt(getFromTextField);
        Integer to = getInt(getToTextField);
        Integer weight = graph.get(from, to);
        selectedNodes.clear();
        if (weight != null) {
            selectedNodes.add(from);
            selectedNodes.add(to);
        }
        repaint();
    }

    private void put() throws ValidationException {
        Integer from = getInt(putFromTextField);
        Integer to = getInt(putToTextField);
        Integer weight = getInt(putWeightTextField);
        graph.put(from, to, weight);
        pointMap.computeIfAbsent(from, f -> newPoint());
        pointMap.computeIfAbsent(to, f -> newPoint());
        selectedNodes.clear();
        selectedNodes.add(from);
        selectedNodes.add(to);
        invalidateStatistics();
        repaint();
    }

    private void removeEdge() throws ValidationException {
        Integer from = getInt(removeFromTextField);
        Integer to = getInt(removeToTextField);
        graph.remove(from, to);
        selectedNodes.clear();
        selectedNodes.add(from);
        selectedNodes.add(to);
        invalidateStatistics();
        repaint();
    }

    private void selectChildren() throws ValidationException {
        Integer from = getInt(getChildrenTextField);
        selectedNodes.clear();
        selectedNodes.add(from);
        graph.forEachChild(from, (f, to, weight) -> {
            selectedNodes.add(to);
        });
        repaint();
    }

    private void selectParents() throws ValidationException {
        Integer to = getInt(getParentsTextField);
        selectedNodes.clear();
        selectedNodes.add(to);
        graph.forEachParent(to, (from, t, weight) -> {
            selectedNodes.add(from);
        });
        repaint();
    }

    private void clearChildren() throws ValidationException {
        Integer from = getInt(clearChildrenTextField);
        graph.clearChildren(from);
        selectedNodes.clear();
        selectedNodes.add(from);
        invalidateStatistics();
        repaint();
    }

    private void clearParents() throws ValidationException {
        Integer to = getInt(clearParentsTextField);
        graph.clearParents(to);
        selectedNodes.clear();
        selectedNodes.add(to);
        invalidateStatistics();
        repaint();
    }

    private void clearEdges() {
        graph.clearEdges();
        selectedNodes.clear();
        invalidateStatistics();
        repaint();
    }

    private void clear() {
        graph.clear();
        pointMap.clear();
        selectedNodes.clear();
        invalidateStatistics();
        repaint();
    }

    private void findPaths() throws ValidationException {
        Integer from = getInt(pathsFromTextField);
        Integer to = getInt(pathsToTextField);
        ObservableList<Path<Integer, Integer>> paths = pathComboBox.getItems();
        paths.clear();
        Graphs.findPaths(graph, from, to, path -> {
            addPath(paths, path);
        });
        selectedNodes.clear();
        if (!paths.isEmpty()) {
            selectedNodes.addAll(paths.get(0).getPath());
            pathComboBox.getSelectionModel().select(0);
        }
        repaint();
    }

    private void findMinPaths() throws ValidationException {
        Integer from = getInt(minPathFromTextField);
        Integer to = getInt(minPathToTextField);
        Map<Integer, Integer> minWeights = new HashMap<>();
        Graphs.dijkstra(graph, from, weightComparator, sum, minWeights);
        ObservableList<Path<Integer, Integer>> paths = pathComboBox.getItems();
        paths.clear();
        Graphs.findMinPaths(graph, from, to, minWeights, weightComparator, subtract, path -> {
            addPath(paths, path);
        });
        selectedNodes.clear();
        if (!paths.isEmpty()) {
            selectedNodes.addAll(paths.get(0).getPath());
            pathComboBox.getSelectionModel().select(0);
        }
        repaint();
    }

    private void selectPath() {
        Path<Integer, Integer> selectedPath = pathComboBox.getSelectionModel().getSelectedItem();
        if (selectedPath != null) {
            selectedNodes.clear();
            selectedNodes.addAll(selectedPath.getPath());
            repaint();
        }
    }

    private void findCenter() {
        StringBuilder sb = new StringBuilder();
        String line = "_".repeat(30) + "\n";
        Graphs.findCenter(graph, weightComparator, sum,
                (nodes, matrix, maxRow) -> {
                    sb.append("Nodes\n");
                    sb.append(nodes.toString());
                    sb.append('\n');
                    sb.append(line);
                    sb.append("Matrix\n");
                    printMatrix(sb, matrix);
                    sb.append(line);
                    sb.append("Max row\n");
                    printRow(sb, maxRow);
                    sb.append('\n');
                    sb.append(line);
                },
                (center, paths) -> {
                    sb.append("Center: ").append(center).append("\nPaths:");
                    if (paths.isEmpty()) {
                        sb.append(" []\n").append(line);
                    } else {
                        sb.append('\n');
                        paths.forEach(path -> {
                            sb.append(path.toString()).append('\n').append(line);
                        });
                    }
                });
        centerText = sb.toString();
        repaint();
    }

    private void countLinks() {
        linksCountMap.keySet().retainAll(pointMap.keySet());
        linksCountMap.values().forEach(LinksCount::clear);
        Graphs.countLinks(graph, linksCountMap);
        repaint();
    }

    private void setNodeWidth() throws ValidationException {
        int width = getInt(nodeWidthTextField);
        nodeSize.setWidth(width);
        repaint();
    }

    private void setNodeHeight() throws ValidationException {
        int height = getInt(nodeHeightTextField);
        nodeSize.setHeight(height);
        repaint();
    }

    private void setArrowWidth() throws ValidationException {
        int width = getInt(arrowWidthTextField);
        nodeSize.setWidth(width);
        repaint();
    }

    private void setArrowHeight() throws ValidationException {
        int height = getInt(arrowHeightTextField);
        nodeSize.setHeight(height);
        repaint();
    }

    private void setArrowPosFactor() throws ValidationException {
        arrowPosFactor = getDouble(arrowPosFactorTextField);
        repaint();
    }

    private void setWeightPosFactor() throws ValidationException {
        weightPosFactor = getDouble(weightPosFactorTextField);
        repaint();
    }

    private void setLineWidth() throws ValidationException {
        lineWidth = getDouble(lineWidthTextField);
        repaint();
    }

    private void onMousePressed(MouseEvent mouseEvent) {
        int nodeWidth = nodeSize.getWidth();
        int nodeHeight = nodeSize.getHeight();
        int mouseX = (int) mouseEvent.getX();
        int mouseY = (int) mouseEvent.getY();
        for (Point pos : pointMap.values()) {
            int posX = pos.getX();
            int posY = pos.getY();
            if (Geometry.containsPoint(posX, posY, posX + nodeWidth, posY + nodeHeight, mouseX, mouseY)) {
                selectedNodePos = pos;
                delta.setX(posX - mouseX);
                delta.setY(posY - mouseY);
                break;
            }
        }
    }

    private void onMouseDragged(MouseEvent mouseEvent) {
        if (selectedNodePos != null) {
            int mouseX = (int) mouseEvent.getX();
            int mouseY = (int) mouseEvent.getY();
            selectedNodePos.setX(Geometry.clamp(delta.getX() + mouseX, 0, (int) (canvas.getWidth() - nodeSize.getWidth())));
            selectedNodePos.setY(Geometry.clamp(delta.getY() + mouseY, 0, (int) (canvas.getHeight() - nodeSize.getHeight())));
            repaint();
        }
    }

    private void onMouseReleased(MouseEvent mouseEvent) {
        selectedNodePos = null;
    }

    private static void enableError(Node node) {
        node.pseudoClassStateChanged(ERROR_CLASS, true);
    }

    private static void disableError(Node node) {
        node.pseudoClassStateChanged(ERROR_CLASS, false);
    }

    private static EventHandler<KeyEvent> ifEnterPressed(Runnable action) {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                action.run();
            }
        };
    }

    private static EventHandler<ActionEvent> onAction(Runnable action) {
        return actionEvent -> {
            action.run();
        };
    }

    private static Runnable suppressValidationExceptions(ValidationRunnable action) {
        return () -> {
            try {
                action.run();
            } catch (ValidationException ignored) {
            }
        };
    }

    private static int getInt(TextField textField) throws ValidationException {
        int value;
        try {
            value = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            enableError(textField);
            throw new ValidationException(e);
        }
        disableError(textField);
        return value;
    }

    private static double getDouble(TextField textField) throws ValidationException {
        double value;
        try {
            value = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            enableError(textField);
            throw new ValidationException(e);
        }
        disableError(textField);
        return value;
    }
}