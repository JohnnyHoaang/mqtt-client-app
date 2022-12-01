/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
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
    private String topicUser;
    private LogicHandler instance;

    public Sensor(String filePath, MqttRun mqtt, Mqtt5BlockingClient client, String topicUser ){
        this.filePath = filePath;
        this.client = client;
        this.mqtt = mqtt;
        this.processBuilder = new ProcessBuilderHandler(this.filePath, this);
        this.topicUser = topicUser;
        this.instance  = new LogicHandler();
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
    public LogicHandler getInstance(){
        return this.instance;
    }
    public void sendSensorData(String topic, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException{
        byte[] signedMessage = instance.generateSignature("SHA256withRSA", key, LocalDateTime.now().toString());
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("time", LocalDateTime.now());
        jsonMessage.put("signedTime", signedMessage);
        mqtt.publishMessage(client, topic, jsonMessage.toString().getBytes());
    }
    public MqttRun getMqtt(){
        return this.mqtt;
    }
    public Mqtt5BlockingClient getClient(){
        return this.client;
    }
    public String getTopicUser(){
        return this.topicUser;
    }
    abstract void sensorLoop(PrivateKey key);
    
}
