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

    public static void main(String[] args) throws IOException{

        launch();

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