package com.mycompany.mqtt.client.app;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.Tile.ImageMask;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class FXDashboard extends HBox {

    private MqttHandler mqtt;
    private Mqtt5BlockingClient client;
    private SecurityHandler instance;
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<VBox> vboxs = new ArrayList<VBox>();
    private ArrayList<HBox> hboxs = new ArrayList<HBox>();
    private int PREF_WIDTH = 300;
    private int PREF_HEIGHT = 200;
    private HashMap storedUsersPublicKeys = new HashMap<String, PublicKey>();
    private DataHandler dataInstance;
    Console console = System.console();

    public FXDashboard(MqttHandler mqtt) {
        this.instance = new SecurityHandler();
        String ksPath = null;
        char[] password = null;
        KeyStore ks = null;
        do {
            ksPath = console.readLine("Enter Keystore path: ");
            password = console.readPassword("Enter Keystore password: ");
            ks = this.instance.loadKeystore(ksPath, password);
        } while (ks == null);

        mqtt.setKeyStore(ks);
        this.mqtt = mqtt;
        login();
        this.dataInstance = new DataHandler(mqtt, client, storedUsersPublicKeys, this, ksPath, password);
        try {
            this.buildScreen();
        } catch (IOException e) {
            // e.printStackTrace();
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

            InputStream is = new FileInputStream("src\\assets\\not_found.png");
            
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

        dataInstance.retrieveData();;
    }

    /**
     * prompt user for credentials until successfully logged in to mqtt
     */
    private void login() {
        boolean askCredentials = true;
        while(askCredentials){
            try {
                System.out.println("MQTT Credentials");
                this.client = mqtt.run();
                askCredentials = false;
            } catch (Exception e) {
                System.out.println(Colors.RED + "\nInvalid credentials, please try again" + Colors.RESET);
            }
        }
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
     * Update corresponding tiles for buzzer sensor
     * @param user
     * @param time
     */
    public void updateBuzzerTile(String user, String time) {
        if(user.equals("johnny") || user.equals("carlton")){
            tiles.get(6).setDescription(time);
        } else if(user.equals("alexander")) {
            tiles.get(7).setDescription(time);
        } else if(user.equals("katharina")) {
            tiles.get(8).setDescription(time);
        }
    }

    /**
     * Update corresponding time tiles for buzzer and motion sensor
     * 
     * @param user
     * @param time
     */
    public void updateTimeTile(String user, String time, int firstRow, int secondRow, int thirdRow) {
        if (user.equals("johnny") || user.equals("carlton")) {
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
    public void updateTempHum(String user, double temperature, double humidity) {
        if (user.equals("johnny") || user.equals("carlton")) {
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

    public void updateImage(String user, byte[] pictureByteArray) {
        Image img = new Image(new ByteArrayInputStream(pictureByteArray));
        if (user.equals("johnny")) {
            tiles.get(13).setImage(img);
        } else if (user.equals("alexander")) {
            tiles.get(14).setImage(img);
        } else if (user.equals("katharina")) {
            tiles.get(15).setImage(img);
        }
    }
}
