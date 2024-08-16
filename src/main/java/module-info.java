module com.example.graph {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.graph to javafx.fxml;
    exports com.example.graph;
    exports com.example.graph.core;
    exports com.example.graph.controller;
    exports com.example.graph.exception;
    opens com.example.graph.controller to javafx.fxml;
    opens com.example.graph.core to javafx.fxml;
}