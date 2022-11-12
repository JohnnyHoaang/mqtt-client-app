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
public class HumidityApp {
    private String filePath ="./pi-sensor-code/DHT11.py";

    public String getHumidity(){
        String result = "";
        try {
            ProcessBuilderHandler processBuilder = new ProcessBuilderHandler(this.filePath);
            result = processBuilder.startProcess();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    // Calls humidity and temperature information in a loop to update given tile
    public void humidityLoop(){
        // TODO: Will take tile parameter to update tile text
        Thread thread = new Thread(()-> {
            try {
                while(true){
                    String humidityInfo = this.getHumidity();
                    String [] humidityArr = humidityInfo.split(",");
                    double humidity = Double.parseDouble(humidityArr[0]);
                    double temperature = Double.parseDouble(humidityArr[1]);
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
}
