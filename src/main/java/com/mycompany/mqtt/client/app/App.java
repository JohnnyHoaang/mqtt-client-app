package com.mycompany.mqtt.client.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException{
//        launch();
    System.out.println("started");
    MotionSensorApp motion = new MotionSensorApp();
//    BuzzerApp buzzer = new BuzzerApp();
//    buzzer.sensorLoop();
    motion.sensorLoop();
    }

}