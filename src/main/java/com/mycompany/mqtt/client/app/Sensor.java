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

    public Sensor(String filePath){
        this.filePath = filePath;
    }
    public void getSensorInfo(){
        try {
            ProcessBuilderHandler processBuilder = new ProcessBuilderHandler(this.filePath, this);
            processBuilder.startProcess();
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
    abstract void sensorLoop();
}
