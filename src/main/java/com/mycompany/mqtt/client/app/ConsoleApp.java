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

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

// TODO: perhaps merge with App later
public class ConsoleApp {

    Console con = System.console();
    LogicHandler instance = new LogicHandler();
    KeyStore ks = null;
    Key[] keys;
    MqttRun mqtt = new MqttRun();
    Mqtt5BlockingClient client;

    public static void main(String[] args) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        ConsoleApp app = new ConsoleApp();
        
        app.menu();
    }

    private void connectMqtt() {
        boolean connect = false;
        MqttRun mqtt = new MqttRun();


        while (connect == false) {

            try {
                mqtt.run();
                connect = true;
            } catch (Exception e) {
                System.out.println("Unable to establish a connection, ensure Username and Password are correct and try again");
            }
        }


    }

    private void sendMessage() {

    }

    private void menu() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, SignatureException {

        while (true) {
            System.out.println("<|--------------- MQTT Client App ---------------|>\n");

            System.out.println("1. Load a KeyStore\n"
                              +"2. Extract Keys from KeyStore\n"
                              +"3. Connect to MQTT Client\n"
                              +"4. Store certificate to KeyStore\n"
                              +"5. Send a message\n"
                              +"6. Exit");

            String choice = con.readLine();
            switch (choice) {
                case "1":
                    ks = loadKeystore();
                    break;

                case "2":
                    
                    System.out.println("Enter KeyStore password to extract keys");
                    char[] pass = getPass();
                    keys = instance.extractKeys(ks, pass);
                    if (keys == null) {
                        System.out.println("No keys were extracted, no keys present or incorrect password");
                    }
                    System.out.println("Keys extracted");
                    break;

                case "3":
                    client = mqtt.run();
                    break;

                case "4":

                    break;

                case "5":
                    writeMessage();
                    break;

                case "6":
                    System.exit(0);

                default:
                    System.out.println("That is not a valid menu option");
                
                    break;
            }
        }
    }

    private String getPath() {
        boolean pathValid = false;
        System.out.println("\n<----- Keystore Path ----->");
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
        System.out.println("\n<----- Keystore Password ----->");
            
        while (passValid != true) {
            System.out.println("\nPlease enter the password for your keystore");
            pass = con.readPassword();
            passValid = instance.validatePass(pass);
        }
        return pass;
    }

    private KeyStore loadKeystore() {
        boolean gotKeystore = false;

        String path;
        char[] pass = {};
        KeyStore ks = null;
        Key[] keys;

        LogicHandler instance = new LogicHandler();
        Console con = System.console();

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

    private void writeMessage() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException {
        System.out.println("<----- Write a Message ----->");
        System.out.println("\nFirst please enter the topic you wish to subscribe to:");
        String topic = con.readLine();
        System.out.println("\nNext please enter the message you wish to send:");
        String message = con.readLine();
        try {
            mqtt.publishMessage(client, topic, instance.generateSignature("SHA256withECDSA", (PrivateKey)keys[1], message));
        } catch (Exception e) {
            System.out.println("\nCould not publish message, please ensure you have successfully:\n"
                              +"- Loaded a keystore\n"
                              +"- Extracted the keys\n"
                              +"- Connected to the client\n");
        }
    }

}
