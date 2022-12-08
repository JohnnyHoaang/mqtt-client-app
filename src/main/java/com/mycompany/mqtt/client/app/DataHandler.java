package com.mycompany.mqtt.client.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.concurrent.Task;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.json.*;

public class DataHandler {
    private MqttHandler mqtt;
    private Mqtt5BlockingClient client;
    private HashMap storedUsersPublicKeys;
    private FXDashboard dashboard;
    private SecurityHandler instance;
    private String ksPath;
    private char[] ksPassword;

    public DataHandler(MqttHandler mqtt, Mqtt5BlockingClient client, HashMap storedUsersPublicKeys, FXDashboard dashboard,
            String ksPath, char[] ksPassword) {
        this.mqtt = mqtt;
        this.client = client;
        this.storedUsersPublicKeys = storedUsersPublicKeys;
        this.dashboard = dashboard;
        this.ksPath = ksPath;
        this.ksPassword = ksPassword;
        this.instance = new SecurityHandler();
    }

    /**
     * Retieve sensor data from the mqtt server
     */
    public void retrieveData() {

        // subscribe to all topics under sensor
        mqtt.subscribeToTopic(this.client, "certificate/#");
        mqtt.subscribeToTopic(this.client, "sensor/#");

        mqtt.messageReceived(this.client);

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                while (true) {
                    if (isCancelled()) {
                        break;
                    }
                    try {
                        // used to update display with data retieved from mqtt server
                        Thread.sleep(100);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    handleResult(mqtt.getResult());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (Exception e) {

                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Parse data accordingly and send to appropriate methods to be displayed
     * 
     * @param result
     */
    private void handleResult(String result) throws IOException {
        try {
            if (!result.equals("")) {
                // parse data to get topic and info
                String[] informations = result.split("'");
                JSONObject json = new JSONObject(informations[1]);
                String[] topics = informations[0].split("/");
                if (json.has("certificate")) {
                    String user = topics[1];
                    String certString = json.get("certificate").toString();

                    // get certificate and save it to the keystore
                    InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(certString));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                    mqtt.getKeyStore().setCertificateEntry(user, cert);
                }
                // check sensor type and display accordingly
                if (topics[0].equals("sensor")) {
                    String sensorType = topics[1];
                    String user = topics[2];
                    PublicKey publicKey = null;

                    if (mqtt.getKeyStore().getCertificate(user) == null) {
                        System.out.println("No matching certificate for user " + user);
                    } else {
                        Certificate cert = mqtt.getKeyStore().getCertificate(user);
                        publicKey = cert.getPublicKey();
                        // Add user public key to public key collection
                        this.storedUsersPublicKeys.put(user, publicKey);
                        instance.saveKeystoreToFile(mqtt.getKeyStore(), this.ksPath, this.ksPassword);
                    }
                    // Update user tiles with MQTT information and signature verificction using user
                    // public key
                    switch (sensorType) {
                        case "buzzer":
                            sensorTimeUpdate(json, user, (PublicKey) this.storedUsersPublicKeys.get(user), "buzzer");
                            break;
                        case "motion":
                            sensorTimeUpdate(json, user, (PublicKey) this.storedUsersPublicKeys.get(user), "motion");
                            break;
                        case "humidity":
                            sensorAmbientUpdate(json, user, (PublicKey) this.storedUsersPublicKeys.get(user));
                            break;
                        default:
                            break;
                    }
                }
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }

    }

    /**
     * Performs signature verification for ambient information and update
     * corresponding user tiles
     * 
     * @param json
     * @param user
     * @param publicKey
     */
    private void sensorAmbientUpdate(JSONObject json, String user, PublicKey publicKey) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        System.out.println("Received from mqtt: " + json.toString());

        // get signed data from received data and verify it
        byte[] temperatureSignature = Base64.getDecoder().decode(json.get("signedTemp").toString());
        byte[] humiditySignature = Base64.getDecoder().decode(json.get("signedHum").toString());
        double temperature = Double.parseDouble(json.get("temperature").toString());
        double humidity = Double.parseDouble(json.get("humidity").toString());
        byte[] signatureMotionTimeBytes = Base64.getDecoder().decode(json.get("signedTime").toString());
        // only update tiles if data was successfully verified
        boolean sensorTimeCheck = this.instance.verifySignature(signatureMotionTimeBytes, publicKey,
                "SHA256withECDSA", json.get("time").toString());
        boolean temperatureCheck = this.instance.verifySignature(temperatureSignature, publicKey,
                "SHA256withECDSA", json.get("temperature").toString());
        boolean humidityCheck = this.instance.verifySignature(humiditySignature, publicKey,
                "SHA256withECDSA", json.get("humidity").toString());
        System.out.println("Verified Signature");
        if (temperatureCheck && sensorTimeCheck) {
            System.out.println("Updating ambient tile");
            dashboard.updateTempHum(user, temperature, humidity);
        }
    }

    /**
     * Performs signature verification for time and update corresponding user tiles
     * 
     * @param json
     * @param user
     * @param publicKey
     * @param type
     */
    private void sensorTimeUpdate(JSONObject json, String user, PublicKey publicKey, String type)
            throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        System.out.println("Received from mqtt: " + json.toString());

        // get signed message from received data and verify it
        byte[] signatureMotionTimeBytes = Base64.getDecoder().decode(json.get("signedTime").toString());
        boolean sensorTimeCheck = this.instance.verifySignature(signatureMotionTimeBytes, publicKey,
                "SHA256withECDSA", json.get("time").toString());

        // only display data if it passes verification
        if (sensorTimeCheck) {
            String sensorDate = json.get("time").toString().substring(1, 10);
            String sensorTime = json.get("time").toString().substring(11, 22);
            String sensorTimestamp = sensorDate + " | " + sensorTime;
            if (type.equals("buzzer")) {
                System.out.println("Updating buzzer tile");
                dashboard.updateTimeTile(user, sensorTimestamp, 6, 7, 8);
            } else if (type.equals("motion")) {
                System.out.println("Updating motion tile");
                dashboard.updateTimeTile(user, sensorTimestamp, 9, 10, 11);
            }
        }
    }
}