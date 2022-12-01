package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.json.*;

public class LogicHandler {

    private HumidityApp humiditySensor = new HumidityApp(null, null, null);
    private BuzzerApp buzzerSensor  = new BuzzerApp(null, null, null);
    private MotionSensorApp motionSensor = new MotionSensorApp(null, null, null);

    Console con = System.console();

    public KeyStore loadKeystore(String path, char[] pass) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(path), pass);
            System.out.println(Colors.GREEN + "Keystore retrieved successfully\n" + Colors.RESET);
            return ks;
        } catch (Exception e) {
            System.out.println(Colors.RED + "Unable to retreive Keystore, path or password was invalid\n" + Colors.RESET);
            return null;
        }
    }

    public Key[] extractKeys(KeyStore ks, char[] pass) {
        try {
            Enumeration<String> enumeration = ks.aliases();
            String alias = enumeration.nextElement();
            Certificate cert = ks.getCertificate(alias);
            Key publicKey = cert.getPublicKey();
            Key privateKey = ks.getKey(alias, pass);
            Key[] keys = {publicKey, privateKey};
            return keys;
        } catch (Exception e) {
            System.out.println(Colors.RED + "Unable to extract keys, please ensure your password is correct" + Colors.RESET);
        }
        return null;
    }

    public boolean validatePath(String path) {
        if (path.length() < 250 && path.length() != 0) {
            Pattern pattern = Pattern.compile("([a-zA-Z]:)?((\\\\|\\/)?([a-zA-Z0-9_.][^-<>=+])+)+(\\\\|\\/)?(([a-zA-Z0-9_][^-<>=+]).*)");
            Normalizer.normalize(path, Form.NFC);
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                return true;
            }
            System.out.println(Colors.RED + "\nPath entered is an improper format" + Colors.RESET);
            return false;
        }
        System.out.println(Colors.RED + "\nPath entered is either too long or empty" + Colors.RESET);
        
        return false;
    }

    public Boolean validatePass(char[] pass) {
        if (pass.length > 6 && pass.length < 30) {
            
            return true;
        }
        System.out.println(Colors.RED + "Invalid password" + Colors.RESET);
                
        return false;
    }

    /**
     * Method for generating digital signature.
     * @author Carlton Davis
     */
    byte[] generateSignature (String algorithm, PrivateKey privatekey, String message) 
            throws NoSuchAlgorithmException, NoSuchProviderException, 
            InvalidKeyException, UnsupportedEncodingException, SignatureException {
        
        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");
        
        //Initialize the signature scheme
        sig.initSign(privatekey);
        
        //Compute the signature
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = sig.sign();
        
        return signature;
    }

    /**
     * Method for verifying digital signature.
     * @author Carlton Davis
     * TODO: Be used to verify received messages from mqtt later
     */
    boolean verifySignature(byte[] signature, PublicKey publickey, String algorithm, String message) 
            throws NoSuchAlgorithmException, NoSuchProviderException, 
            InvalidKeyException, UnsupportedEncodingException, SignatureException {
        
        //Create an instance of the signature scheme for the given signature algorithm
        Signature sig = Signature.getInstance(algorithm, "SunEC");
        
        //Initialize the signature verification scheme.
        sig.initVerify(publickey);
        
        //Compute the signature.
        sig.update(message.getBytes("UTF-8"));
        
        //Verify the signature.
        boolean validSignature = sig.verify(signature);
        
        if(validSignature) {
            System.out.println(Colors.GREEN + "\nSignature is valid" + Colors.RESET);
        } else {
            System.out.println(Colors.RED + "\nSignature is not valid" + Colors.RESET);
        }
        
        return validSignature;
    }
    public void sendCertificate(MqttRun mqtt, Mqtt5BlockingClient client, String topicUser) throws KeyStoreException, CertificateEncodingException{
        var console = System.console();
        char [] pass = console.readPassword("Enter password: ");
        KeyStore ks = this.loadKeystore("./JohnnyECcertif.ks", pass);
        Enumeration<String> enumeration = ks.aliases();
        String alias = enumeration.nextElement();
        Key [] keys = this.extractKeys(ks, pass);
        JSONObject json = new JSONObject();
        String encodedString = Base64.getEncoder().encodeToString(ks.getCertificate(alias).getEncoded());
        json.put("certificate", encodedString);
        mqtt.publishMessage(client, "certificate/"+topicUser+"/", json.toString().getBytes());
        
    }
    public void startHumiditySensor(){
        System.out.println("Entered");
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
