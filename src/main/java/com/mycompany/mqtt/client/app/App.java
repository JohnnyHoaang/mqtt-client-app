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
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        var scene = new Scene(new FXDashboard(), 1215, 600);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException{
//        launch();
        var console = System.console();
        String topicUser = console.readLine("Enter your topic user:");
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        HumidityApp humidity = new HumidityApp(mqtt , client, topicUser);
        humidity.sensorLoop();
        BuzzerApp buzzer = new BuzzerApp(mqtt, client, topicUser);
        buzzer.sensorLoop();
        MotionSensorApp motion = new MotionSensorApp(mqtt, client, topicUser);
        motion.sensorLoop();
        mqtt.subscribeToTopic(client,"example/+/johnny/");
    
        mqtt.messageReceived(client);
        FXDashboard dash = new FXDashboard();
        while(true){
            System.out.println(mqtt.getResult());
            dash.test = mqtt.getResult();
            Thread.sleep(1000);
        }
    }
}