/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.mqtt.client.app;
import java.io.IOException;
import javafx.application.Platform;
/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class HumidityApp extends Sensor{
    private String output = "";
    
    public HumidityApp(){
        this.setFilePath("./pi-sensor-code/DHT11.py");
    }
    // Calls humidity and temperature information in a loop to update given tile
    public void humidityLoop(){
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
                    Thread.sleep(2000);
                } 
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    private void setTileInfo(double humidity, double temperature){
        // TODO : Update tile text
        System.out.println("Humidity: " + humidity);
        System.out.println("Temperature: " + temperature);
    }
    @Override
    public String getOutput(){
        return this.output;
    }
    @Override
    public void setOutput(String output){
        this.output = output;
    }
}
