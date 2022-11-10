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
    public void run(){
        final String host = "3e093932f5a140289ec08eb559057c33.s2.eu.hivemq.cloud";
        String username = getUsername();
        String password = getPassword();
        String topic = "my/test/topic";
        Mqtt5BlockingClient client = createClient(host);
        connectClient(client, username, password);
        subscribeToTopic(client, topic);
        messageReceived(client);
        publishMessage(client, topic);
    }
    /**
     * Gets username
     * 
     */
    public String getUsername(){
        boolean check = false;
        String username ="";
        while(check == false){
            System.out.println("Enter Username:");
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
            System.out.println("Enter Password:");
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
        System.out.println("Client Created");
        return client;
    }
    
    /**
     *
     * Connects Client to server
     */
    public void connectClient(Mqtt5BlockingClient client, String username, String password){
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();
        System.out.println("Connected Successfully");
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
            System.out.println("Received message: " +
                publish.getTopic() + " -> " +
                UTF_8.decode(publish.getPayload().get()));
            client.disconnect();
        });
    }
    
    /**
     *
     * Publishes message
     */
    public void publishMessage(Mqtt5BlockingClient client, String topic){
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode("Hello"))
                .send();
    }
}
