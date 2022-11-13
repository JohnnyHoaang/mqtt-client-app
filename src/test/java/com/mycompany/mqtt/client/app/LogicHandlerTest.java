/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.mqtt.client.app;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Katharina Orfanidis
 */
public class LogicHandlerTest {
    
    public LogicHandlerTest() {
    }
    

    /**
     * Test of loadKeystore method, of class LogicHandler.
     * @throws KeyStoreException
     */
    @Test
    public void testLoadKeystore() throws KeyStoreException {
        LogicHandler instance = new LogicHandler();
        KeyStore result = instance.loadKeystore("keystore.ks", "123456".toCharArray());
        assertEquals(result.getClass(), KeyStore.class);
    }

    /**
     * Test of storeKeys method, of class LogicHandler.
     */
    @Test
    public void testStoreKeys() {
        System.out.println("storeKeys");
        KeyStore ks = null;
        LogicHandler instance = new LogicHandler();
        instance.storeKeys(ks);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of establishConnection method, of class LogicHandler.
     */
    @Test
    public void testEstablishConnection() {
        System.out.println("establishConnection");
        LogicHandler instance = new LogicHandler();
        instance.establishConnection();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of extractKeys method, of class LogicHandler.
     */
    // TODO: fix test
    @Test
    public void testExtractKeys() throws Exception {
        System.out.println("extractKeys");
        LogicHandler instance = new LogicHandler();
        KeyStore ks = instance.loadKeystore("keystore.ks", "123456".toCharArray());
        PublicKey key = instance.extractKeys(ks);
        // assertEquals(PublicKey.class, key.getClass());
    }

    /**
     * Test of validatePath method, of class LogicHandler.
     */
    @Test
    public void testValidatePath() {
        String path = "keystore.ks";
        LogicHandler instance = new LogicHandler();
        Boolean expResult = true;
        Boolean result = instance.validatePath(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPassword method, of class LogicHandler.
     */
    @Test
    public void testValidPassword() {
        LogicHandler instance = new LogicHandler();
        boolean expResult = true;
        boolean result = instance.validatePass("123456".toCharArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testInvalidPassword() {
        LogicHandler instance = new LogicHandler();
        boolean expResult = false;
        boolean result = instance.validatePass("1234".toCharArray());
        assertEquals(expResult, result);
    }
    
}
