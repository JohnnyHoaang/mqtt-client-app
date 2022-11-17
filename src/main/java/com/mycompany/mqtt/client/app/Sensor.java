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
    private String filePath;
    private String output;
    private Thread thread;
    private ProcessBuilderHandler processBuilder;

    public Sensor(String filePath){
        this.filePath = filePath;
        this.processBuilder = new ProcessBuilderHandler(this.filePath, this);
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
    abstract void sensorLoop();
}
