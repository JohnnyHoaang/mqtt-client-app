package com.mycompany.mqtt.client.app;

public class MotionSensorApp extends Sensor{
    private String output = "";

    public MotionSensorApp(){
        super("./pi-sensor-code/SenseLED.py");
    }

    public void sensorLoop(){
        Thread thread = new Thread(() -> {
            try {
                CameraApp camera = new CameraApp();
                while(true){
                    this.getSensorInfo();
                    //Gets output from sensor
                    String output = this.getOutput();
                    if(output.equals("motion detected >>>")){
                        System.out.println("Confirmation: Motion detected.");
                        System.out.println("Taking picture");
                        camera.execute();
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
        return this.output;
    }

    @Override
    public void setOutput(String out){
        this.output = out;
    }
}
