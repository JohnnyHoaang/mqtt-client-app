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
    
    public BuzzerApp(){
        super("./pi-sensor-code/Doorbell.py");
    }
    
    public void sensorLoop(){
        Thread thread = new Thread(()-> {
            try {
                String previousOutput = "";
                while(true){
                    this.getSensorInfo();
                    //Receive output from sensor
                    String output = this.getOutput();
                    String buzzerOn ="buzzer turned on >>>";
                    if(output.equals(buzzerOn) 
                            && !previousOutput.equals(buzzerOn)){
                        // TODO: Notify to MQTT server if buzzer turned on 
                        // System.out.println("Confirmation: Buzzer turned on.");
                    } else {
                        // System.out.println("output:"+output);
                    }
                    previousOutput = this.getOutput();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        });
        setThread(thread);
        thread.start();
    }
}
