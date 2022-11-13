package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicHandler {

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

    public Key[] extractKeys(KeyStore ks, char[] pass) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        
        Enumeration<String> enumeration = ks.aliases();
        String alias = enumeration.nextElement();
        Certificate cert = ks.getCertificate(alias);
        Key publicKey = cert.getPublicKey();
        Key privateKey = ks.getKey(alias, pass);
        Key[] keys = {publicKey, privateKey};
        return keys;
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

    public Boolean validatePass(char[] pass) {
        if (pass.length > 6 && pass.length < 30) {
            
            return true;
        }
        System.out.println("Invalid password");
                
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
            System.out.println("\nSignature is valid");
        } else {
            System.out.println("\nSignature is not valid");
        }
        
        return validSignature;
    }


}
