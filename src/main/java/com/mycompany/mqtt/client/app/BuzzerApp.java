package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import java.security.PrivateKey;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class BuzzerApp extends Sensor{
   
    public BuzzerApp(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/Doorbell.py", mqtt, client, topicUser);
    }
    
    /**
     * Keep the sensor running to continuously get and send data
     * @param key sent with the data in order to sign it
     */
    public void sensorLoop(PrivateKey key){
        Thread thread = new Thread(()-> {
            try {
                String previousOutput = "";
                while(true){
                    this.getSensorInfo();
                    //Receive output from sensor
                    String output = this.getOutput();
                    String buzzerOn ="buzzer turned on >>>";
                    if(output.equals(buzzerOn)  && !previousOutput.equals(buzzerOn)){
                        sendSensorData("sensor/buzzer/"+getTopicUser()+"/", key);
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
