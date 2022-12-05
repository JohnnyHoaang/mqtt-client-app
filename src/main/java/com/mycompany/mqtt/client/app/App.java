package com.mycompany.mqtt.client.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import java.security.*;
import java.util.Enumeration;
/**
 * JavaFX App
 */
public class App extends Application{
    // private FXDashboard dashboard;
    public static MqttRun mqtt;
    @Override
    public void start(Stage stage) throws IOException {
        var console = System.console();

        mqtt = new MqttRun();
        var scene = new Scene(new FXDashboard(mqtt,""), 1215, 600);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeyStoreException{
        launch();
        
    }
}