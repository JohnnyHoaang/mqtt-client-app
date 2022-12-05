package com.mycompany.mqtt.client.app;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
public class TestApp {
    public static void main(String[] args){
        var instance = new LogicHandler();
        var console = System.console();
        var password = console.readPassword("Enter password: ");
        var ks = instance.loadKeystore("TestKeystore.ks", password);
        var keys = instance.extractKeys(ks, password);
        byte [] signature = null;
        try {
            signature = instance.generateSignature("SHA256withECDSA", (PrivateKey)keys[1] , "Hello");
            String encoded = Base64.getEncoder().encodeToString(signature);
            System.out.println(encoded);
            byte[] decoded = Base64.getDecoder().decode(encoded);
            System.out.println(new String(decoded));
            System.out.println(instance.verifySignature(decoded, (PublicKey)keys[0], "SHA256withECDSA", "Hello"));
        } catch (Exception e){
            e.printStackTrace();
        }   
        
    }
}
