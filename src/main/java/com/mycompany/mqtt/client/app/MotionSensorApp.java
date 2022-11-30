package com.mycompany.mqtt.client.app;


import java.io.File;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MotionSensorApp extends Sensor{

    private CameraApp camera = new CameraApp();

    public MotionSensorApp(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/SenseLED.py", mqtt, client, topicUser);
   
    }

    public void sensorLoop(){
        Thread thread = new Thread(() -> {
            try {
                String previousOutput = "";
                while(true){
                    this.getSensorInfo();
                    //Gets output from sensor
                    String output = this.getOutput();
                    String motionOn = "motion detected >>>";
                    if(output.equals(motionOn) && !previousOutput.equals(motionOn)){
                        camera.execute();
                        sendSensorData("sensor/motion/"+getTopicUser()+"/");
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

    @Override
    public void sendSensorData(String topic){
        byte[] fileContent = FileUtils.readFileToByteArray(new File(camera.getOutPutPath() + camera.getCameraCount()));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("pic",encodedString);
        getMqtt().publishMessage(getClient(), topic, jsonMessage.toString().getBytes());
    }
    
}
