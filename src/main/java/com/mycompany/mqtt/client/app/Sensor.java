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
import java.util.Base64;

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
    private MqttHandler mqtt;
    private Mqtt5BlockingClient client;
    private String topicUser;
    private SecurityHandler instance;

    public Sensor(String filePath, MqttHandler mqtt, Mqtt5BlockingClient client, String topicUser ){
        this.filePath = filePath;
        this.client = client;
        this.mqtt = mqtt;
        this.processBuilder = new ProcessBuilderHandler(this.filePath, this);
        this.topicUser = topicUser;
        this.instance  = new SecurityHandler();
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
    public SecurityHandler getInstance(){
        return this.instance;
    }

    /**
     * Sends the collected data from the sensors to the mqtt server
     * 
     * @param topic
     * @param key
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     */
    public void sendSensorData(String topic, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException{

        var time = LocalDateTime.now();
        String signedMessage = Base64.getEncoder().encodeToString(instance.generateSignature("SHA256withECDSA", key, time.toString()));
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("time", time);
        jsonMessage.put("signedTime", signedMessage);
        System.out.println("Sending Sensor Confirmation: " + jsonMessage.toString());
        mqtt.publishMessage(client, topic, jsonMessage.toString().getBytes());;
    }

    /**
     * 
     * @return instance on the MqttRun class
     */
    public MqttHandler getMqtt(){
        return this.mqtt;
    }

    /**
     * 
     * @return instance of the mqtt client
     */
    public Mqtt5BlockingClient getClient(){
        return this.client;
    }

    /**
     * 
     * @return the name to be used in the topic
     */
    public String getTopicUser(){
        return this.topicUser;
    }

    abstract void sensorLoop(PrivateKey key);
}
