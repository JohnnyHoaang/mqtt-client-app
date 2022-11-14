package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.IOException;
import java.security.KeyStore;

// TODO: perhaps merge with App later
public class ConsoleApp {
    public static void main(String[] args) throws IOException {
        System.out.println("|--------------- MQTT Client App ---------------|\n");

        boolean passValid = false;
        boolean pathValid = false;
        boolean gotKeystore = false;

        LogicHandler instance = new LogicHandler();
        Console con = System.console();

        while (gotKeystore != true) {

            System.out.println("\n<----- Keystore Path ----->");

            String path = "";
    
            while (pathValid != true) {
                System.out.println("\nPlease enter the path to your keystore: ");
                path = con.readLine();
                pathValid = instance.validatePath(path);
            }
    
            System.out.println("\n<----- Keystore Password ----->");
            
            char[] pass = {};

            while (passValid != true) {
                System.out.println("\nPlease enter the password for your keystore");
                pass = con.readPassword();
                passValid = instance.validatePass(pass);
            }

            KeyStore ks = instance.loadKeystore(path, pass);

            if (ks == null) {
                passValid = false;
                pathValid = false;
            } else {
                gotKeystore = true;
            }
        }
        
    }
}
