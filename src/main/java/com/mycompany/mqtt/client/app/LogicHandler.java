package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicHandler {
    private HumidityApp humiditySensor = new HumidityApp();
    private BuzzerApp buzzerSensor = new BuzzerApp();
    private MotionSensorApp motionSensor = new MotionSensorApp();

    Console con = System.console();

    // TODO: user input for loading JAVA keystore username and password
    public KeyStore loadKeystore(String path, char[] pass) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(path), pass);
            System.out.println("Keystore retrieved successfully\n");
            return ks;
        } catch (Exception e) {
            System.out.println("Unable to retreive Keystore, path or password was invalid\n");
            return null;
        }
    }

    // TODO: user input for storing keys in keystore, username and password
    public void storeKeys(KeyStore ks) {
        
    }

    // TODO: user input to establish connection to MQTT broker
    public void establishConnection(){

    }

    public PublicKey extractKeys(KeyStore ks) throws KeyStoreException {
        Enumeration<String> enumeration = ks.aliases();
        String alias = enumeration.nextElement();
        Certificate cert = ks.getCertificate(alias);
        PublicKey pubKey = ((PublicKey)cert.getPublicKey());
        return pubKey;
    }

    //* prompt until valid username and passwords are obtained */
    public String getPath(String path) {
        if (path.length() < 250 || path != null) {
            if (validatePath(path)) {
                System.out.println("\nValid path");
                
                return path;
            }
        }
        System.out.println("\nInvalid path");
        
        return null;
    }

    public Boolean validatePath(String path) {
        if (path.length() < 250 && path.length() != 0) {
            Pattern pattern = Pattern.compile("([a-zA-Z]:)?((\\\\|/)?[a-zA-Z0-9_.]+)+(\\\\|/)?([a-zA-Z0-9_].*)");
            Normalizer.normalize(path, Form.NFC);
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                return true;
            }
            System.out.println("\nPath entered is an improper format");
            return false;
        }
        System.out.println("\nPath entered is either too long or empty");
        
        return false;
    }

    // public char[] getPassword(char[] pass) {
        
    //     if (validatePass(pass)) {
    //         return pass;
    //     } 
    //     return null;
    // }

    public Boolean validatePass(char[] pass) {
        if (pass.length > 6 && pass.length < 30) {
            
            return true;
        }
        System.out.println("Invalid password");
                
        return false;
    }
    public void startHumiditySensor(){
        this.humiditySensor.sensorLoop();
    }
    public void stopHumiditySensor(){
        this.humiditySensor.stopThread();
    }
    public void startBuzzerSensor(){
        this.buzzerSensor.sensorLoop();
    }
    public void stopBuzzerSensor(){
        this.buzzerSensor.stopThread();
    }
    public void startMotionSensor(){
        this.motionSensor.sensorLoop();
    }
    public void stopMotionSensor(){
        this.motionSensor.stopThread();
    }
    
}
