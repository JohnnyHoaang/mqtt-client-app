/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.mqtt.client.app;

import java.util.Scanner;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class HumidityApp extends Sensor{

    public HumidityApp(){
        super("./pi-sensor-code/DHT11.py");
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
                    double humidity = Double.parseDouble(humidityArr[0]);
                    double temperature = Double.parseDouble(humidityArr[1]);
                    // Set tile info with provided output
                    setTileInfo(humidity, temperature);
                    Thread.sleep(3000);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        setThread(thread);
        thread.start();
    }
    private void setTileInfo(double humidity, double temperature){
        // TODO : Update tile text
        System.out.println("Humidity: " + humidity);
        System.out.println("Temperature: " + temperature);
    }
}
