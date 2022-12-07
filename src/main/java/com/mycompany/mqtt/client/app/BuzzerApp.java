/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import java.security.PrivateKey;
import java.time.LocalDateTime;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class BuzzerApp extends Sensor{
   
    public BuzzerApp(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/Doorbell.py", mqtt, client, topicUser);
    }
    
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
