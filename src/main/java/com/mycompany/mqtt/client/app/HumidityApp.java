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
    public void humidityLoop(){
        Thread thread = new Thread(()-> {
            try {
                while(true){
                    System.out.println(this.getHumidity());
                    Thread.sleep(2000);
                } 
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        
    }
}
