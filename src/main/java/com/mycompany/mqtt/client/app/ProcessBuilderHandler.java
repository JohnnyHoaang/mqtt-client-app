package com.mycompany.mqtt.client.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessBuilderHandler {
    
    private ProcessBuilder processBuilder = new ProcessBuilder();
    private Sensor sensor;
   //The constructor to execute Python command takes a String and a Sensor
    public ProcessBuilderHandler(String theApp, Sensor sensor) {
        this.sensor = sensor;
        //Determine if the OS is MS Windows 
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        
        //List to store the command and the command arguments
        List<String> commandAndArgs;
        
        //Setup the command based on the OS type
        if (isWindows) {
            commandAndArgs = List.of("C:\\Dev\\python3", theApp);
            this.processBuilder.command(commandAndArgs);
        }
        else {
            commandAndArgs = List.of("/usr/bin/python3", theApp);
            this.processBuilder.command(commandAndArgs);
        }
    }
    
    //Start the process and get the output
    void startProcess() throws IOException {
     
        //Start the process
        var process = this.processBuilder.start();
        
        try (var reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                // Set output to sensor object
                this.sensor.setOutput(line);
            }

        }
    }
}
