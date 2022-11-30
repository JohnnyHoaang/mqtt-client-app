package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Scanner;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

// TODO: perhaps merge with App later
public class ConsoleApp {

    private Console con = System.console();
    private KeyStore ks = null;
    private Key[] keys;
    public static MqttRun mqtt = new MqttRun();
    public static Mqtt5BlockingClient client;
    public static String user;
    private LogicHandler instance = new LogicHandler();

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        ConsoleApp app = new ConsoleApp();
        
        app.menu();
    }

    private void menu() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, SignatureException {
        boolean menu = true;
        while (menu) {
            System.out.println(Colors.PURPLE + "\n<|--------------- MQTT Client App ---------------|>\n" + Colors.RESET);

            System.out.println("1. Load a KeyStore\n"
                              +"2. Extract Keys from KeyStore\n"
                              +"3. Connect to MQTT Client\n"
                              +"4. Store certificate to KeyStore " + Colors.YELLOW + "IN PROGRESS\n" + Colors.RESET
                              +"5. Send a message\n"
                              +"6. Start Buzzer Sensor\n"
                              +"7. Start Temperature/Humidity Sensor\n"
                              +"8. Start Motion Sensor\n"
                              +"9. Start all sensors\n"
                              +"10. Exit");

            String choice = con.readLine();
            switch (choice) {
                case "1":
                    ks = getKeystore();
                    break;

                case "2":
                    
                    char[] pass = getPass();
                    keys = instance.extractKeys(ks, pass);
                    if (keys != null) {
                        System.out.println(Colors.GREEN + "\nKeys extracted" + Colors.RESET);
                    }
                    break;

                case "3":
                    System.out.println("Enter your topic user name");
                    user = con.readLine();
                    client = mqtt.run();
                    break;

                case "4":
                    System.out.println(Colors.YELLOW + "\nCERTIFICATE STORING NOT YET AVAILABLE" + Colors.RESET);
                    
                    break;

                case "5":
                    writeMessage();
                    break;

                case "6":
                    instance.startBuzzerSensor();
                    sensorMenu();
                    break;
                case "7":
                    instance.startHumiditySensor();
                    sensorMenu();
                    break;
                case "8":
                    instance.startMotionSensor();
                    sensorMenu();
                    break;
                case "9":
                    
                    instance.startBuzzerSensor();
                    instance.startHumiditySensor();
                    instance.startMotionSensor();
                    sensorMenu();
                    break;
                case "10":
                    System.exit(1);
                default:
                    System.out.println(Colors.RED + "\nThat is not a valid menu option" + Colors.RESET);
                
                    break;
            }
        }
    }

    private void sensorMenu() throws UnrecoverableKeyException, InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException {
        boolean menu = true;
        while (menu) {
            System.out.println("Press f to exit back to MainMenu");
            String choice = con.readLine();

            switch (choice) {
                case "f":
                    instance.stopBuzzerSensor();
                    instance.stopHumiditySensor();
                    instance.stopMotionSensor();
                    menu();
                    break;
            
                default:
                    break;
            }
        }
    }

    // get the path from the user and validate it, promting again if it's invalid
    private String getPath() {
        boolean pathValid = false;
        System.out.println(Colors.PURPLE + "\n<----- Keystore Path ----->" + Colors.RESET);
        String path = "";

        while (pathValid != true) {
            System.out.println("\nPlease enter the path to your keystore: ");
            path = con.readLine();
            pathValid = instance.validatePath(path);
        }
        return path;
    }

    private char[] getPass() {
        boolean passValid = false;
        char[] pass = {};
        System.out.println(Colors.PURPLE + "\n<----- Keystore Password ----->" + Colors.RESET);
            
        while (passValid != true) {
            System.out.println("\nPlease enter the password for your keystore");
            pass = con.readPassword();
            passValid = instance.validatePass(pass);
        }
        return pass;
    }

    private KeyStore getKeystore() {
        boolean gotKeystore = false;

        String path;
        char[] pass = {};
        KeyStore ks = null;

        while (gotKeystore != true) {

            path = getPath();
            pass = getPass();
            ks = instance.loadKeystore(path, pass);

            if(ks != null) {
                gotKeystore = true;
            }
        }
        return ks;
    }
    
    // TODO: be used later when fetching certs from mqtt
    private void storeCertificate(String alias, Certificate cert) throws KeyStoreException{
        ks.setCertificateEntry(alias, cert);
        
    }

    private void writeMessage() {
        System.out.println(Colors.PURPLE + "\n<----- Write a Message ----->" + Colors.RESET);
        System.out.println("\nFirst please enter the topic you wish to subscribe to:");
        String topic = con.readLine();
        System.out.println("\nNext please enter the message you wish to send:");
        String message = con.readLine();
        try {
            mqtt.publishMessage(client, topic, instance.generateSignature("SHA256withECDSA", (PrivateKey)keys[1], message));
            System.out.println(Colors.GREEN + "\nMessage Sent.\n" + Colors.RESET);
            
        } catch (Exception e) {
            System.out.println(Colors.RED + "\nCould not publish message, please ensure you have successfully:\n"
                              +"- Loaded a keystore\n"
                              +"- Extracted the keys\n"
                              +"- Connected to the client\n" + Colors.RESET);
        }
    }
}
