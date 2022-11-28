/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import java.time.LocalDateTime;
import org.json.JSONObject;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public abstract class Sensor {
    private String filePath;
    private String output;
    private Thread thread;
    private ProcessBuilderHandler processBuilder;
    private MqttRun mqtt;
    private Mqtt5BlockingClient client;

    public Sensor(String filePath, MqttRun mqtt, Mqtt5BlockingClient client ){
        this.filePath = filePath;
        this.client = client;
        this.mqtt = mqtt;
        this.processBuilder = new ProcessBuilderHandler(this.filePath, this);
    }
    public void getSensorInfo(){
        try {
            this.processBuilder.startProcess();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getOutput(){
        return this.output;
    }
    public void setOutput(String output){
        this.output = output;
    }
    public Thread getThread(){
        return this.thread;
    }
    public void setThread(Thread thread){
        this.thread = thread;
    }
    public void stopThread(){
        if(this.thread!=null){
            this.thread.stop();
        }
    }
    public void sendSensorData(String topic){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("time",LocalDateTime.now());
        mqtt.publishMessage(client, topic, jsonMessage.toString().getBytes());
    }
    public MqttRun getMqtt(){
        return this.mqtt;
    }
    public Mqtt5BlockingClient getClient(){
        return this.client;
    }
    abstract void sensorLoop();
    
}
