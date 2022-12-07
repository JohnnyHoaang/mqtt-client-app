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
    private MqttHandler mqtt = new MqttHandler();
    private Mqtt5BlockingClient client;
    private String topicUser = "";
    private AmbientSensor humidity;
    private BuzzerSensor buzzer;
    private MotionSensor motion;
    // public static String user;
    private SecurityHandler instance = new SecurityHandler();

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, 
            KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, 
            SignatureException, InterruptedException, CertificateEncodingException {
        ConsoleApp app = new ConsoleApp();
        
        app.menu();
    }

    /**
     * Display a menu for the user
     * 
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     * @throws InterruptedException
     * @throws CertificateEncodingException
     */
    private void menu() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, 
            InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, SignatureException, 
            InterruptedException, CertificateEncodingException {
        boolean menu = true;
        while (menu) {
            System.out.println(Colors.PURPLE + "\n<|--------------- MQTT Client App ---------------|>\n" + Colors.RESET);

            System.out.println("1. Load a KeyStore\n"
                              +"2. Extract Keys from KeyStore\n"
                              +"3. Connect to MQTT and Start all sensors\n"
                              +"4. Exit");

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
                        topicUser = con.readLine("\nEnter topic user name: \n");
                        boolean askCredentials = true;
                        while (askCredentials) {
                            try {
                                System.out.println(Colors.PURPLE + "\nMQTT Credentials" + Colors.RESET);
                                client = mqtt.run();
                                askCredentials = false;
                                instance.sendCertificate(mqtt, client, topicUser, ks);
                                humidity = new AmbientSensor(mqtt , client, topicUser);
                                humidity.sensorLoop((PrivateKey)keys[1]);
                                buzzer = new BuzzerSensor(mqtt, client, topicUser);
                                buzzer.sensorLoop((PrivateKey)keys[1]);
                                motion = new MotionSensor(mqtt, client, topicUser);
                                motion.sensorLoop((PrivateKey)keys[1]);
                                sensorMenu();
                            } catch (Exception e) {
                                System.out.println(Colors.RED + "Invalid credentials, please try again" + Colors.RESET);
                            }
                        }

                    } else {
                        System.out.println(Colors.RED + "\nEnsure keystore was loaded and keys were extracted" + Colors.RESET);
                    }

                    break;

                case "4":
                    System.exit(1);
                default:
                    System.out.println(Colors.RED + "\nThat is not a valid menu option" + Colors.RESET);
                
                    break;
            }
        }
    }

    /**
     * Menu for when sensors are running, allows the user to eneter f to shut down all sensors
     * 
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     * @throws InterruptedException
     * @throws CertificateEncodingException
     */
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

    /**
     * get the path from the user and validate it, promting again if it's invalid
     * 
     * @return the user given path
     */
    private String getPath() {
        boolean pathValid = false;
        System.out.println(Colors.PURPLE + "\n<----- Keystore Path ----->" + Colors.RESET);
        String path = "";

        // keep prompting until path is valid
        while (pathValid != true) {
            System.out.println("\nPlease enter the path to your keystore: ");
            path = con.readLine();
            pathValid = instance.validatePath(path);
        }
        return path;
    }

    /**
     * @return user given password
     */
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

    /**
     * @return the users keystore
     */
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
