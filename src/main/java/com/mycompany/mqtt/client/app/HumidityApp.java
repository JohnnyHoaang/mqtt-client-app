/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import java.time.LocalDateTime;
import java.util.Scanner;
import org.json.JSONObject;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class HumidityApp extends Sensor{
    private double humidity;
    private double temperature;
    public HumidityApp(MqttRun mqtt, Mqtt5BlockingClient client){
        super("./pi-sensor-code/DHT11.py", mqtt, client);
    }
    // Calls humidity and temperature information in a loop to update given tile
    public void sensorLoop(){
        Scanner scanner = new Scanner(System.in);
        // TODO: Will take tile parameter to update tile text
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
                    sendSensorData("example/humidity/");
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
    public void sendSensorData(String topic){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("time",LocalDateTime.now());
        jsonMessage.put("temperature",this.temperature);
        jsonMessage.put("humidity",this.humidity);
        getMqtt().publishMessage(getClient(), topic, jsonMessage.toString().getBytes());
    }
}
