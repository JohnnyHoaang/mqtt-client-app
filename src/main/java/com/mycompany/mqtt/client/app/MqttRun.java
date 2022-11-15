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
import java.util.Scanner;
/**
 *
 * @author 2043441
 */
public class MqttRun {
    Console cnsl = System.console();
    Scanner sc = new Scanner(System.in);
    public Mqtt5BlockingClient run(){
        final String host = "a70d21edc42b4ad59d04019f79dd252c.s2.eu.hivemq.cloud";
        String username = getUsername();
        String password = getPassword();
        // String topic = "my/test/topic";
        Mqtt5BlockingClient client = createClient(host);
        connectClient(client, username, password);
        // subscribeToTopic(client, topic);
        // messageReceived(client);
        // publishMessage(client, topic);
        return client;
    }
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
        System.out.println("\nClient Created");
        return client;
    }
    
    /**
     *
     * Connects Client to server
     */
    public void connectClient(Mqtt5BlockingClient client, String username, String password){
        try {
            client.connectWith()
            .simpleAuth()
            .username(username)
            .password(UTF_8.encode(password))
            .applySimpleAuth()
            .send();
            System.out.println("\nConnected Successfully");
        } catch (Exception e) {
            System.out.println(Colors.RED + "\nUnable to connect, ensure username and password are correct or try again later" + Colors.RESET);
            
        }

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
        client.toAsync().publishes(ALL, publish -> {
            System.out.println("\nReceived message: " +
                publish.getTopic() + " -> " +
                UTF_8.decode(publish.getPayload().get()));
            client.disconnect();
        });
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
