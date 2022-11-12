package com.mycompany.mqtt.client.app;

import java.io.Console;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;
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
            return ks;
        } catch (Exception e) {
            return null;
        }
    }

    // TODO: user input for storing keys in keystore, username and password
    public void storeKeys(KeyStore ks) {
        
    }

    // TODO: user input to establish connection to MQTT broker
    public void establishConnection(){

    }

    public PublicKey extractKeys(KeyStore ks) throws KeyStoreException {
        Enumeration<String> enumeration = ks.aliases();
        String alias = enumeration.nextElement();
        Certificate cert = ks.getCertificate(alias);
        PublicKey pubKey = ((PublicKey)cert.getPublicKey());
        return pubKey;
    }

    //* prompt until valid username and passwords are obtained */
    public String getPath() {
        String path =  con.readLine();
        if (path.length() < 250 || path != null) {
            if (validatePath(path)) {
                return path;
            }
        }
        return null;
    }

    public Boolean validatePath(String path) {
        Pattern pattern = Pattern.compile("([a-zA-Z]:)?((\\\\|/)?[a-zA-Z0-9_.]+)+(\\\\|/)?([a-zA-Z0-9_].*)");
        Normalizer.normalize(path, Form.NFC);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    public char[] getPassword() {
        return con.readPassword();
    }
}
