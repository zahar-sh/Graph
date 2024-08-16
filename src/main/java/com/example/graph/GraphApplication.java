package com.example.graph;

import com.example.graph.controller.MainController;
import com.example.graph.exception.LoadingException;
import com.example.graph.loader.ControllerLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphApplication extends Application {
    @Override
    public void start(Stage stage) throws LoadingException {
        ControllerLoader controllerLoader = new ControllerLoader(GraphApplication.class);
        MainController controller = controllerLoader.load(MainController.FXML);
        Scene scene = new Scene(controller.getRoot());
        stage.setTitle("Graph viewer");
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}