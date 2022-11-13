/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;

/**
 *
 * @author Johnny Hoang <johnny.hoang@dawsoncollege.qc.ca>
 */
public class BuzzerApp extends Sensor{
    private String output = "";
    
    public BuzzerApp(){
        this.setFilePath("./pi-sensor-code/Doorbell.py");
    }
    
    public void buzzerLoop(){
        Thread thread = new Thread(()-> {
            try {
                while(true){
                    this.getSensorInfo();
                    //Receive output from sensor
                    String output = this.getOutput();
                    if(output.equals("buzzer turned on >>>")){
                        // TODO: Notify to MQTT server if buzzer turned on 
                        System.out.println("Comfirmation: Buzzer turned on.");
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        });
        thread.start();
    }
    @Override
    public String getOutput(){
        return output;
    }
    @Override
    public void setOutput(String output){
        this.output = output;
    }
}
