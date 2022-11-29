package com.mycompany.mqtt.client.app;

import java.io.IOException;
import java.util.ArrayList;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class FXDashboard extends HBox {
    
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<VBox> vboxs = new ArrayList<VBox>();
    private ArrayList<HBox> hboxs = new ArrayList<HBox>();

    public FXDashboard() throws IOException {
        this.buildScreen();
    }

    private void buildScreen() throws IOException {

        var PREF_WIDTH = 300;
        var PREF_HEIGHT = 200;

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

        for (int i = 0; i < 3; i++) {
            String title = "BUZZER - ";
            if (i == 0) {
                title += "Johnny";
            } else if(i == 1) {
                title += "Alexandre";
            } else {
                title += "Katharina";
            }
            var buzzer = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(PREF_WIDTH, PREF_HEIGHT)
                .title(title)
                .text("TimeStamp when buzzer is pressed")
                .description("Timstamp:")
                .descriptionAlignment(Pos.BASELINE_LEFT)
                .textVisible(true)
                .build();

            tiles.add(buzzer);
        }

        for (int i = 0; i < 3; i++) {
            String title = "MOTION DETECTOR - ";
            if (i == 0) {
                title += "Johnny";
            } else if(i == 1) {
                title += "Alexandre";
            } else {
                title += "Katharina";
            }
            var motion = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(PREF_WIDTH, PREF_HEIGHT)
                .title(title)
                .text("TimeStamp when motion is detected")
                .description("Timstamp:")
                .descriptionAlignment(Pos.BASELINE_LEFT)
                .textVisible(true)
                .build();

            tiles.add(motion);
        }

        for (int i = 0; i < 3; i++) {
            String title = "IMAGE - ";
            if (i == 0) {
                title += "Johnny";
            } else if(i == 1) {
                title += "Alexandre";
            } else {
                title += "Katharina";
            }
            var image = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(PREF_WIDTH, PREF_HEIGHT)
                .title(title)
                .text("Image taken when motion is detected")
                .description("Timstamp:")
                .descriptionAlignment(Pos.BASELINE_LEFT)
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
    }
}
