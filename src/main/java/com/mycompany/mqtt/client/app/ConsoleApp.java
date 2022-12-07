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
import java.security.cert.*;
import java.util.Scanner;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

// TODO: perhaps merge with App later
public class ConsoleApp {

    private Console con = System.console();
    private KeyStore ks = null;
    private Key[] keys;
    private MqttRun mqtt = new MqttRun();
    private Mqtt5BlockingClient client;
    private String topicUser = "";
    private HumidityApp humidity;
    private BuzzerApp buzzer;
    private MotionSensorApp motion;
    // public static String user;
    private LogicHandler instance = new LogicHandler();

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, 
            KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, 
            SignatureException, InterruptedException, CertificateEncodingException {
        ConsoleApp app = new ConsoleApp();
        
        app.menu();
    }

    private void menu() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, 
            InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, SignatureException, 
            InterruptedException, CertificateEncodingException {
        boolean menu = true;
        while (menu) {
            System.out.println(Colors.PURPLE + "\n<|--------------- MQTT Client App ---------------|>\n" + Colors.RESET);

            System.out.println("1. Load a KeyStore\n"
                              +"2. Extract Keys from KeyStore\n"
                              +"3. Connect to MQTT and Start all sensors\n"
                              +"4. Store certificate to KeyStore " + Colors.YELLOW + "IN PROGRESS\n" + Colors.RESET
                              +"5. Exit");

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
                    if (keys != null && ks != null) {
                        topicUser = con.readLine("Enter topic user name");
                        client = mqtt.run();
                        instance.sendCertificate(mqtt, client, topicUser, ks);
                        humidity = new HumidityApp(mqtt , client, topicUser);
                        humidity.sensorLoop((PrivateKey)keys[1]);
                        buzzer = new BuzzerApp(mqtt, client, topicUser);
                        buzzer.sensorLoop((PrivateKey)keys[1]);
                        motion = new MotionSensorApp(mqtt, client, topicUser);
                        motion.sensorLoop((PrivateKey)keys[1]);
                        sensorMenu();
                    } else {
                        System.out.println(Colors.RED + "\nEnsure keystore was loaded and keys were extracted" + Colors.RESET);
                    }

                    break;
                   
                case "4":
                    System.out.println(Colors.YELLOW + "\nCERTIFICATE STORING NOT YET AVAILABLE" + Colors.RESET);
                    
                    break;

                case "5":
                    System.exit(1);
                default:
                    System.out.println(Colors.RED + "\nThat is not a valid menu option" + Colors.RESET);
                
                    break;
            }
        }
    }

    private void sensorMenu() throws UnrecoverableKeyException, InvalidKeyException, 
            KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, 
            UnsupportedEncodingException, SignatureException, InterruptedException, CertificateEncodingException {
        boolean menu = true;
        while (menu) {
            System.out.println("Press f to exit back to MainMenu");
            String choice = con.readLine();

            switch (choice) {
                case "f":
                    humidity.stopThread();
                    buzzer.stopThread();;
                    motion.stopThread();;
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
}
