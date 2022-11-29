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
    private String topicUser;
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<VBox> vboxs = new ArrayList<VBox>();
    private ArrayList<HBox> hboxs = new ArrayList<HBox>();
    private int PREF_WIDTH = 300;
    private int PREF_HEIGHT = 200;

    public String test = "";

    public FXDashboard(MqttRun mqtt, String topicUser){
        
            this.mqtt = mqtt;
            this.client = mqtt.run();
            this.topicUser = topicUser;
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
                titleTemp = "Temperature - Alexandre";
                titleHum = "Humidity - Alexandre";
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

        createTextTiles("BUZZER - ", "Timesatamp when buzzer is pressed");

        createTextTiles("MOTION - ", "Timestamp when motion is detected");

        for (int i = 0; i < 3; i++) {
            String prefix = "IMAGE - ";
            String title = "";
            if (i == 0) {
                title = prefix + "Johnny";
            } else if(i == 1) {
                title = prefix +"Alexandre";
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
        num = 6;
        VBox tilesColumnTempHumid = new VBox();
        for (HBox hBox : hboxs) {
            tilesColumnTempHumid.getChildren().add(hBox);
        }

        vboxs.add(tilesColumnTempHumid);
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

        runSensors(); 

                
         

    }

    private void createTextTiles(String prefix, String desc) {
        var title = "";
        for (int i = 0; i < 3; i++) {;
            if (i == 0) {
                title = prefix + "Johnny";
            } else if(i == 1) {
                title = prefix + "Alexandre";
            } else {
                title = prefix + "Katharina";
            }
            var buzzer = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(PREF_WIDTH, PREF_HEIGHT)
                .title(title)
                .text(desc)
                .description("Timstamp:")
                .descriptionAlignment(Pos.BASELINE_LEFT)
                .textVisible(true)
                .build();

            tiles.add(buzzer);
        }
    }
    public void runSensors(){
        HumidityApp humidity = new HumidityApp(mqtt , client, topicUser);
        humidity.sensorLoop();
        BuzzerApp buzzer = new BuzzerApp(mqtt, client, topicUser);
        buzzer.sensorLoop();
        MotionSensorApp motion = new MotionSensorApp(mqtt, client, topicUser);
        motion.sensorLoop();
        mqtt.subscribeToTopic(client,"example/+/johnny/");
        mqtt.messageReceived(client);
       

        Task task = new Task<Void>() {
            @Override public Void call() {
                while(true){
                    if (isCancelled()) {
                       break;
                    }
                    try {
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
    private void handleResult(String result){
        
        String [] informations = result.split("'");
        JSONObject json = new JSONObject(informations[1]);
        String []topics = informations[0].split("/");
        if(topics[0].equals("example")){
            String sensorType = topics[1];
            String user = topics[2];
            switch(sensorType){
                case "buzzer":
                    String buzzerTime = json.get("time").toString();
                    if(user.equals("johnny")){
                        tiles.get(6).setDescription(buzzerTime);
                    }
                    break;
                case "motion":
                    String motionTime = json.get("time").toString();
                    if(user.equals("johnny")){
                        tiles.get(9).setDescription(motionTime);
                    }
                    break;
                case "humidity":
                    double humidity = Double.parseDouble(json.get("humidity").toString());
                    double temperature = Double.parseDouble(json.get("temperature").toString());
                    if(user.equals("johnny")){
                        tiles.get(0).setValue(temperature);
                        tiles.get(1).setValue(humidity);
                    }
                    break;
                default:
                    break;
            }
        } else {
            
        }
    }
    
    
}
