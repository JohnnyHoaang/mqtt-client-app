package com.mycompany.mqtt.client.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
/**
 * JavaFX App
 */
public class App extends Application{
    private FXDashboard dashboard;
    private static MqttRun mqtt;
    @Override
    public void start(Stage stage) throws IOException {
        var console = System.console();
        String topicUser = console.readLine("Enter your topic user:");
        mqtt = new MqttRun();
        var scene = new Scene(new FXDashboard(mqtt,topicUser), 1215, 600);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        launch();

    }
}