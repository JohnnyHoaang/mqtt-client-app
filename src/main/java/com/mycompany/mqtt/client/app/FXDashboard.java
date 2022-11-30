package com.mycompany.mqtt.client.app;

import java.io.IOException;
import java.util.ArrayList;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.Tile.ImageMask;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
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
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<VBox> vboxs = new ArrayList<VBox>();
    private ArrayList<HBox> hboxs = new ArrayList<HBox>();
    private int PREF_WIDTH = 300;
    private int PREF_HEIGHT = 200;

    public FXDashboard(MqttRun mqtt, String topicUser){
        
        this.mqtt = mqtt;
        this.client = mqtt.run();
        try {
            this.buildScreen();
        } catch (IOException e){
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
            } else if(i == 1) {
                titleTemp = "Temperature - Alexander";
                titleHum = "Humidity - Alexander";
            } else {
                titleTemp = "Temperature - Katharina";
                titleHum = "Humidity - Katharina";
            }
            var gaugeTile = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE)
                .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
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
                .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
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

            if(i == 1)
                num = 2;
            if(i == 2) 
                num = 4;
            for (int j = num; j < num+2; j++) {
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
            } else if(i == 1) {
                title = prefix +"Alexander";
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
            if(i == 2) 
                num = 12;
            
            for (int j = num; j < num+3; j++) {
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
     * @param prefix
     * @param desc
     */
    private void createTextTiles(String prefix, String desc) {
        var title = "";
        for (int i = 0; i < 3; i++) {;
            if (i == 0) {
                title = prefix + "Johnny";
            } else if(i == 1) {
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
    public void retrieveData(){

        // subscribe to all topics under sensor
        mqtt.subscribeToTopic(client,"sensor/#");

        mqtt.messageReceived(client);
       
        Task task = new Task<Void>() {
            @Override public Void call() {
                while(true){
                    if (isCancelled()) {
                       break;
                    }
                    try {
                        // used to update display with data retieved from mqtt server
                        Thread.sleep(100);
                        Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            handleResult(mqtt.getResult());
                        }});
                    } catch(Exception e){

                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Parse data accordingly and send to appropriate methods to be displayed
     * @param result
     */
    private void handleResult(String result){
        
        try {
            // parse data to get topic and info
            String [] informations = result.split("'");
            JSONObject json = new JSONObject(informations[1]);
            String []topics = informations[0].split("/");

            // check sensor type and display accordingly
            if(topics[0].equals("sensor")){
                String sensorType = topics[1];
                String user = topics[2];
                switch(sensorType){
                    case "buzzer":
                        String buzzerDate = json.get("time").toString().substring(0, 10);
                        String buzzerTime = json.get("time").toString().substring(11, 22);
                        String buzzerTimestamp = buzzerDate + " | " + buzzerTime;
                        updateBuzzerTile(user, buzzerTimestamp);
                        break;
                    case "motion":
                        String motionDate = json.get("time").toString().substring(1, 10);
                        String motionTime = json.get("time").toString().substring(11, 22);
                        String motionTimestamp = motionDate + " | " + motionTime;
                        updateMotionTile(user, motionTimestamp);
                        break;
                    case "humidity":
                        double temperature = Double.parseDouble(json.get("temperature").toString());
                        double humidity = Double.parseDouble(json.get("humidity").toString());
                        updateTempHum(user, temperature, humidity);
                        break;
                    default:
                        break;
                }
            } else {
                
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    
    /**
     * Update corresponding tiles for buzzer sensor
     * @param user
     * @param time
     */
    private void updateBuzzerTile(String user, String time) {
        if(user.equals("johnny")){
            tiles.get(6).setDescription(time);
        } else if(user.equals("alexander")) {
            tiles.get(7).setDescription(time);
        } else if(user.equals("katharina")) {
            tiles.get(8).setDescription(time);
        }
    }

    /**
     * Update corresponding tiles for motion sensor
     * @param user
     * @param time
     */
    private void updateMotionTile(String user, String time) {
        if(user.equals("johnny")){
            tiles.get(9).setDescription(time);
        } else if(user.equals("alexander")) {
            tiles.get(10).setDescription(time);
        } else if(user.equals("katharina")) {
            tiles.get(11).setDescription(time);
        }
    }

    /**
     * Update corresponding tiles for temperature/humididty sensor
     * @param user
     * @param time
     */
    private void updateTempHum(String user, double temperature, double humidity) {
        if(user.equals("johnny")){
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
