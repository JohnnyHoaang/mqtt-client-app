package com.mycompany.mqtt.client.app;

import java.io.IOException;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.Tile.ImageMask;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class FXDashboard extends HBox {
    
    public FXDashboard() throws IOException {
        this.buildScreen();
    }
    private void buildScreen() throws IOException {

        var PREF_WIDTH = 300;
        var PREF_HEIGHT = 200;

        var gaugeTileJ = TileBuilder.create()
            .skinType(Tile.SkinType.GAUGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Temp Gauge")
            .text("Temperature")
            .unit("Celsius")
            .textVisible(true)
            .textAlignment(TextAlignment.LEFT)
            .value(0)
            .threshold(75)
            .animated(true)
            .build();
        
        var percentageTileJ = TileBuilder.create()
            .skinType(Tile.SkinType.PERCENTAGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Humidity")
            .unit("Percent")
            .description("Humidity")
            .maxValue(60)
            .build();

        var gaugeTileA = TileBuilder.create()
            .skinType(Tile.SkinType.GAUGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Temp Gauge")
            .text("Temperature")
            .unit("Celsius")
            .textVisible(true)
            .textAlignment(TextAlignment.LEFT)
            .value(0)
            .threshold(75)
            .animated(true)
            .build();
        
        var percentageTileA = TileBuilder.create()
            .skinType(Tile.SkinType.PERCENTAGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Humidity")
            .unit("Percent")
            .description("Humidity")
            .maxValue(60)
            .build();

        var gaugeTileK = TileBuilder.create()
            .skinType(Tile.SkinType.GAUGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Temp Gauge")
            .text("Temperature")
            .unit("Celsius")
            .textVisible(true)
            .textAlignment(TextAlignment.LEFT)
            .value(0)
            .threshold(75)
            .animated(true)
            .build();
        
        var percentageTileK = TileBuilder.create()
            .skinType(Tile.SkinType.PERCENTAGE)
            .prefSize(PREF_WIDTH/2, PREF_HEIGHT)
            .title("Humidity")
            .unit("Percent")
            .description("Humidity")
            .maxValue(60)
            .build();

        //Setup tile with TextArea to display output from external program
        TextArea textArea = new TextArea();

        //Make the TextArea non editable
        textArea.setEditable(false);

        /*Change the background and the font color of the TextArea
           and make the border of the TextArea transparent
         */
        textArea.setStyle("-fx-control-inner-background: #2A2A2A; "
                + "-fx-text-inner-color: white;"
                + "-fx-text-box-border: transparent;");

        //Write output to TextArea
        textArea.setText("\n\nOUTPUT");

        HBox tempHumidJ = new HBox(gaugeTileJ, percentageTileJ);
        HBox tempHumidA = new HBox(gaugeTileA, percentageTileA);
        HBox tempHumidK = new HBox(gaugeTileK, percentageTileK);

        var buzzerJ = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(PREF_WIDTH, PREF_HEIGHT)
                .title("Johnny Buzzer")
                .text("TimeStamp when buzzer is pressed")
                .description("Timstamp:")
                .descriptionAlignment(Pos.BASELINE_LEFT)
                .textVisible(true)
                .build();

        var buzzerA = TileBuilder.create()
            .skinType(SkinType.TEXT)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Alexandre Buzzer")
            .text("TimeStamp when buzzer is pressed")
            .description("Timstamp:")
            .descriptionAlignment(Pos.BASELINE_LEFT)
            .textVisible(true)
            .build();

        var buzzerK = TileBuilder.create()
            .skinType(SkinType.TEXT)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Katharina Buzzer")
            .text("TimeStamp when buzzer is pressed")
            .description("Timstamp:")
            .descriptionAlignment(Pos.BASELINE_LEFT)
            .textVisible(true)
            .build();


        var motionJ = TileBuilder.create()
            .skinType(SkinType.TEXT)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Johnny Motion Detector")
            .text("TimeStamp when motion is detected")
            .description("Timstamp:")
            .descriptionAlignment(Pos.BASELINE_LEFT)
            .textVisible(true)
            .build();

        var motionA = TileBuilder.create()
            .skinType(SkinType.TEXT)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Alexandre Motion Detector")
            .text("TimeStamp when motion is detected")
            .description("Timstamp:")
            .descriptionAlignment(Pos.BASELINE_LEFT)
            .textVisible(true)
            .build();

        var motionK = TileBuilder.create()
            .skinType(SkinType.TEXT)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Katharina Motion Detector")
            .text("TimeStamp when motion is detected")
            .description("Timstamp:")
            .descriptionAlignment(Pos.BASELINE_LEFT)
            .textVisible(true)
            .build();

        var imageJ = TileBuilder.create()
            .skinType(SkinType.IMAGE)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Johnny Motion Sensor Image")
            .image(null)
            .imageMask(ImageMask.RECTANGULAR)
            .text("Image taken when motion is detected")
            .textAlignment(TextAlignment.CENTER)
            .build();

        var imageA = TileBuilder.create()
            .skinType(SkinType.IMAGE)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Alexandre Motion Sensor Image")
            .image(null)
            .imageMask(ImageMask.RECTANGULAR)
            .text("Image taken when motion is detected")
            .textAlignment(TextAlignment.CENTER)
            .build();

        var imageK = TileBuilder.create()
            .skinType(SkinType.IMAGE)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Katharina Motion Sensor Image")
            .image(null)
            .imageMask(ImageMask.RECTANGULAR)
            .text("Image taken when motion is detected")
            .textAlignment(TextAlignment.CENTER)
            .build();

        // //Setup a tile with an exit button to end the application
        // var exitButton = new Button("Exit");

        // //Setup event handler for the exit button
        // exitButton.setOnAction(e -> endApplication());

        // var exitTile = TileBuilder.create()
        //     .skinType(Tile.SkinType.CUSTOM)
        //     .prefSize(PREF_WIDTH, PREF_HEIGHT)
        //     .textSize(Tile.TextSize.BIGGER)
        //     .title("Quit the application")
        //     .graphic(exitButton)
        //     .roundedCorners(false)
        //     .build();
        
        var tilesColumnTempHumid = new VBox(tempHumidJ, tempHumidA, tempHumidK);
        var tilesColumnBuzzer = new VBox(buzzerJ, buzzerA, buzzerK);
        var tilesColumnMotion = new VBox(motionJ, motionA, motionK);
        var tilesColumnImage = new VBox(imageJ, imageA, imageK);

        // var tilesColumnK = new VBox(tempHumidK);
        tilesColumnTempHumid.setMinWidth(PREF_WIDTH);
        tilesColumnTempHumid.setSpacing(5);

        tilesColumnBuzzer.setMinWidth(PREF_WIDTH);
        tilesColumnBuzzer.setSpacing(5);

        tilesColumnMotion.setMinWidth(PREF_WIDTH);
        tilesColumnMotion.setSpacing(5);

        tilesColumnImage.setMinWidth(PREF_WIDTH);
        tilesColumnImage.setSpacing(5);

        this.getChildren().addAll(tilesColumnTempHumid, tilesColumnBuzzer, tilesColumnMotion, tilesColumnImage);
        this.setSpacing(5);
    }

    private void endApplication() {
        Platform.exit();
    }

}
