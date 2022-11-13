package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

// TODO: perhaps merge with App later
public class ConsoleApp {
    public static void main(String[] args) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        System.out.println("|--------------- MQTT Client App ---------------|\n");

        ConsoleApp app = new ConsoleApp();

        boolean passValid = false;
        boolean pathValid = false;
        boolean gotKeystore = false;

        String path;
        char[] pass = {};
        KeyStore ks = null;
        Key[] keys;

        LogicHandler instance = new LogicHandler();
        Console con = System.console();

        while (gotKeystore != true) {

            System.out.println("\n<----- Keystore Path ----->");

            path = "";
    
            while (pathValid != true) {
                System.out.println("\nPlease enter the path to your keystore: ");
                path = con.readLine();
                pathValid = instance.validatePath(path);
            }
    
            System.out.println("\n<----- Keystore Password ----->");
            
            while (passValid != true) {
                System.out.println("\nPlease enter the password for your keystore");
                pass = con.readPassword();
                passValid = instance.validatePass(pass);
            }

            ks = instance.loadKeystore(path, pass);

            if (ks == null) {
                passValid = false;
                pathValid = false;
            } else {
                gotKeystore = true;
            }
        }

        keys = instance.extractKeys(ks, pass);
        System.out.println("keys[0]");

        app.connectMqtt();

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
    
}
