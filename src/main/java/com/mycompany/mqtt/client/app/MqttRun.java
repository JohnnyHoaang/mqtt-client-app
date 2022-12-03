/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mqtt.client.app;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.util.Scanner;
/**
 *
 * @author 2043441
 */
public class MqttRun {
    private KeyStore ks;
    private String result = "";
    Console cnsl = System.console();
    Scanner sc = new Scanner(System.in);
    public Mqtt5BlockingClient run() throws InvocationTargetException{
        final String host = "061d9ed673164eda847418a5b5609221.s2.eu.hivemq.cloud";
        String username = getUsername();
        String password = getPassword();
        Mqtt5BlockingClient client = createClient(host);
        System.out.println("\nConnecting...");
        connectClient(client, username, password);
        return client;
    }
//    public MqttRun(KeyStore ks){
//        this.ks = ks;
//    }
    /**
     * Gets username
     * 
     */
    public String getUsername(){
        boolean check = false;
        String username ="";
        while(check == false){
            System.out.println("\nEnter Username:");
            //username = sc.nextLine();
            username = System.console().readLine();
            if(username.length() <= 30){
                check = true;
            }
        }   
        return username;
    }
    /**
     *
     * Gets Password
     */
    public String getPassword(){
        boolean check = false;
        char[] password ={};
        while(check == false){
            System.out.println("\nEnter Password:");
            password = cnsl.readPassword();
            //password = sc.nextLine();
            if(password.length >= 8){
                check = true;
            }
        }
        String pass = String.valueOf(password);
        return pass;
    }
    
    /**
     *
     * Creates Client
     */
    public Mqtt5BlockingClient createClient(String host){
        Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();
        System.out.println(Colors.GREEN + "\nClient Created" + Colors.RESET);
        return client;
    }
    
    /**
     *
     * Connects Client to server
     */
    public void connectClient(Mqtt5BlockingClient client, String username, String password) throws InvocationTargetException{

        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(UTF_8.encode(password))
            .applySimpleAuth()
            .send();

        System.out.println(Colors.GREEN + "\nConnected Successfully" + Colors.RESET);
    }
    
    /**
     *
     * Subscribes to Topic of users choice
     */
    public void subscribeToTopic(Mqtt5BlockingClient client, String topic){
        client.subscribeWith()
                .topicFilter(topic)
                .send();
    }
    
    /**
     *
     * Confirms messaged received
     */
    public void messageReceived(Mqtt5BlockingClient client){
        System.out.println("start");
        Thread thread = new Thread(()->{
            client.toAsync().publishes(ALL, publish -> {
            this.result = publish.getTopic() + "'" + UTF_8.decode(publish.getPayload().get()).toString();
        });
        });
        thread.start();
        System.out.println("end");
    }
    public void close(Mqtt5BlockingClient client){
        client.disconnect();
    }
    public String getResult(){
        return this.result;
    }
    public KeyStore getKeyStore(){
        return this.ks;
    }
    public void setKeyStore(KeyStore ks){
        this.ks = ks;
    }
    /**
     *
     * Publishes message
     */
    public void publishMessage(Mqtt5BlockingClient client, String topic, byte[] message){

        client.publishWith()
                .topic(topic)
                .payload(message)
                .send();
    }
}
