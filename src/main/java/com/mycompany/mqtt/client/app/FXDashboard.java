package com.mycompany.mqtt.client.app;

import java.io.ByteArrayInputStream;
import java.io.Console;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.Tile.ImageMask;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.concurrent.Task;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.json.*;

public class FXDashboard extends HBox {

    private MqttRun mqtt;
    private Mqtt5BlockingClient client;
    private LogicHandler instance;
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<VBox> vboxs = new ArrayList<VBox>();
    private ArrayList<HBox> hboxs = new ArrayList<HBox>();
    private int PREF_WIDTH = 300;
    private int PREF_HEIGHT = 200;
    private HashMap storedUsersPublicKeys = new HashMap<String, PublicKey>();
    Console console = System.console();

    public FXDashboard(MqttRun mqtt, String topicUser) {
        this.instance = new LogicHandler();
        String ksPath = console.readLine("Enter Keystore path: ");
        char[] password = console.readPassword("Enter Keystore password: ");
        var ks = this.instance.loadKeystore(ksPath, password);
        this.mqtt = mqtt;
        mqtt.setKeyStore(ks);
        this.client = mqtt.run();

        try {
            this.buildScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildScreen() throws IOException {

        // tiles for temperature and humidity, one for each member
        for (int i = 0; i < 3; i++) {
            String titleTemp = "";
            String titleHum = "";
            if (i == 0) {
                titleTemp = "Temperature - Johnny";
                titleHum = "Humidity - Johnny";
            } else if (i == 1) {
                titleTemp = "Temperature - Alexander";
                titleHum = "Humidity - Alexander";
            } else {
                titleTemp = "Temperature - Katharina";
                titleHum = "Humidity - Katharina";
            }
            var gaugeTile = TileBuilder.create()
                    .skinType(Tile.SkinType.GAUGE)
                    .prefSize(PREF_WIDTH / 2, PREF_HEIGHT)
                    .title(titleTemp)
                    .text("Temperature")
                    .unit("Celsius")
                    .textVisible(true)
                    .textAlignment(TextAlignment.LEFT)
                    .value(0)
                    .threshold(75)
                    .animated(true)
                    .build();

            tiles.add(gaugeTile);

            var percentageTile = TileBuilder.create()
                    .skinType(Tile.SkinType.PERCENTAGE)
                    .prefSize(PREF_WIDTH / 2, PREF_HEIGHT)
                    .title(titleHum)
                    .unit("Percent")
                    .description("Humidity")
                    .maxValue(60)
                    .build();

            tiles.add(percentageTile);
        }

        // group temperature and humidity tiles together to look like one tile
        var num = 0;
        for (int i = 0; i < 3; i++) {

            var row = new HBox();

            if (i == 1)
                num = 2;
            if (i == 2)
                num = 4;
            for (int j = num; j < num + 2; j++) {
                row.getChildren().add(tiles.get(j));
            }
            hboxs.add(row);
        }

        // call method to create text tiles for buzzer and motion timestamps
        createTextTiles("BUZZER - ", "Timesatamp when buzzer is pressed");
        createTextTiles("MOTION - ", "Timestamp when motion is detected");

        // Create image tile for each member
        for (int i = 0; i < 3; i++) {
            String prefix = "IMAGE - ";
            String title = "";
            if (i == 0) {
                title = prefix + "Johnny";
            } else if (i == 1) {
                title = prefix + "Alexander";
            } else {
                title = prefix + "Katharina";
            }
            var image = TileBuilder.create()
                    .skinType(SkinType.IMAGE)
                    .prefSize(PREF_WIDTH, PREF_HEIGHT)
                    .title(title)
                    .image(null)
                    .imageMask(ImageMask.RECTANGULAR)
                    .text("Image taken when motion is detected")
                    .textAlignment(TextAlignment.LEFT)
                    .textVisible(true)
                    .build();

            tiles.add(image);
        }

        VBox tilesColumnTempHumid = new VBox();

        // add tiles to vbox
        for (HBox hBox : hboxs) {
            tilesColumnTempHumid.getChildren().add(hBox);
        }

        vboxs.add(tilesColumnTempHumid);

        // indicate where to start reading the tile list
        num = 6;
        for (int i = 0; i < 3; i++) {

            var column = new VBox();
            if (i == 1)
                num = 9;
            if (i == 2)
                num = 12;

            for (int j = num; j < num + 3; j++) {
                column.getChildren().add(tiles.get(j));
            }
            column.setSpacing(5);
            vboxs.add(column);
        }
        tilesColumnTempHumid.setMinWidth(PREF_WIDTH);
        tilesColumnTempHumid.setSpacing(5);

        for (VBox vbox : vboxs) {
            this.getChildren().add(vbox);
        }
        this.setSpacing(5);

        retrieveData();
    }

    /**
     * Create text tiles for timestamps for each member
     * 
     * @param prefix
     * @param desc
     */
    private void createTextTiles(String prefix, String desc) {
        var title = "";
        for (int i = 0; i < 3; i++) {
            ;
            if (i == 0) {
                title = prefix + "Johnny";
            } else if (i == 1) {
                title = prefix + "Alexander";
            } else {
                title = prefix + "Katharina";
            }
            var buzzer = TileBuilder.create()
                    .skinType(SkinType.TEXT)
                    .prefSize(PREF_WIDTH, PREF_HEIGHT)
                    .title(title)
                    .text(desc)
                    .description("Timestamp")
                    .descriptionAlignment(Pos.BASELINE_LEFT)
                    .textVisible(true)
                    .build();

            tiles.add(buzzer);
        }
    }

    /**
     * Retieve sensor data from the mqtt server
     */
    public void retrieveData() {

        // subscribe to all topics under sensor
        mqtt.subscribeToTopic(client, "certificate/#");
        mqtt.subscribeToTopic(client, "sensor/#");

        mqtt.messageReceived(client);

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

                    InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(certString));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                    mqtt.getKeyStore().setCertificateEntry(user, cert);
                    // TODO: Save Keystore into a file
                }
                // check sensor type and display accordingly
                if (topics[0].equals("sensor")) {
                    String sensorType = topics[1];
                    String user = topics[2];
                    PublicKey publicKey = null;

                    if(mqtt.getKeyStore().getCertificate(user) == null){
                        System.out.println("No matching certificate for user " + user);
                    } else {
                        Certificate cert = mqtt.getKeyStore().getCertificate(user);
                        publicKey = cert.getPublicKey();
                        // Add user public key to public key collection
                        this.storedUsersPublicKeys.put(user, publicKey);
                    }
                    // Update user tiles with MQTT information and signature verificction using user public key
                    switch (sensorType) {
                        case "buzzer":
                            sensorTimeUpdate(json, user, (PublicKey)this.storedUsersPublicKeys.get(user) , "buzzer");
                            break;
                        case "motion":
                            sensorTimeUpdate(json, user, (PublicKey)this.storedUsersPublicKeys.get(user), "motion");
                            break;
                        case "humidity":
                            sensorAmbientUpdate(json, user, (PublicKey)this.storedUsersPublicKeys.get(user));
                            break;
                        default:
                            break;
                    }
                } else {

                }
            }

        } catch (Exception e) {
        }

    }
    /**
     * Performs signature verification for ambient information and update corresponding user tiles
     * @param json
     * @param user
     * @param publicKey
     */
    private void sensorAmbientUpdate(JSONObject json, String user, PublicKey publicKey) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        byte[] temperatureSignature = Base64.getDecoder().decode(json.get("signedTemp").toString());
        byte[] humiditySignature = Base64.getDecoder().decode(json.get("signedHum").toString());
        double temperature = Double.parseDouble(json.get("temperature").toString());
        double humidity = Double.parseDouble(json.get("humidity").toString());

        boolean temperatureCheck = this.instance.verifySignature(temperatureSignature, publicKey,
                "SHA256withECDSA", json.get("temperature").toString());
        boolean humidityCheck = this.instance.verifySignature(humiditySignature, publicKey,
                "SHA256withECDSA", json.get("humidity").toString());
        if (temperatureCheck) {
            updateTempHum(user, temperature, humidity);
        }
    }
    /**
     * Performs signature verification for time and update corresponding user tiles
     * @param json
     * @param user
     * @param publicKey
     * @param type
     */
    private void sensorTimeUpdate(JSONObject json, String user, PublicKey publicKey, String type)
            throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        byte[] signatureMotionTimeBytes = Base64.getDecoder().decode(json.get("signedTime").toString());
        boolean sensorTimeCheck = this.instance.verifySignature(signatureMotionTimeBytes, publicKey,
                "SHA256withECDSA", json.get("time").toString());
        if (sensorTimeCheck) {
            String sensorDate = json.get("time").toString().substring(1, 10);
            String sensorTime = json.get("time").toString().substring(11, 22);
            String sensorTimestamp = sensorDate + " | " + sensorTime;
            if (type.equals("buzzer")) {
                updateTimeTile(user, sensorTimestamp, 6, 7, 8);
            } else if (type.equals("motion")) {
                updateTimeTile(user, sensorTimestamp, 9, 10, 11);
            }
        }
    }

    /**
     * Update corresponding time tiles for buzzer and motion sensor
     * 
     * @param user
     * @param time
     */
    private void updateTimeTile(String user, String time, int firstRow, int secondRow, int thirdRow) {
        if (user.equals("johnny")) {
            tiles.get(firstRow).setDescription(time);
        } else if (user.equals("alexander")) {
            tiles.get(secondRow).setDescription(time);
        } else if (user.equals("katharina")) {
            tiles.get(thirdRow).setDescription(time);
        }
    }

    /**
     * Update corresponding tiles for temperature/humididty sensor
     * 
     * @param user
     * @param time
     */
    private void updateTempHum(String user, double temperature, double humidity) {
        if (user.equals("johnny")) {
            tiles.get(0).setValue(temperature);
            tiles.get(1).setValue(humidity);
        } else if (user.equals("alexander")) {
            tiles.get(2).setValue(temperature);
            tiles.get(3).setValue(humidity);
        } else if (user.equals("katharina")) {
            tiles.get(4).setValue(temperature);
            tiles.get(5).setValue(humidity);
        }
    }
}
