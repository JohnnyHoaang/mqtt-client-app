package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicHandler {
    private HumidityApp humiditySensor = new HumidityApp(null,null, null);
    private BuzzerApp buzzerSensor = new BuzzerApp(null,null, null);
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
