/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.mqtt.client.app;

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

import org.junit.jupiter.api.Test;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Katharina Orfanidis
 */
public class LogicHandlerTest {

    /**
     * Test of loadKeystore method, of class LogicHandler.
     * @throws KeyStoreException
     */
    @Test
    public void testLoadKeystore() throws KeyStoreException {
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        KeyStore result = instance.loadKeystore("TestKeystore.ks", "12345678".toCharArray());
        assertEquals(result.getClass(), KeyStore.class);
    }

    /**
     * Test of extractKeys method, of class LogicHandler.
     */
    @Test
    public void testExtractKeys() throws Exception {
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        KeyStore ks = instance.loadKeystore("./TestKeystore.ks", "12345678".toCharArray());
        Key[] keys = instance.extractKeys(ks, "12345678".toCharArray());
        int length = keys.length;
        assertNotNull(keys);
        assertEquals(2, length);
    }

    /**
     * Test of validatePath method, of class LogicHandler.
     */
    @Test
    public void testValidatePath() {
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        String validPath = "keystore.ks";
        String invalidPath = "not->valid -path";


        boolean validResult = instance.validatePath(validPath);
        boolean invalidResult = instance.validatePath(invalidPath);

        assertEquals(true, validResult);
        assertEquals(false, invalidResult);
    }

    /**
     * Test of getPassword method, of class LogicHandler.
     */
    @Test
    public void testValidPassword() {
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        boolean expResult = true;
        boolean result = instance.validatePass("12345678".toCharArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testInvalidPassword() {
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        boolean expResult = false;
        boolean result = instance.validatePass("1234".toCharArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testGeneratingSignature() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, SignatureException{
        MqttRun mqtt = new MqttRun();
        Mqtt5BlockingClient client = mqtt.run();
        String user = "";

        LogicHandler instance = new LogicHandler(mqtt, client, user);
        KeyStore ks = instance.loadKeystore("TestKeystore.ks", "12345678".toCharArray());
        Key[] keys = instance.extractKeys(ks, "12345678".toCharArray());
        byte[] sig = instance.generateSignature("SHA256withECDSA", (PrivateKey)keys[1], "hello");
        assertNotNull(sig);
    }
}
