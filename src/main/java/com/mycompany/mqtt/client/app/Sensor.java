/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public abstract class Sensor {
    private String filePath = "";
    private String output = "";
    
    public void getSensorInfo(){
        try {
            ProcessBuilderHandler processBuilder = new ProcessBuilderHandler(this.filePath, this);
            processBuilder.startProcess();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    abstract String getOutput();
    abstract void setOutput(String output);
}
