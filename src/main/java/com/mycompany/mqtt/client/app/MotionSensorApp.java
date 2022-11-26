package com.mycompany.mqtt.client.app;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MotionSensorApp extends Sensor{

    public MotionSensorApp(MqttRun mqtt, Mqtt5BlockingClient client){
        super("./pi-sensor-code/SenseLED.py", mqtt, client);
   
    }

    public void sensorLoop(){
        Thread thread = new Thread(() -> {
            try {
                CameraApp camera = new CameraApp();
                String previousOutput = "";
                while(true){
                    this.getSensorInfo();
                    //Gets output from sensor
                    String output = this.getOutput();
                    String motionOn = "motion detected >>>";
                    if(output.equals(motionOn) && !previousOutput.equals(motionOn)){
                        System.out.println("Confirmation: Motion detected.");
                        System.out.println("Taking picture");
                        camera.execute();
                        sendSensorData("example/motion/");
                    } else if (output.equals("no motion detected <<<")){
                        System.out.println(output);
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
