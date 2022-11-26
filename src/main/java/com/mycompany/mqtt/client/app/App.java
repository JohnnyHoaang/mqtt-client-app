package com.mycompany.mqtt.client.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
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
    MqttRun mqtt = new MqttRun();
    Mqtt5BlockingClient client = mqtt.run();
    HumidityApp humidity = new HumidityApp(mqtt , client);
    humidity.sensorLoop();
    BuzzerApp buzzer = new BuzzerApp(mqtt, client);
    buzzer.sensorLoop();
    MotionSensorApp motion = new MotionSensorApp(mqtt, client);
    motion.sensorLoop();
    while(true){
        mqtt.messageReceived(client);
    }
   
    }

}