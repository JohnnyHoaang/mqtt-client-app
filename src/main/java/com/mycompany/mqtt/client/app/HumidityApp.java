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
public class HumidityApp extends Sensor{
    private double humidity;
    private double temperature;

    public HumidityApp(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/DHT11.py", mqtt, client, topicUser);
    }
    // Calls humidity and temperature information in a loop to update given tile
    public void sensorLoop(PrivateKey key){
        Thread thread = new Thread(()-> {
            try {
                while(true){
                    this.getSensorInfo();
                    // Receive output from sensor
                    String humidityInfo = this.getOutput();
                    String [] humidityArr = humidityInfo.split(",");
                    this.humidity = Double.parseDouble(humidityArr[0]);
                    this.temperature = Double.parseDouble(humidityArr[1]);
                    // Set tile info with provided output
                    sendSensorData("sensor/humidity/"+getTopicUser()+"/", key);
                    Thread.sleep(3000);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        setThread(thread);
        thread.start();
    }
    @Override
    public void sendSensorData(String topic, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException{
        String signedTemp = Base64.getEncoder().encodeToString(getInstance().generateSignature("SHA256withECDSA", key, Double.toString(this.temperature)));
        String signedHumidity = Base64.getEncoder().encodeToString(getInstance().generateSignature("SHA256withECDSA", key, Double.toString(this.humidity)));
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("time",LocalDateTime.now());
        jsonMessage.put("temperature", this.temperature);
        jsonMessage.put("signedTemp", signedTemp);
        jsonMessage.put("humidity", this.humidity);
        jsonMessage.put("signedHum", signedHumidity);
        System.out.println("Sending ambient data: " + jsonMessage.toString());
        getMqtt().publishMessage(getClient(), topic, jsonMessage.toString().getBytes());
    }
}
