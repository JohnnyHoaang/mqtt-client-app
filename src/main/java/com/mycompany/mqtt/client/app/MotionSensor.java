package com.mycompany.mqtt.client.app;

import java.security.PrivateKey;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MotionSensor extends Sensor{

    public MotionSensor(MqttHandler mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/SenseLED.py", mqtt, client, topicUser);
   
    }

    /**
     * Runs motion sensor continuously to get motion data to be sent to the server
     * @param key sent with data in order to sign it
     */
    public void sensorLoop(PrivateKey key){
        Thread thread = new Thread(() -> {
            try {
                CameraApp camera = new CameraApp();
                String previousOutput = "";
                while(true){
                    this.getSensorInfo();
                    //Gets output from sensor
                    String output = this.getOutput();
                    String motionOn = "motion detected >>>";
                    if(output.equals(motionOn) && !previousOutput.equals(motionOn)){
                        camera.execute();
                        sendSensorData("sensor/motion/"+getTopicUser()+"/", key);
                    } 
                    previousOutput = this.getOutput();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        });
        setThread(thread);
        thread.start();
    }
    
}
