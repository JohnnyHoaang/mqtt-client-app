package com.mycompany.mqtt.client.app;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import org.json.JSONObject;
import java.security.PrivateKey;
import java.time.LocalDateTime;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MotionSensorApp extends Sensor{

    private CameraApp camera = new CameraApp();

    public MotionSensorApp(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser){
        super("./pi-sensor-code/SenseLED.py", mqtt, client, topicUser);
   
    }

    public void sensorLoop(PrivateKey key){
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
                        sendSensorData("sensor/motion/"+getTopicUser()+"/", key);
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
        try{
        //byte[] fileContent = FileUtils.readFileToByteArray(new File(camera.getOutPutPath() + camera.getCameraCount()));
        File f = new File(camera.getOutPutPath() + camera.getCameraCount());
        FileInputStream fin = new FileInputStream(f);
        byte imageByteArray[] = new byte[(int)f.length()];
        fin.read(imageByteArray);
        String encodedString = Base64.getEncoder().encodeToString(imageByteArray);
        fin.close();
        //String encodedString = Base64.getEncoder().encodeToString(fileContent);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("pic",encodedString);
        getMqtt().publishMessage(getClient(), topic, jsonMessage.toString().getBytes());
        }
        catch(IOException exception){
            System.out.println(exception);
        }
    }
        
    
}
